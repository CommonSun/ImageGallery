package com.etiennelawlor.imagegallery.library.events;

/**
 * Created by BX on 3/14/2016.
 */
public class MultipleSelectModeChanged {
    public boolean isMultipleSelected;
    public MultipleSelectModeChanged(boolean selected) {
        this.isMultipleSelected = selected;
    }
}
