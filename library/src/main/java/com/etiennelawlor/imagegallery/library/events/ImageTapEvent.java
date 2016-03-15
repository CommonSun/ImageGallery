
/*
 * ImageTapEvent.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 3/14/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */


package com.etiennelawlor.imagegallery.library.events;

import com.etiennelawlor.imagegallery.library.models.ImageModel;

public class ImageTapEvent {
    public ImageModel model;
    public ImageTapEvent(ImageModel model) {
        this.model = model;
    }
}
