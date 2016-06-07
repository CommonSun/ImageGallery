
/*
 * AnnotatedImageListRowHolder.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 6/3/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.events.AnnotatingImageTapEvent;

import org.greenrobot.eventbus.EventBus;

public class AnnotatedImageListRowHolder extends RecyclerView.ViewHolder {
    protected ImageView thumbnail;
    protected TextView annotation;

    public AnnotatedImageListRowHolder(View view) {
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new AnnotatingImageTapEvent(getAdapterPosition()));
            }
        });
        this.annotation = (TextView) view.findViewById(R.id.annotationText);
    }

}
