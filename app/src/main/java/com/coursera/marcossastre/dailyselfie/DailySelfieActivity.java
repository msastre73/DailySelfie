package com.coursera.marcossastre.dailyselfie;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class DailySelfieActivity extends ListActivity{
    //Code for the cam request
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //Folder name where pics are saved in the sd
    static final String DIR_NAME = "DailySelfie_ByMarcos";
    //TAG for Log
    private static final String TAG = "Lab-Graphics";


    private SelfieListAdapter mSelfieAdapter;
    //file created when the FloatinActionButton is clicked, then passes to the SelfiItem
    //created in onActivityResult
    private File mCurrentFile;
    private String mCurrentFilePath;
    private float mLayoutHeigt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);
        mSelfieAdapter = new SelfieListAdapter(getApplicationContext());


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //See the code of this function
                Log.i(TAG, "FOA clicked");
                dispatchTakePictureIntent();

            }
        });

        getListView().setAdapter(mSelfieAdapter);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.delte_all) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        if(requestCode== REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.i(TAG, "Intent returned with result OK");

            //Creates the image for the thumb from the saved pic
            //174 is the width and height of the thumb provided by the cam
            Bitmap returnedImage = decodeSampledBitmapFromFile(mCurrentFilePath, 174, 174);
            //When I create the Bitmap thumb it cames rotated (I don't know why)
            //So I have to rotate it back
            Matrix matrix = new Matrix();
            matrix.postRotate(-90f);
            returnedImage = Bitmap.createBitmap(returnedImage,0,0,
                    returnedImage.getWidth(),
                    returnedImage.getHeight(),
                    matrix, false);

            //Creates a Time Stamp for the selfie's title
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            //Creates a new SelfieItem with the previous bitmap and the current file
            SelfieItem newItem = new SelfieItem(mCurrentFile, returnedImage, timeStamp);

            //Add the new item to the list adapter
            mSelfieAdapter.add(newItem);


        }
    }

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
                Log.i(TAG, "Image created");
            } catch (IOException ex){
                System.out.println("IOException occurred:"+ ex.toString());
            }
            //Continue only if the file was successfully created
            if(photoFile != null){
                //Saves the file to pass it later at onActivityResult
                mCurrentFile = photoFile;
                //Saves the file's path to create later the thumb at onActivityResult
                mCurrentFilePath = photoFile.getAbsolutePath();
                //Put Extra in the Intent to save the full-size image
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.i(TAG, "Intent delivered");
            }



        }
    }

    //Method to create a collision-resistant file
    private File createImage() throws IOException{
        //Creates an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //Creates a dedicated folder to store the selfies
        //imageFileName = "DailySelfie_byMarcos" + File.separator + imageFileName;
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

    //Method to created a simpled Bitmap from a File. It is used to create the thumbnails
    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight){

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
}
