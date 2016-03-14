package com.etiennelawlor.imagegallery.library.models;

/**
 * Created by BX on 3/14/2016.
 */
public class ImageModel {
    public String externalUrl;
    public String localpath;
    public boolean isSelected;

    public ImageModel (String image) {
        this.externalUrl = image;
        isSelected = false;
    }
}
