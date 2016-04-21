package com.etiennelawlor.imagegallery.library.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.adapters.ImageGalleryAdapter;
import com.etiennelawlor.imagegallery.library.enums.PaletteColorType;
import com.etiennelawlor.imagegallery.library.util.ImageGalleryUtils;
import com.etiennelawlor.imagegallery.library.view.GridSpacesItemDecoration;

import java.util.ArrayList;

public class ImageGalleryActivity extends AppCompatActivity implements ImageGalleryAdapter.OnImageClickListener {

    public static final String IMAGES = "images";
    public static final String PALETTE = "palette_color_type";
    public static final String ACTION_BACK = "action_back";
    public static final String ACTION_CAPTION = "action_caption";
    public static final String ACTION_NEXT = "action_next";


    // region Member Variables
    private ArrayList<String> mImages;
    private PaletteColorType mPaletteColorType;

    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private TextView textViewNumbers, textViewNavigation, textViewNext, textViewCaption;
    private LinearLayout linearLayoutBack;
    // endregion

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
                mImages = extras.getStringArrayList(IMAGES);
                mPaletteColorType = (PaletteColorType) extras.get(PALETTE);
                String backNavigationText = extras.getString(ACTION_BACK);
                textViewNavigation.setText(backNavigationText);
                try {
                    String nextAction = extras.getString(ACTION_NEXT);
                    textViewNext.setText(nextAction);

                    String caption = extras.getString(ACTION_CAPTION);
                    textViewCaption.setText(caption);
                } catch (Exception e) {

                }
            }
        }

        setUpRecyclerView();
    }
    // endregion

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
        setUpRecyclerView();
    }

    // region ImageGalleryAdapter.OnImageClickListener Methods
    @Override
    public void onImageClick(int position) {
        Intent intent = new Intent(ImageGalleryActivity.this, FullScreenImageGalleryActivity.class);

        intent.putStringArrayListExtra(FullScreenImageGalleryActivity.IMAGES, mImages);
        intent.putExtra(FullScreenImageGalleryActivity.POSITION, position);
        if (mPaletteColorType != null) {
            intent.putExtra(FullScreenImageGalleryActivity.PALETTE, mPaletteColorType);
        }
        String caption = textViewCaption.getText().toString();
        intent.putExtra(FullScreenImageGalleryActivity.ACTION_BACK, caption);
        intent.putExtra(FullScreenImageGalleryActivity.FROM_GALLERY, true);

        startActivity(intent);
    }
    // endregion

    // region Helper Methods
    private void bindViews() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewNavigation = (TextView)mToolbar.findViewById(R.id.back);
        textViewNumbers = (TextView)mToolbar.findViewById(R.id.numbers);
        textViewNumbers.setVisibility(View.GONE);
        textViewNext = (TextView)mToolbar.findViewById(R.id.nextAction);
        textViewCaption = (TextView)mToolbar.findViewById(R.id.caption);
        linearLayoutBack = (LinearLayout)mToolbar.findViewById(R.id.backLayout);
        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUpRecyclerView() {
        int numOfColumns;
        if (ImageGalleryUtils.isInLandscapeMode(this)) {
            numOfColumns = 4;
        } else {
            numOfColumns = 3;
        }

        mRecyclerView.setLayoutManager(new GridLayoutManager(ImageGalleryActivity.this, numOfColumns));
        mRecyclerView.addItemDecoration(new GridSpacesItemDecoration(ImageGalleryUtils.dp2px(this, 2), numOfColumns));
        ImageGalleryAdapter imageGalleryAdapter = new ImageGalleryAdapter(mImages);
        imageGalleryAdapter.setOnImageClickListener(this);

        mRecyclerView.setAdapter(imageGalleryAdapter);
    }
    // endregion
}
