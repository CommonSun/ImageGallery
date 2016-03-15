
/*
 * PhotoGalleryActivity.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 3/14/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.adapters.PhotoGalleryAdapter;
import com.etiennelawlor.imagegallery.library.events.FinishedSelectionEvent;
import com.etiennelawlor.imagegallery.library.events.ImageLongClickEvent;
import com.etiennelawlor.imagegallery.library.events.ImageModelsEvent;
import com.etiennelawlor.imagegallery.library.events.ImageTapEvent;
import com.etiennelawlor.imagegallery.library.models.ImageModel;
import com.etiennelawlor.imagegallery.library.util.ImageGalleryUtils;
import com.etiennelawlor.imagegallery.library.view.GridSpacesItemDecoration;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class PhotoGalleryActivity extends AppCompatActivity implements PhotoGalleryAdapter.OnImageClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String IMAGES_SELECTED_EXTRA = "images_selected";
    public static final String ACTION_BACK_EXTRA = "action_back";
    public static final String MULTIPLE_SELECT_EXTRA = "multiple_select";
    public static final String CAPTION_EXTRA = "caption_text";
    public static final String ACTION_NEXT_EXTRA = "action_next";

    // region Member Variables
    private String mBackAction;
    private String mNextAction;
    private String mCaption;
    PhotoGalleryAdapter mAdapter;
    private boolean mMultipleSelect;
    private ArrayList<String> mSelectedPhotos;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TextView textViewNumbers, textViewNavigation, textViewCaption, textViewNextAction;
    private LinearLayout linearLayoutBack;
    // endregion
    Handler handler;
    LoaderManager loaderManager;

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_gallery);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mBackAction = extras.getString(ACTION_BACK_EXTRA);
                mMultipleSelect = extras.getBoolean(MULTIPLE_SELECT_EXTRA);
                mCaption = extras.getString(CAPTION_EXTRA);
                mNextAction = extras.getString(ACTION_NEXT_EXTRA);
                // for pre-selected images by Media id
                try {
                    mSelectedPhotos = extras.getStringArrayList(IMAGES_SELECTED_EXTRA);
                } catch (Exception e) {
                    Log.e("tag", "exception !!");
                }
            }
        }
        init();
    }
    // endregion


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onStop(){
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //setUpRecyclerView();
    }

    // region ImageGalleryAdapter.OnImageClickListener Methods
    @Override
    public void onImageClick(int position) {
        /*
        Intent intent = new Intent(ImageGalleryActivity.this, FullScreenImageGalleryActivity.class);
        intent.putStringArrayListExtra(FullScreenImageGalleryActivity.IMAGES, mImages);
        intent.putExtra(FullScreenImageGalleryActivity.POSITION, position);
        if (mPaletteColorType != null) {
            intent.putExtra(FullScreenImageGalleryActivity.PALETTE, mPaletteColorType);
        }
        intent.putExtra(FullScreenImageGalleryActivity.ACTION_BACK, mContactName);
        intent.putExtra(FullScreenImageGalleryActivity.FROM_GALLERY, true);

        startActivity(intent);
        */
    }
    // endregion

    private void multipleSelectChange(Boolean isOn) {
        if (isOn == null) {
            mMultipleSelect = !mMultipleSelect;
        } else
            mMultipleSelect = isOn;

        textViewNumbers.setText("");
        if (mMultipleSelect) {
            textViewNextAction.setVisibility(View.VISIBLE);
            textViewNumbers.setVisibility(View.VISIBLE);
            mToolbar.setBackgroundColor(Color.BLUE);
        } else {
            textViewNextAction.setVisibility(View.GONE);
            textViewNumbers.setVisibility(View.GONE);
            mToolbar.setBackgroundColor(Color.BLACK);
            if (mAdapter != null) {
                mAdapter.deselectAll();
                mSelectedPhotos.clear();
            }
        }
    }

    private void numberSelected(ArrayList<ImageModel> imagesModel) {
        int sizeSelected = mSelectedPhotos.size();
        int sizeAdapter = -1;

        if (mAdapter != null && mAdapter.getItemCount() > 0)
            sizeAdapter = mAdapter.getItemCount();
        else if (imagesModel != null && imagesModel.size() > 0) {
            sizeAdapter = imagesModel.size();
        }

        if (sizeAdapter > 0) {
            textViewNumbers.setText(String.valueOf(sizeSelected) +
                    " of " + String.valueOf(sizeAdapter));
        } else {
            textViewNumbers.setText("" + sizeSelected);
        }
    }

    private void init() {
        bindViews();
        handler = new Handler();
        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(1, null, this);
        textViewNavigation.setText(mBackAction);
        textViewCaption.setText(mCaption);
        textViewNextAction.setText(mNextAction);
        textViewNextAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextAction();
            }
        });

        textViewNumbers.setText("");
        if (mSelectedPhotos != null && mSelectedPhotos.size() > 0) {
            numberSelected(null);
            mMultipleSelect = true;
        }
        multipleSelectChange(mMultipleSelect);
        setSupportActionBar(mToolbar);
    }

    // region Helper Methods
    private void bindViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewNavigation = (TextView)mToolbar.findViewById(R.id.navigation);
        textViewCaption = (TextView)mToolbar.findViewById(R.id.caption);
        textViewNextAction = (TextView)mToolbar.findViewById(R.id.nextAction);
        textViewNumbers = (TextView)mToolbar.findViewById(R.id.numbers);
        textViewNumbers.setVisibility(View.GONE);
        linearLayoutBack = (LinearLayout)mToolbar.findViewById(R.id.back);
        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private boolean findById(long id) {
        String sId = String.valueOf(id);
        for (String selectedId : mSelectedPhotos) {
            if (sId.equalsIgnoreCase(selectedId))
                return true;
        }
        return false;
    }

    private void selectPreSelected(ArrayList<ImageModel> imagesModel) {
        if (mSelectedPhotos != null && mSelectedPhotos.size() > 0) {
            for (ImageModel model : imagesModel) {
                if (findById(model.id))
                    model.isSelected = true;
            }
        }
        numberSelected(imagesModel);
    }

    private void setUpRecyclerView(ArrayList<ImageModel> imagesModel) {
        int numOfColumns;
        if (ImageGalleryUtils.isInLandscapeMode(this)) {
            numOfColumns = 4;
        } else {
            numOfColumns = 3;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(PhotoGalleryActivity.this, numOfColumns));
        mRecyclerView.addItemDecoration(new GridSpacesItemDecoration(ImageGalleryUtils.dp2px(this, 2), numOfColumns));

        selectPreSelected(imagesModel);
        mAdapter = new PhotoGalleryAdapter(imagesModel, mMultipleSelect);
        mAdapter.setOnImageClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }
    // endregion

    public void onEvent(ImageModelsEvent event) {
        setUpRecyclerView(event.images);
    }

    public void onEvent(ImageLongClickEvent event) {
        multipleSelectChange(null);
    }

    public void onEvent(ImageTapEvent event) {
        if (mSelectedPhotos == null) {
            mSelectedPhotos = new ArrayList<>();
        }
        ImageModel model = event.model;
        if (model.isSelected)
            mSelectedPhotos.add(String.valueOf(event.model.id));
        else {
            int i = 0;
            boolean toDelete = false;
            String modelId = String.valueOf(model.id);
            for (String id : mSelectedPhotos) {
                if (id.equalsIgnoreCase(modelId)) {
                    toDelete = true;
                    break;
                }
                i++;
            }
            if (toDelete == true)
                mSelectedPhotos.remove(i);
        }
        numberSelected(null);
    }

    private void nextAction() {
        if(mSelectedPhotos.size() == 0)
            finish();
        ArrayList<ImageModel> selectedModels = new ArrayList<>(mSelectedPhotos.size());
        ArrayList<ImageModel> models = mAdapter.getItems();
        for (ImageModel imageModel : models) {
            if (imageModel.isSelected)
                selectedModels.add(imageModel);
        }

        FinishedSelectionEvent event = new FinishedSelectionEvent(selectedModels);
        EventBus.getDefault().postSticky(event);
        finish();
    }

    private void checkImageStatus() {
        /*
        if (adapter.isEmpty()) {
            imgNoMedia.setVisibility(View.VISIBLE);
        } else {
            imgNoMedia.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media._ID;
        return new CursorLoader(this, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final ArrayList<ImageModel> galleryList = new ArrayList<>();
        try {

            if (data != null && data.moveToFirst()) {
                while (data.moveToNext()) {
                    int dataColumnIndex = data
                            .getColumnIndex(MediaStore.Images.Media.DATA);
                    int idColumnIndex = data
                            .getColumnIndex(MediaStore.Images.Media._ID);
                    ImageModel item = new ImageModel(data.getString(dataColumnIndex),
                                                     data.getLong(idColumnIndex));
                    galleryList.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread() {

            @Override
            public void run() {
                Looper.prepare();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        setUpRecyclerView(galleryList);
                        checkImageStatus();
                    }
                });
                Looper.loop();
            };

        }.start();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

}
