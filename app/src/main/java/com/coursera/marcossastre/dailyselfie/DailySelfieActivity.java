package com.coursera.marcossastre.dailyselfie;

import android.content.Intent;
import android.graphics.Bitmap;
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

public class DailySelfieActivity extends AppCompatActivity {
    //Code for the cam request
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //TAG for Log
    private static final String TAG = "Lab-Graphics";


    private ImageView mPicCaptured;
    private String mCurrentPhotoPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_selfie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPicCaptured = (ImageView) findViewById(R.id.picCaptured);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //See the code of this function
                Log.i(TAG, "FOA clicked");
                dispatchTakePictureIntent();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.goto_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Set the returned Thumb to the ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {



        if(requestCode== REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            Log.i(TAG, "Intent returned with result OK");
            Bundle extras = data.getExtras();
            Bitmap returnedImage = (Bitmap) extras.get("data");
            mPicCaptured.setImageBitmap(returnedImage);
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
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                       // Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.i(TAG, "Intent delivered");
            }



        }
    }

    //Method to create a collision-resistant file name and save it
    private File createImage() throws IOException{
        //Creates an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = new File(
                storageDir /*directory*/,
                imageFileName /*prefix*/
                 + ".jpg"/*sufix*/

        );

        //Saves a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        Log.i(TAG, "File created");
        return image;

    }
}
