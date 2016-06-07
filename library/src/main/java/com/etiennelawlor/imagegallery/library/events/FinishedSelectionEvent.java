
/*
 * FinishedSelectionEvent.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 3/14/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.events;

import com.etiennelawlor.imagegallery.library.models.ImageModel;

import java.util.List;

public class FinishedSelectionEvent {
    public String source;
    public List<ImageModel> selectedPhotos;
    public String galleryId;
    public FinishedSelectionEvent(List<ImageModel> selected, String source) {
        this.selectedPhotos = selected;
        this.source = source;
    }
}
