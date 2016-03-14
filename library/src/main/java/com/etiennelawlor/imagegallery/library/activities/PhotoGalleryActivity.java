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
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.adapters.PhotoGalleryAdapter;
import com.etiennelawlor.imagegallery.library.enums.PaletteColorType;
import com.etiennelawlor.imagegallery.library.events.ImageModelsEvent;
import com.etiennelawlor.imagegallery.library.events.MultipleSelectModeChanged;
import com.etiennelawlor.imagegallery.library.models.ImageModel;
import com.etiennelawlor.imagegallery.library.util.ImageGalleryUtils;
import com.etiennelawlor.imagegallery.library.view.GridSpacesItemDecoration;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class PhotoGalleryActivity extends AppCompatActivity implements PhotoGalleryAdapter.OnImageClickListener,
        LoaderManager.LoaderCallbacks<Cursor>{

    public static final String IMAGES = "images";
    public static final String PALETTE = "palette_color_type";
    public static final String ACTION_BACK = "action_back";

    // region Member Variables
    private ArrayList<ImageModel> mImages;
    private PaletteColorType mPaletteColorType;
    private String mContactName;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TextView textViewNumbers, textViewNavigation;
    private LinearLayout linearLayoutBack;
    // endregion
    Handler handler;
    LoaderManager loaderManager;
    PhotoGalleryAdapter mAdapter;

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_gallery);

        bindViews();

        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                //mImages = extras.getStringArrayList(IMAGES);
                mPaletteColorType = (PaletteColorType) extras.get(PALETTE);
                mContactName = extras.getString(ACTION_BACK);
                textViewNavigation.setText(mContactName);
            }
        }

        //setUpRecyclerView(null);
        handler = new Handler();
        loaderManager = getSupportLoaderManager();
        loaderManager.initLoader(1, null, this);
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

    // region Helper Methods
    private void bindViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewNavigation = (TextView)mToolbar.findViewById(R.id.navigation);
        textViewNumbers = (TextView)mToolbar.findViewById(R.id.numbers);
        textViewNumbers.setVisibility(View.GONE);
        linearLayoutBack = (LinearLayout)mToolbar.findViewById(R.id.back);
        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();
                finish();
            }
        });
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

        mAdapter = new PhotoGalleryAdapter(imagesModel);
        mAdapter.setOnImageClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }
    // endregion

    private void changeToolbar(boolean multipleSelected) {
        if (multipleSelected)
            mToolbar.setBackgroundColor(Color.BLUE);
        else
            mToolbar.setBackgroundColor(Color.BLACK);
    }

    public void onEvent(ImageModelsEvent event) {
        setUpRecyclerView(event.images);
    }

    public void onEvent(MultipleSelectModeChanged event) {
        changeToolbar(event.isMultipleSelected);
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
        final ArrayList<ImageModel> galleryList = new ArrayList<ImageModel>();
        try {

            if (data != null && data.moveToFirst()) {
                while (data.moveToNext()) {
                    ImageModel item = new ImageModel("");

                    int dataColumnIndex = data
                            .getColumnIndex(MediaStore.Images.Media.DATA);

                    item.localpath = data.getString(dataColumnIndex);
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
                        //mAdapter.addAll(galleryList);
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
