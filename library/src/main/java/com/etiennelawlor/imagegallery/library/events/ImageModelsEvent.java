
/*
 * ImageModelsEvent.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 3/14/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.events;

import com.etiennelawlor.imagegallery.library.models.ImageModel;

import java.util.ArrayList;

public class ImageModelsEvent {
    public ArrayList<ImageModel> images;

    public ImageModelsEvent(ArrayList<ImageModel> allimages) {
        this.images = allimages;
    }
}
