
/*
 * ImageModel.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 3/14/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

public class ImageModel implements Parcelable {
    public String externalUrl;
    public String localPath;
    public boolean isSelected;
    public long mediaId; // from Media db

    // extending it for annotation
    private String annotation;
    private int yPositionPercent;
    private float annotationToHeightScale;

    public ImageModel (String image, long id) {
        this.localPath = image;
        this.mediaId = id;
        isSelected = false;
    }

    public ImageModel clone() {
        ImageModel newModel = new ImageModel(this.localPath, this.mediaId);
        newModel.isSelected = this.isSelected;
        if (!TextUtils.isEmpty(externalUrl))
            newModel.externalUrl = new String(externalUrl);
        if (!TextUtils.isEmpty(annotation))
            newModel.annotation = new String(annotation);
        newModel.yPositionPercent = this.yPositionPercent;
        return newModel;
    }


    protected ImageModel(Parcel in) {
        externalUrl = in.readString();
        localPath = in.readString();
        isSelected = in.readByte() != 0x00;
        mediaId = in.readLong();
        annotation = in.readString();
        yPositionPercent = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(externalUrl);
        dest.writeString(localPath);
        dest.writeByte((byte) (isSelected ? 0x01 : 0x00));
        dest.writeLong(mediaId);
        dest.writeString(annotation);
        dest.writeInt(yPositionPercent);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ImageModel> CREATOR = new Parcelable.Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };


    public String getUuid() {
        if (TextUtils.isEmpty(localPath))
            return null;
        int i = localPath.lastIndexOf("/");
        if (i != -1)
            return localPath.substring(i+1);

        return null;
    }

    public void setMediaUrl(String local) {
        this.localPath = local;
    }

    public String getMediaUrl() {
        return localPath;
    }

    public void setAnnotation(String anno, int positionYPercent, float scale) {
        this.annotation = anno;
        this.yPositionPercent = positionYPercent;
        this.annotationToHeightScale = scale;
    }

    public String getAnnotation() {
        return this.annotation;
    }

    public  void setYPositionYPercent(int yPercent) {
        this.yPositionPercent = yPercent;
    }

    public int getYPositionYPercent() {
        return this.yPositionPercent;
    }

    public float getAnnotationToHeightScale() {
        return this.annotationToHeightScale;
    }

    /** traverse all selected images for annotation text **/
    static public boolean hasAnnotation(List<ImageModel> selectedPhotos) {
        if (selectedPhotos != null && selectedPhotos.size() > 0) {
            for (ImageModel imageModel : selectedPhotos) {
                if (!TextUtils.isEmpty(imageModel.annotation))
                    return true;
            }
        }
        return false;
    }

}
