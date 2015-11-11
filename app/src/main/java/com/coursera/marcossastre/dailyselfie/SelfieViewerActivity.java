package com.coursera.marcossastre.dailyselfie;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

/**
 * Created by Marcos Sastre on 10/11/2015.
 */


public class SelfieViewerActivity extends Activity {

    private ImageView mImageView;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sefie_viewer);
        mImageView = (ImageView) findViewById(R.id.imageView);

        //Get the intent that started this Activity
        Intent intentReceived = getIntent();
        //Get the file put on the extra and convert it to a bitmap
        String picToShowPath = (String) intentReceived.getExtras().get(DailySelfieActivity.PIC_TO_SHOW_KEY);
        Bitmap bitmapPic = BitmapFactory.decodeFile(picToShowPath);

        mImageView.setImageBitmap(bitmapPic);


    }

}
