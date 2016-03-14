package com.etiennelawlor.imagegallery.library.events;

import com.etiennelawlor.imagegallery.library.models.ImageModel;

import java.util.ArrayList;

/**
 * Created by BX on 3/14/2016.
 */
public class ImageModelsEvent {
    public ArrayList<ImageModel> images;

    public ImageModelsEvent(ArrayList<ImageModel> allimages) {
        this.images = allimages;
    }
}
