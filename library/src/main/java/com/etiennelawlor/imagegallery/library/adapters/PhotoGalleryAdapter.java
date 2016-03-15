
/*
 * PhotoGalleryAdapter.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 3/14/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.events.ImageLongClickEvent;
import com.etiennelawlor.imagegallery.library.events.ImageTapEvent;
import com.etiennelawlor.imagegallery.library.models.ImageModel;
import com.etiennelawlor.imagegallery.library.util.ImageGalleryUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class PhotoGalleryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // region Member Variables
    private final List<ImageModel> mImagesModelList;
    private int mGridItemWidth;
    private int mGridItemHeight;
    private OnImageClickListener mOnImageClickListener;
    private boolean multiplePicking;
    // endregion

    // region Interfaces
    public interface OnImageClickListener {
        void onImageClick(int position);
    }
    // endregion

    // region Constructors
    public PhotoGalleryAdapter(List<ImageModel> images, boolean multiplePick) {
        mImagesModelList = images;
        multiplePicking = multiplePick;
    }
    // endregion

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photo_thumbnail, viewGroup, false);
        v.setLayoutParams(getGridItemLayoutParams(v));

        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final ImageViewHolder holder = (ImageViewHolder) viewHolder;

        ImageModel item = mImagesModelList.get(position);
        String image;
        if (!TextUtils.isEmpty(item.externalUrl)) {
            image = item.externalUrl;
            setUpImage(holder.mImageView, image);
        } else {
            image = item.localPath;
            File file = new File(image);
            setUpLocalImage(holder.mImageView, file);
        }

        if (item.isSelected)
            holder.mSelected.setVisibility(View.VISIBLE);
        else
            holder.mSelected.setVisibility(View.GONE);

        holder.mFrameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPos = holder.getAdapterPosition();
                ImageModel item = mImagesModelList.get(adapterPos);
                if (multiplePicking) {
                    if (item != null) {
                        item.isSelected = !item.isSelected;
                        notifyDataSetChanged();
                        ImageTapEvent event = new ImageTapEvent(item);
                        EventBus.getDefault().post(event);
                    }
                } else {  // single pick -> do some action
                    Log.d("tag", "not multiple picking");
                    if (adapterPos != RecyclerView.NO_POSITION) {
                        if (mOnImageClickListener != null) {
                            mOnImageClickListener.onImageClick(adapterPos);
                        }
                    }
                }
            }
        });

        holder.mFrameLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int adapterPos = holder.getAdapterPosition();
                ImageLongClickEvent event = new ImageLongClickEvent(adapterPos);
                EventBus.getDefault().post(event);
                multiplePicking = !multiplePicking;
                return false;
            }
        });
    }

    public void deselectAll() {
        for (ImageModel model : mImagesModelList) {
            if (model.isSelected) {
                model.isSelected = false;
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mImagesModelList != null) {
            return mImagesModelList.size();
        } else {
            return 0;
        }
    }

    // region Helper Methods
    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mOnImageClickListener = listener;
    }

    private ViewGroup.LayoutParams getGridItemLayoutParams(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        int screenWidth = ImageGalleryUtils.getScreenWidth(view.getContext());
        int numOfColumns;
        if (ImageGalleryUtils.isInLandscapeMode(view.getContext())) {
            numOfColumns = 4;
        } else {
            numOfColumns = 3;
        }

        mGridItemWidth = screenWidth / numOfColumns;
        mGridItemHeight = screenWidth / numOfColumns;

        layoutParams.width = mGridItemWidth;
        layoutParams.height = mGridItemHeight;

        return layoutParams;
    }

    private void setUpImage(ImageView iv, String imageUrl) {
        if (!TextUtils.isEmpty(imageUrl)) {
            Picasso.with(iv.getContext())
                    .load(imageUrl)
                    .resize(mGridItemWidth, mGridItemHeight)
                    .centerCrop()
                    .into(iv);
        } else {
            iv.setImageDrawable(null);
        }
    }

    private void setUpLocalImage(ImageView iv, File imageFile) {
        if (imageFile != null) {
            Picasso.with(iv.getContext())
                    .load(imageFile)
                    .resize(mGridItemWidth, mGridItemHeight)
                    .centerCrop()
                    .into(iv);
        } else {
            iv.setImageDrawable(null);
        }
    }

    public void addAll(ArrayList<ImageModel> files) {

        try {
            this.mImagesModelList.clear();
            this.mImagesModelList.addAll(files);

        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDataSetChanged();
    }

    // endregion

    // region Inner Classes

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        // region Member Variables
        private final ImageView mImageView;
        private final FrameLayout mFrameLayout;
        private final ImageView mSelected;
        // endregion

        // region Constructors
        public ImageViewHolder(final View view) {
            super(view);

            mImageView = (ImageView) view.findViewById(R.id.iv);
            mFrameLayout = (FrameLayout) view.findViewById(R.id.fl);
            mSelected = (ImageView) view.findViewById(R.id.selected);
        }
        // endregion
    }

    public ArrayList<ImageModel> getItems() {
        return (ArrayList<ImageModel>) mImagesModelList;
    }

    // endregion
}
