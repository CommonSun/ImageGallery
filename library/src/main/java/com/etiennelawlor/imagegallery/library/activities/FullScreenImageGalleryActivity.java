package com.etiennelawlor.imagegallery.library.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.adapters.FullScreenImageGalleryAdapter;
import com.etiennelawlor.imagegallery.library.enums.PaletteColorType;
import com.etiennelawlor.imagegallery.library.events.ImagesRemovedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class FullScreenImageGalleryActivity extends AppCompatActivity {

    public static final String IMAGES = "images";
    public static final String PALETTE = "palette_color_type";
    public static final String POSITION = "position";
    public static final String ACTION_BACK = "action_back";
    public static final String FROM_GALLERY = "from_gallery";
    public static final String SHOW_REMOVE = "show_remove";
    public static final String SHOW_DONE = "show_done";
    public static final String BACK_HERE = "back_here";

    // region Member Variables
    private List<String> mImages;
    private List<String> mRemovedImages;
    private int mPosition;
    private PaletteColorType mPaletteColorType;
    private String mName;
    private boolean mFromGallery;
    private int mCurrentposition;
    private boolean mdontGoBrowseGallery;

    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TextView textViewNumbers, textViewNavigation;
    private LinearLayout linearLayoutBack;
    private TextView textViewRemove;
    private TextView textViewDone;
    // endregion

    // region Listeners
    private final ViewPager.OnPageChangeListener mViewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(position);

                setActionBarTitle(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    // endregion

    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_full_screen_image_gallery);
        bindViews();
        setSupportActionBar(mToolbar);

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                mImages = extras.getStringArrayList(IMAGES);
                mPaletteColorType = (PaletteColorType) extras.get(PALETTE);
                mPosition = extras.getInt(POSITION);
                mName = extras.getString(ACTION_BACK);
                textViewNavigation.setText(mName);
                mFromGallery = extras.getBoolean(FROM_GALLERY);
                boolean showRemove = extras.getBoolean(SHOW_REMOVE);
                if (showRemove)
                    textViewRemove.setVisibility(View.VISIBLE);
                else
                    textViewRemove.setVisibility(View.GONE);
                boolean showDone = extras.getBoolean(SHOW_DONE);
                if (showDone)
                    textViewDone.setVisibility(View.VISIBLE);
                else
                    textViewDone.setText("         ");
                mdontGoBrowseGallery = extras.getBoolean(BACK_HERE);
            }
        }

        mRemovedImages = new ArrayList<>();
        setUpViewPager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeListeners();
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

    // region Helper Methods
    private void bindViews() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewNumbers = (TextView)mToolbar.findViewById(R.id.numbers);
        textViewNavigation = (TextView)mToolbar.findViewById(R.id.back);
        linearLayoutBack = (LinearLayout)mToolbar.findViewById(R.id.backLayout);
        linearLayoutBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mdontGoBrowseGallery)
                    goToGallery();
                else
                    finish();
            }
        });
        textViewRemove = (TextView) findViewById(R.id.caption);
        textViewRemove.setVisibility(View.GONE);
        textViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPosition = getNextPosition(mCurrentposition);
                mRemovedImages.add(mImages.get(mCurrentposition));
                mImages.remove(mCurrentposition);
                setUpViewPager();
                textViewDone.setVisibility(View.VISIBLE);
            }
        });

        textViewDone = (TextView) findViewById(R.id.nextAction);
        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDone();
            }
        });
    }

    private int getNextPosition(int position) {
        position--;
        if (position < 0)
            position = 0;
        return position;
    }

    private void setUpViewPager() {
        ArrayList<String> images = new ArrayList<>();
        images.addAll(mImages);

        FullScreenImageGalleryAdapter fullScreenImageGalleryAdapter = new FullScreenImageGalleryAdapter(images, mPaletteColorType);
        mViewPager.setAdapter(fullScreenImageGalleryAdapter);
        mViewPager.addOnPageChangeListener(mViewPagerOnPageChangeListener);
        mViewPager.setCurrentItem(mPosition);

        setActionBarTitle(mPosition);
        if (mImages.size() == 0)
            onDone();
    }

    private void setActionBarTitle(int position) {
        if (mViewPager != null && mImages.size() > 0) {
            mCurrentposition = position;
            int totalPages = mViewPager.getAdapter().getCount();

            if (mToolbar != null) {
                textViewNumbers.setText(String.format("%d of %d", (mCurrentposition + 1), totalPages));
            }
        }
    }

    private void removeListeners() {
        mViewPager.removeOnPageChangeListener(mViewPagerOnPageChangeListener);
    }

    private void goToGallery() {
        if(!mFromGallery) {
            Intent intent = new Intent(FullScreenImageGalleryActivity.this, ImageGalleryActivity.class);
            intent.putStringArrayListExtra(ImageGalleryActivity.ACTION_BACK, (ArrayList<String>) mImages);
            intent.putExtra(ImageGalleryActivity.PALETTE, mPaletteColorType);
            intent.putExtra(ImageGalleryActivity.ACTION_BACK, mName);
            startActivity(intent);
        }
        finish();
    }

    private void onDone() {
        Intent intent = getIntent();
        intent.putStringArrayListExtra(FullScreenImageGalleryActivity.IMAGES, (ArrayList) mImages);
        setResult(RESULT_OK, intent);

        if (mRemovedImages != null && mRemovedImages.size() > 0)
            EventBus.getDefault().postSticky(new ImagesRemovedEvent(mRemovedImages));
        finish();
    }

    // endregion
}
