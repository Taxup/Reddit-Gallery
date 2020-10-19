package com.takhir.redditgallery.models;

import android.widget.ImageView;

public class ImageCard {
    private String title;
    private ImageView image;

    public ImageCard(String title, ImageView image) {
        this.title = title;
        this.image = image;
    }

    public ImageCard() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "ImageCard{" +
                "title='" + title + '\'' +
                ", content='" + image + '\'' +
                '}';
    }
}
