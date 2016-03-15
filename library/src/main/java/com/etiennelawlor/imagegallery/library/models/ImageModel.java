package com.etiennelawlor.imagegallery.library.models;

/**
 * Created by BX on 3/14/2016.
 */
public class ImageModel {
    public String externalUrl;
    public String localPath;
    public boolean isSelected;
    public long id; // from Media db

    public ImageModel (String image, long id) {
        this.localPath = image;
        this.id = id;
        isSelected = false;
    }
}
