package com.etiennelawlor.imagegallery.library.events;

import com.etiennelawlor.imagegallery.library.models.ImageModel;

import java.util.ArrayList;

/**
 * Created by BX on 3/15/2016.
 */
public class FinishedSelectionEvent {
    public ArrayList<ImageModel> selectedPhotos;
    public FinishedSelectionEvent(ArrayList<ImageModel> selected) {
        this.selectedPhotos = selected;
    }
}
