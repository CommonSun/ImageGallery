package com.etiennelawlor.imagegallery.library.events;

import com.etiennelawlor.imagegallery.library.models.ImageModel;

/**
 * Created by BX on 3/15/2016.
 */
public class ImageTapEvent {
    public ImageModel model;
    public ImageTapEvent(ImageModel model) {
        this.model = model;
    }
}
