
/*
 * AnnotatedImageListAdapter.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 6/4/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.models.ImageModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AnnotatedImageListAdapter extends RecyclerView.Adapter<AnnotatedImageListRowHolder>  {
    public static final String IMAGE_IS_PLUS = "plus";

    private List<ImageModel> mediumList;
    private Context mContext;
    private int imageSizeInPixel;
    private Drawable plusDrawable;

    public AnnotatedImageListAdapter(Context context, List<ImageModel> mediumList, int imageSizeInPixel) {
        this.mediumList = mediumList;
        this.mContext = context;
        this.imageSizeInPixel = imageSizeInPixel;
        this.plusDrawable = context.getResources().getDrawable(R.drawable.icon_plus);
        mediumList.add(getPlusImage());
    }

    @Override
    public AnnotatedImageListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.selected_image_recycle_view_row, null);
        AnnotatedImageListRowHolder mh = new AnnotatedImageListRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(AnnotatedImageListRowHolder feedListRowHolder, int i) {
        ImageModel oneMedium = mediumList.get(i);

        if (oneMedium.localPath.equals(IMAGE_IS_PLUS)) {
            feedListRowHolder.thumbnail.setImageDrawable(plusDrawable);
        } else if (!TextUtils.isEmpty(oneMedium.getMediaUrl())) {
            File file = new File(oneMedium.getMediaUrl());
            Picasso.with(mContext)
                    .load(file)
                    .fit()
                    .centerCrop()
                    .into(feedListRowHolder.thumbnail);
        }

        if (!TextUtils.isEmpty(oneMedium.getAnnotation())) {
            feedListRowHolder.annotation.setVisibility(View.VISIBLE);
            feedListRowHolder.annotation.setText(oneMedium.getAnnotation());
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)feedListRowHolder.annotation.getLayoutParams();
            params.topMargin = (int) (imageSizeInPixel * oneMedium.getYPositionYPercent() / 100f);
        } else {
            feedListRowHolder.annotation.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return (null != mediumList ? mediumList.size() : 0);
    }

    public ImageModel getItem(int pos) {
        if (pos < mediumList.size())
            return mediumList.get(pos);
        return null;
    }

    public List<ImageModel> getMediumList() {
        return mediumList;
    }

    public int removePlusImage() {
        int lastPos = mediumList.size()-1;
        mediumList.remove(lastPos);
        return lastPos;
    }

    private ImageModel getPlusImage() {
        ImageModel imageModel = new ImageModel(IMAGE_IS_PLUS, 0);
        return imageModel;
    }

    private boolean findById(long id, List<ImageModel> aList) {
        for (ImageModel imageModel : aList) {
            if (id  == imageModel.mediaId)
                return true;
        }
        return false;
    }

    public boolean addImages(List<ImageModel> moreImages) {
        int lastPosition = removePlusImage();
        int added = 0;
        notifyItemRemoved(lastPosition);

        // Adding images
        for (ImageModel imageModel : moreImages) {
            if (!findById(imageModel.mediaId, mediumList)) {
                mediumList.add(imageModel);
                added++;
            }
        }
        notifyItemRangeInserted(lastPosition, added);

        // Removing images
        boolean wasRemove = false;
        for (int i=0; i<mediumList.size(); i++) {
            ImageModel imageModel = mediumList.get(i);
            if (!findById(imageModel.mediaId, moreImages)) {
                mediumList.remove(i);
                notifyItemRemoved(i);
                wasRemove = true;
            }
        }

        // add plus
        mediumList.add(getPlusImage());
        notifyItemInserted(mediumList.size()-1);

        return wasRemove;
    }

    public ArrayList<String> getMediaIds() {
        ArrayList<String> toReturn = new ArrayList<>(mediumList.size());
        for (int i=0; i < mediumList.size()-1; i++) {
            ImageModel oneMedium  = mediumList.get(i);
            toReturn.add(String.valueOf(oneMedium.mediaId));
        }
        return toReturn;
    }

}
