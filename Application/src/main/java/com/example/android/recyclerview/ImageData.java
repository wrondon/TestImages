package com.example.android.recyclerview;

import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * Created by william on 3/25/15.
 */
public class ImageData {
    private String imageUrl;

    private ImageView image;

    private int imageId;

    private Bitmap bitmap;


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ImageData(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ImageData(String imageUrl, int imageId) {
        this.imageUrl = imageUrl;
        this.imageId = imageId;
    }

    public ImageData(Bitmap bitmap) {
        this.imageUrl = imageUrl;
        this.bitmap = bitmap;
    }
}
