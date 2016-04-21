
/*
 * ImagesRemovedEvent.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 3/16/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.events;

import java.util.List;

public class ImagesRemovedEvent {
    public List<String> images;
    public ImagesRemovedEvent(List<String> images) {
        this.images = images;
    }
}
