package com.coursera.marcossastre.dailyselfie;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Marcos Sastre on 09/11/2015.
 */
public class SelfieItem {
    //Thumbnail for the list
    private Bitmap thumb;
    //Title to display
    private String title;
    //Path to the full image saved by the cam
    private String imagePath;


    //Constructor
     public SelfieItem(String fullImage, Bitmap thumb, String title) {
        this.imagePath = fullImage;
        this.thumb = thumb;
        this.title = title;
    }

    //Getter and Setters
  public String getFullImagePath() {
        return imagePath;
    }

    public void setFullImagePath(String fullImage) {
        this.imagePath = fullImage;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
