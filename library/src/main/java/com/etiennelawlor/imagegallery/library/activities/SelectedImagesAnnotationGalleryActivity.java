
/*
 * AnnotatedImageListAdapter.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 6/5/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.adapters.AnnotatedImageListAdapter;
import com.etiennelawlor.imagegallery.library.enums.PaletteColorType;
import com.etiennelawlor.imagegallery.library.events.AnnotatingImageTapEvent;
import com.etiennelawlor.imagegallery.library.events.FinishedSelectionEvent;
import com.etiennelawlor.imagegallery.library.models.ImageModel;
import com.etiennelawlor.imagegallery.library.view.PaletteTransformation;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.etiennelawlor.imagegallery.library.util.ImageGalleryUtils.dp2px;

public class SelectedImagesAnnotationGalleryActivity extends AppCompatActivity {
    public static final String TAG = SelectedImagesAnnotationGalleryActivity.class.getSimpleName();
    public static int BOTTOM_BAR_IMAGE_SIZE_DIPS = 96; // @todo take from resource

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
    private String mImagesGalleryId;


    private Toolbar mToolbar;
    private ViewPager mViewPager;
    //private TextView textViewNumbers, textViewNavigation;
    //private LinearLayout linearLayoutBack;
    private ImageView imageViewBack;
    private TextView textViewRemove;
    private TextView textViewDone;
    // endregion

    private ScrollView scrollView;
    private RecyclerView mRecyclerView;
    AnnotatedImageListAdapter adapter;
    ImageView oneImage;
    private RelativeLayout mainLayout;
    private FrameLayout imageLayout;
    private FrameLayout subLayout;
    private Target picassoTarget;
    private EditText annotationText;

    private int imageViewWidth, imageViewHeight;
    private int annotationViewHeight;
    private float annotationToBitmapHeightScale;
    private int lastTapDownY;
    private int lastMoveUpY;
    private Drawable annotationXimage;
    private int adapterPosition;
    private float scaledFactor;
    private int scaledBitmapHeight;

    private int minAnnotationForBitmap, maxAnnotationForBitmap;
    private int startingAnnotationPosition;



    // region Lifecycle Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_selected_images_annotation_gallery);
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
                //textViewNavigation.setText(mName);
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
        if (mPaletteColorType == null)
            mPaletteColorType = PaletteColorType.DARK_VIBRANT;

        mRemovedImages = new ArrayList<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        Picasso.with(this).cancelRequest(oneImage);
        super.onStop();
    }

    // region Helper Methods
    private void bindViews() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        imageViewBack = (ImageView) mToolbar.findViewById(R.id.back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textViewRemove = (TextView) findViewById(R.id.caption);
        textViewRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectedAdapterImage();
            }
        });

        textViewDone = (TextView) findViewById(R.id.nextAction);
        textViewDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDone();
            }
        });

        mRecyclerView = (RecyclerView)findViewById(R.id.selectedImages);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setHasFixedSize(true);
        layoutManager.setStackFromEnd(false);
        mRecyclerView.setLayoutManager(layoutManager);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        oneImage = (ImageView) findViewById(R.id.image);
        annotationXimage = getResources().getDrawable(R.drawable.icon_remove);


        mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        imageLayout = (FrameLayout) findViewById(R.id.imageLayout);
        subLayout = (FrameLayout) findViewById(R.id.subLayout);
        annotationText = (EditText) findViewById(R.id.annotationText);
        annotationText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                setAnnotationRemove(s.length() > 0);
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        annotationText.setText("");
        annotationText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    keyboardDone();
                }
                return false;
            }
        });
        setImageListeners();
        showAnnotation(true);

        ViewTreeObserver viewTreeObserver = imageLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        imageLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        imageLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    imageViewWidth = imageLayout.getWidth();
                    imageViewHeight = imageLayout.getHeight();
                    resolveMeasuredImageLayout();
                }
            });
        }

        ViewTreeObserver viewTreeObserverAnno = annotationText.getViewTreeObserver();
        if (viewTreeObserverAnno.isAlive()) {
            viewTreeObserverAnno.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        annotationText.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        annotationText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    annotationViewHeight = annotationText.getHeight();
                    resolveMeasuredAnnoLayout();
                }
            });
        }
    }

    private void setImageListeners() {
        subLayout.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchImage(v, event);
            }
        });

        annotationText.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return onTouchAnnotation(v, event);
            }
        });
    }

    private void onDone() {
        FinishedSelectionEvent event = new FinishedSelectionEvent(adapter.getMediumList(), TAG);
        EventBus.getDefault().postSticky(event);

        finish();
    }
    // endregion


    private void resolveMeasuredImageLayout() {
        Log.d("imageViewWidth", "imageViewWidth=" + imageViewWidth);
        Log.d("imageViewHeight", "imageViewHeight=" + imageViewHeight);
    }

    private void resolveMeasuredAnnoLayout() {
        Log.d("annotationViewHeight", "annotationViewHeight=" + annotationViewHeight);
    }

    private void setAnnotationText(String annotation){
        if (annotationText != null) {
            annotationText.setText(annotation);
        }
    }

    private String getAnnotationText() {
        if (annotationText != null)
            return annotationText.getText().toString();
        return null;
    }

    private void fixAnnotationPosition(float viewY) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)annotationText.getLayoutParams();
        int pos = (int)(viewY - annotationViewHeight/2f);
        if (pos < minAnnotationForBitmap)
            pos = minAnnotationForBitmap;
        if (pos > maxAnnotationForBitmap)
            pos = maxAnnotationForBitmap;
        params.topMargin = pos;
        annotationText.requestLayout();
    }

    /** *touch for annotation */
    private boolean onTouchImage(View v, MotionEvent event) {
        float screenY = event.getY();
        float viewY = screenY - v.getTop();
        if(event.getAction() == MotionEvent.ACTION_UP) {
            Log.d("onUp", "screenY=" + screenY + ",viewY=" + viewY);
            if (Math.abs(lastTapDownY - viewY) < 3) {
                tapOnImage();
            } else {
                lastMoveUpY = (int)viewY;
                annotationChanged();
            }
        } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
            Log.d("onDown", "screenY=" + screenY + ",viewY=" + viewY);
            lastTapDownY = (int)viewY;
        } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
            fixAnnotationPosition(viewY);
            Log.d("onMovingImage", "screenY=" + screenY + ", viewY=" + viewY);
        }

        return true;
    }

    /** *touch for annotation */
    private boolean onTouchAnnotation(View v, MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (annotationText.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                if (event.getRawX() >= (annotationText.getRight() - annotationText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    annotationRemove();
                    return true;
                }
            }
        }
        return false;
    }

    private void annotationChanged() {
        ImageModel imageModel = adapter.getItem(adapterPosition);
        if (imageModel != null) {
            int startingBitmapHeight = (int) (imageViewHeight/2f - scaledBitmapHeight/2f);
            int heightDiff = (int) (lastMoveUpY - annotationViewHeight/2f - startingBitmapHeight);
            int percentFromHeight = (int)(heightDiff * 100f / scaledBitmapHeight);
            imageModel.setAnnotation(getAnnotationText(), percentFromHeight, annotationToBitmapHeightScale);
            adapter.notifyItemChanged(adapterPosition);
        }
    }

    private void annotationRemove() {
        annotationText.setText("");
        showAnnotation(false);
        editAnnotation(false);

        annotationChanged();
    }

    private void keyboardDone() {
        if (TextUtils.isEmpty(annotationText.getText())) {
            showAnnotation(false);
        }
    }

    private void removeSelectedAdapterImage() {
        Log.e("tag", "removeSelectedAdapterImage");
    }

    private void tapOnImage() {
        if (TextUtils.isEmpty(annotationText.getText())) {
            if (annotationText.getVisibility() == View.INVISIBLE) {
                showAnnotation(true);
                editAnnotation(true);
            } else {
                showAnnotation(false);
                editAnnotation(false);
            }
        } else {
            if (annotationText.getVisibility() == View.INVISIBLE)
                showAnnotation(true);
            if (annotationText.isFocused()) {
                editAnnotation(false);
                annotationChanged();
            } else
                editAnnotation(true);
        }
    }

    private void editAnnotation(boolean edit) {
        if (edit) {
            annotationText.requestFocus();
            showSoftKeyboard(annotationText);
        } else {
            hideSoftKeyboard(annotationText);
            annotationText.clearFocus();
        }
    }

    private void showAnnotation(boolean show) {
        if (show) {
            annotationText.setVisibility(View.VISIBLE);
        } else {
            annotationText.setVisibility(View.INVISIBLE);
        }
    }

    private void setAnnotationRemove(boolean visible) {
        if (visible) {
            int h = annotationXimage.getIntrinsicHeight();
            int w = annotationXimage.getIntrinsicWidth();
            annotationXimage.setBounds(0, 0, w, h);
            annotationText.setCompoundDrawables(null, null, annotationXimage, null);
        } else {
            annotationText.setCompoundDrawables(null, null, null, null);
        }
    }

    private void hideSoftKeyboard(EditText edit) {
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        IBinder binder = edit.getWindowToken();
        inputMethodManager.hideSoftInputFromWindow(binder, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void showSoftKeyboard(EditText edit) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(edit, InputMethodManager.SHOW_IMPLICIT);
    }


    private void gotSelectedImages(List<ImageModel> result) {
        if (adapter == null) {
            adapter = new AnnotatedImageListAdapter(this, result, dp2px(this, BOTTOM_BAR_IMAGE_SIZE_DIPS));
            mRecyclerView.setAdapter(adapter);
            mRecyclerView.scrollToPosition(0);
            tapOnImageBar(0);
        } else {
            addToSelectedImageList(result);
        }
    }

    private void addToSelectedImageList(List<ImageModel> result) {
        boolean removed = adapter.addImages(result);
        if (removed)
            tapOnImageBar(0);
    }

    private void measureCenterImageComplete(Bitmap imageBitmap) {
        // we have bitmap size,
        // and we have image size, so we now percentage resize
        int bitmapHeight = imageBitmap.getHeight();
        int bitmapWidth = imageBitmap.getWidth();
        float aspect = (float) bitmapWidth / (float) bitmapHeight;
        if (aspect > 1.0f) { // bitmap_width = image_width
            scaledFactor = (float) imageViewWidth / (float) bitmapWidth;
        } else { // bitmap_height = image_height
            scaledFactor = (float) imageViewHeight / (float) bitmapHeight;
        }


        scaledBitmapHeight = (int)(bitmapHeight * scaledFactor);

        annotationToBitmapHeightScale = (float) annotationViewHeight / (float) scaledBitmapHeight;
        minAnnotationForBitmap = (int)(imageViewHeight/2f - scaledBitmapHeight/2f);
        maxAnnotationForBitmap = (int)(imageViewHeight/2f + scaledBitmapHeight/2f - annotationViewHeight);
        startingAnnotationPosition = (int) (imageViewHeight/2f);
        Log.d("minmax", "min:" + minAnnotationForBitmap + ", max:"+maxAnnotationForBitmap + ", startingAnnotationPosition" + startingAnnotationPosition);
        if (TextUtils.isEmpty(getAnnotationText()))
            fixAnnotationPosition(startingAnnotationPosition);
    }

    private void setAnnotation(ImageModel imageModel) {
        if (imageModel != null) {
            String annotate = imageModel.getAnnotation();
            if (!TextUtils.isEmpty(annotate)) {
                setAnnotationText(annotate);

                int percent = imageModel.getYPositionYPercent();
                int startingBitmapHeight = (int)(imageViewHeight/2f - scaledBitmapHeight/2f);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) annotationText.getLayoutParams();
                int heightDiff = (int)(percent * scaledBitmapHeight / 100f);
                params.topMargin = startingBitmapHeight + heightDiff;
                annotationText.requestLayout();

                showAnnotation(true);
            } else {
                setAnnotationText("");
                showAnnotation(false);
            }
        }
    }

    private boolean imageIsPlus(ImageModel imageModel) {
        return imageModel.localPath.equals(AnnotatedImageListAdapter.IMAGE_IS_PLUS);
    }

    private void startGallerySelection() {
        Intent intent = new Intent(this, PhotoGalleryActivity.class);

        intent.putExtra(PhotoGalleryActivity.ACTION_GALLERY_ID, mImagesGalleryId);
        intent.putExtra(PhotoGalleryActivity.ACTION_BACK_EXTRA, "Back");
        intent.putExtra(PhotoGalleryActivity.MULTIPLE_SELECT_EXTRA, true);
        intent.putStringArrayListExtra(PhotoGalleryActivity.IMAGES_SELECTED_EXTRA, adapter.getMediaIds());
        intent.putExtra(PhotoGalleryActivity.CAPTION_EXTRA, "Select");
        intent.putExtra(PhotoGalleryActivity.ACTION_NEXT_EXTRA, "Done");

        //startActivityForResult(intent, SELECT_FROM_GALLERY);
        startActivity(intent);
    }

    private void tapOnImageBar(int pos) {
        ImageModel currentImage = adapter.getItem(pos);
        if (currentImage != null) {
            if (imageIsPlus(currentImage)) {
                startGallerySelection();
                Log.d("tag", "on PLUS");
                return;
            }
            adapterPosition = pos;
            setAnnotation(currentImage);

            if (!TextUtils.isEmpty(currentImage.getMediaUrl())) {
                File file = new File(currentImage.getMediaUrl());
                picassoTarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        // loading of the bitmap was a success
                        // TODO do some action with the bitmap
                        measureCenterImageComplete(bitmap);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        // loading of the bitmap failed
                        // TODO do some action/warning/error message
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                Picasso
                        .with(this)
                        .load(file)
                        .into(picassoTarget);

                Picasso.with(this)
                        .load(file)
                        .fit()
                        .centerInside()
                        .transform(PaletteTransformation.instance())
                        .into(oneImage, new PaletteTransformation.PaletteCallback(oneImage) {
                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onSuccess(Palette palette) {
                                int bgColor = Color.BLACK;//getBackgroundColor(palette);
                                if (bgColor != -1)
                                    imageLayout.setBackgroundColor(bgColor);
                            }
                        });
            }
        } else {
            oneImage.setImageDrawable(null);
        }
    }

    @Subscribe
    public void onEvent(AnnotatingImageTapEvent event){
        if (event.position != -1) {
            tapOnImageBar(event.position);
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(FinishedSelectionEvent event){
        if (event.source == TAG)
            return;
        FinishedSelectionEvent stickyEvent = EventBus.getDefault().removeStickyEvent(FinishedSelectionEvent.class);
        mImagesGalleryId = stickyEvent.galleryId;
        if (stickyEvent.selectedPhotos != null && stickyEvent.selectedPhotos.size() > 0) {
            gotSelectedImages(stickyEvent.selectedPhotos);
        } else {
            finish(); // will go back to calling without any images
        }
    }

    // region Helper Methods
    private int getBackgroundColor(Palette palette) {
        int bgColor = -1;

        int vibrantColor = palette.getVibrantColor(0x000000);
        int lightVibrantColor = palette.getLightVibrantColor(0x000000);
        int darkVibrantColor = palette.getDarkVibrantColor(0x000000);

        int mutedColor = palette.getMutedColor(0x000000);
        int lightMutedColor = palette.getLightMutedColor(0x000000);
        int darkMutedColor = palette.getDarkMutedColor(0x000000);

        if (mPaletteColorType != null) {
            switch (mPaletteColorType) {
                case VIBRANT:
                    if (vibrantColor != 0) { // primary option
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) { // fallback options
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case LIGHT_VIBRANT:
                    if (lightVibrantColor != 0) { // primary option
                        bgColor = lightVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case DARK_VIBRANT:
                    if (darkVibrantColor != 0) { // primary option
                        bgColor = darkVibrantColor;
                    } else if (vibrantColor != 0) { // fallback options
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (mutedColor != 0) {
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    }
                    break;
                case MUTED:
                    if (mutedColor != 0) { // primary option
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) { // fallback options
                        bgColor = lightMutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case LIGHT_MUTED:
                    if (lightMutedColor != 0) { // primary option
                        bgColor = lightMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (darkMutedColor != 0) {
                        bgColor = darkMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                case DARK_MUTED:
                    if (darkMutedColor != 0) { // primary option
                        bgColor = darkMutedColor;
                    } else if (mutedColor != 0) { // fallback options
                        bgColor = mutedColor;
                    } else if (lightMutedColor != 0) {
                        bgColor = lightMutedColor;
                    } else if (vibrantColor != 0) {
                        bgColor = vibrantColor;
                    } else if (lightVibrantColor != 0) {
                        bgColor = lightVibrantColor;
                    } else if (darkVibrantColor != 0) {
                        bgColor = darkVibrantColor;
                    }
                    break;
                default:
                    break;
            }
        }

        return bgColor;
    }
    // endregion
}
