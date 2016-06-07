
/*
 * AnnotatingImageTapEvent.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 6/3/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */


package com.etiennelawlor.imagegallery.library.events;

import com.etiennelawlor.imagegallery.library.models.ImageModel;

public class AnnotatingImageTapEvent {
    public int position;
    public AnnotatingImageTapEvent(int pos) {
        this.position = pos;
    }
}
