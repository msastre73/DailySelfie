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
    private LinearLayout mParent;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sefie_viewer);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mParent = (LinearLayout) mImageView.getParent();

        //Set Layout params
        /*mParent.setLayoutParams(new ViewGroup.MarginLayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT));*/
        //Get the intent that started this Activity
        Intent intentReceived = getIntent();
        //Get the file put on the extra and convert it to a bitmap
        File picToShow = (File) intentReceived.getExtras().get(DailySelfieActivity.PIC_TO_SHOW_KEY);
        Bitmap bitmapPic = BitmapFactory.decodeFile(picToShow.getAbsolutePath());


        mImageView.setImageBitmap(bitmapPic);
        mImageView.setScaleType(ImageView.ScaleType.FIT_XY);

    }

}