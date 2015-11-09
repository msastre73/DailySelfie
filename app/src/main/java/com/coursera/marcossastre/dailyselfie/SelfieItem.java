package com.coursera.marcossastre.dailyselfie;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Marcos Sastre on 09/11/2015.
 */
public class SelfieItem {
    //Thumbnail returned from the cam
    private Bitmap thumb;
    //Title to display
    private String title;
    //Path to the full image saved by the cam
    private File fullImage;


    //Constructor
     public SelfieItem(File fullImage, Bitmap thumb, String title) {
        this.fullImage = fullImage;
        this.thumb = thumb;
        this.title = title;
    }

    //Getter and Setters
  public File getFullImage() {
        return fullImage;
    }

    public void setFullImage(File fullImage) {
        this.fullImage = fullImage;
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
