package com.takhir.redditgallery.models;

import android.widget.ImageView;

public class ImageCard {
    private String url;
    private ImageView image;

    public ImageCard(String url) {
        this.url = url;
    }

    public ImageCard() {
    }

    public ImageView getImage() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ImageCard{" +
                "url='" + url + '\'' +
                '}';
    }
}
