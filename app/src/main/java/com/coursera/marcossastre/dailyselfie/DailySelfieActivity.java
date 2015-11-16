package com.coursera.marcossastre.dailyselfie;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailySelfieActivity extends ListActivity{
    //File to save items (persistence)
    static final String FILE_NAME = "DailySelfieItems.txt";
    //Code for the cam request
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //Folder name where pics are saved in the sd
    static final String DIR_NAME = "DailySelfie_ByMarcos";
    //KEY used to pass the pic file to the SelfieViewerActivity
    static final String PIC_TO_SHOW_KEY = "picToShow";
    //KEY used to save the boolean checked to decide if show the initial text or not
    static final String SHOW_INITIAL_TEXT = "show initial text";
    //TAG for Log
    private static final String TAG = "DailySelfie - Main";

    private TextView mInitialText;
    private SelfieListAdapter mSelfieAdapter;
    private String mCurrentFilePath;

    private SharedPreferences prefs;
    private AlarmManager mAlarmManager;
    private final static long ALARM_DELAY = 2 * 60 * 1000L;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mSelfieAdapter = new SelfieListAdapter(getApplicationContext());

        getListView().setAdapter(mSelfieAdapter);
        mInitialText = (TextView) findViewById(R.id.initialText);

        //Loads the SelfieItems from previous sessions
        loadItems();

        //Gets a SharedPreferences object
        prefs = getPreferences(MODE_PRIVATE);

        //Checks if it is the first time the app is created, in that case shows initialText
         if(prefs.getBoolean(SHOW_INITIAL_TEXT,true)){
           mInitialText.setVisibility(View.VISIBLE);
        }




        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Intent to wrap in the Pending Intent of the Alarm
        final Intent mNotificationReceiverIntent = new Intent(DailySelfieActivity.this,
                TodayNotificationReceiver.class);

        //Checks if the Pending Intents exists (i.e the alarm is set), and if not creates it
        boolean isAlarmSet = (PendingIntent.getBroadcast(DailySelfieActivity.this, 0,
                mNotificationReceiverIntent, PendingIntent.FLAG_NO_CREATE) != null);
        if(!isAlarmSet) {
            scheduleDailyNotifications(mNotificationReceiverIntent);
            Log.i(TAG, "Alarm set");
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.delte_all) {
            mSelfieAdapter.clearAll();
            Log.i(TAG, "All selfies were deleted");
            return true;
        }else if(id==R.id.take_pic){
            Log.i(TAG, "Cam menu clicked");
            //Sends the Intent to the camera and sets the path to save the pic
            dispatchTakePictureIntent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Manages what happens when an item is clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "Item clicked at position: " + position);

        //Starts SelfieViewerActivity passing it the pic to show path
        Intent selfieViewerIntent = new Intent(this, SelfieViewerActivity.class);

        SelfieItem selfieClicked = (SelfieItem) getListView().getItemAtPosition(position);
        String picToShowPath = selfieClicked.getFullImagePath();

        selfieViewerIntent.putExtra(PIC_TO_SHOW_KEY, picToShowPath);

        startActivity(selfieViewerIntent);
        Log.i(TAG, "Intent Sent to display the full size image");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode== REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.i(TAG, "Intent returned with result OK");
            //Takes away the initial text view and set SharedPreferences in order to not show
            //it again when onCreate is called
            mInitialText.setVisibility(View.GONE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(SHOW_INITIAL_TEXT, false);
            editor.commit();

            //Reference to the pic saved in the path passed by the intent extra
            Bitmap savedPic = BitmapFactory.decodeFile(mCurrentFilePath);

            //Checks whether the pic is rotated , and fix it if necessary
            Bitmap savedPicValidated = imageOreintationValidator(savedPic, mCurrentFilePath);

            //Stores the validated bitmap
            storeImage(savedPicValidated, mCurrentFilePath);

            //Creates the thumb from the saved pic
            Bitmap thumb = decodeSampledBitmapFromFile(mCurrentFilePath, 174, 174);

             //Creates a Time Stamp for the selfie's title
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            //Creates a new SelfieItem
            SelfieItem newItem = new SelfieItem(mCurrentFilePath, thumb, timeStamp);

            //Add the new item to the list adapter
            mSelfieAdapter.add(newItem);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveItems();
    }

    //AUX METHODS
    //Method to invoke an intent to capture the pic passing it the path to save it
    private void dispatchTakePictureIntent() {
        //Creates the Intent to take a pic
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //If there is any activity (from a cam app) then it invokes it  for result
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Creates a file where the pic should go
            File photoFile = null;

            try{
                photoFile = createImage();
            } catch (IOException ex){
                System.out.println("IOException occurred:"+ ex.toString());
            }
            //Continue only if the file was successfully created
            if(photoFile != null){
                //Saves the file's path
                mCurrentFilePath = photoFile.getAbsolutePath();
                //Put Extra in the Intent to save the full-size image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.i(TAG, "Intent delivered");
            }



        }
    }

    //Method to create a collision-resistant file using a tempStamp
    private File createImage() throws IOException{
        //Creates an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Creates a dedicated folder to store selfies
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)+File.separator+DIR_NAME);
        storageDir.mkdirs();

        File image = new File(
                storageDir /*directory*/,
                imageFileName /* prefix*/
                 + ".jpg"/*sufix*/

        );

        Log.i(TAG, "File created");
        return image;

    }

    //Method to rotate back an image when it is rotated by the cam (this happen in some devices)
    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    //Method to rotate an image
    private Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError err) {
            err.printStackTrace();
        }
        return bitmap;
    }

    //Method to created a sampled Bitmap from a File. It is used to create the thumbnails
    public  Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight){

        //First decode with inJustDecodeBounds=true to check dimensions without actually getting the bitmap
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image (to resize the Bitmap)
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {

            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set, this time we do want the bitmap
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    //Method to store a bitmap in the designated path
    private void storeImage(Bitmap image, String path) {
    File pictureFile = new File(path);

    try {
        FileOutputStream fos = new FileOutputStream(pictureFile);
        image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.close();
    } catch (FileNotFoundException e) {
        Log.d(TAG, "File not found: " + e.getMessage());
    } catch (IOException e) {
        Log.d(TAG, "Error accessing file: " + e.getMessage());
    }

}
    //Method to set the repetitive Alarm to fire periodical notifications
    private void scheduleDailyNotifications(Intent notificationReceiverIntent){
        PendingIntent mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(
                DailySelfieActivity.this, 0, notificationReceiverIntent, 0);

        //Creates the repeating alarm
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + ALARM_DELAY,
                ALARM_DELAY, mNotificationReceiverPendingIntent);


    }


    //PERSISTENCE METHODS
    // Load stored SelfieItems
    private void loadItems() {
        BufferedReader reader = null;
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            reader = new BufferedReader(new InputStreamReader(fis));

            String imagePath = null;
            Bitmap thumb = null;
            String title = null;


            while (null != (imagePath = reader.readLine())) {
                thumb = decodeSampledBitmapFromFile(imagePath, 174, 174);
                title = reader.readLine();
                mSelfieAdapter.add(new SelfieItem(imagePath, thumb, title));

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Save SelfieItems to file
    private void saveItems() {
        PrintWriter writer = null;
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    fos)));

            for (int idx = 0; idx < mSelfieAdapter.getCount(); idx++) {

                writer.println(mSelfieAdapter.getItem(idx));

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != writer) {
                writer.close();
            }
        }
    }
}
