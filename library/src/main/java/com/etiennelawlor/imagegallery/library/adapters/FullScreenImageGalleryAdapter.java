package com.etiennelawlor.imagegallery.library.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.etiennelawlor.imagegallery.library.R;
import com.etiennelawlor.imagegallery.library.enums.PaletteColorType;
import com.etiennelawlor.imagegallery.library.util.ImageGalleryUtils;
import com.etiennelawlor.imagegallery.library.view.PaletteTransformation;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by etiennelawlor on 8/20/15.
 */
public class FullScreenImageGalleryAdapter extends PagerAdapter {
    private static final String MY_BUCKETIMAGE_SUFFIX = "/img.jpg";

    // region Member Variables
    private final List<String> mImages;
    private final PaletteColorType mPaletteColorType;
    // endregion

    // region Constructors
    public FullScreenImageGalleryAdapter(List<String> images, PaletteColorType paletteColorType) {
        mImages = images;
        mPaletteColorType = paletteColorType;
    }
    // endregion

    private boolean isLocal(String url) {
        if (url.indexOf(MY_BUCKETIMAGE_SUFFIX) == -1  &&
                url.indexOf("http://") != 0 &&
                url.indexOf("https://") != 0) {
            return true;
        }
        return false;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.fullscreen_image, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.iv);
        final LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.ll);

        String image = mImages.get(position);

        Context context = imageView.getContext();
        int width = ImageGalleryUtils.getScreenWidth(context);

        if (!TextUtils.isEmpty(image)) {
            if (isLocal(image)) { // it is local
                File localFile = new File(image);
                Picasso.with(imageView.getContext())
                        .load(localFile)
                        .resize(width, 0)
                        .transform(PaletteTransformation.instance())
                        .into(imageView, new PaletteTransformation.PaletteCallback(imageView) {
                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onSuccess(Palette palette) {
                                int bgColor = getBackgroundColor(palette);
                                if (bgColor != -1)
                                    linearLayout.setBackgroundColor(bgColor);
                            }
                        });
            } else {
                Picasso.with(imageView.getContext())
                        .load(image)
                        .resize(width, 0)
                        .transform(PaletteTransformation.instance())
                        .into(imageView, new PaletteTransformation.PaletteCallback(imageView) {
                            @Override
                            public void onError() {

                            }

                            @Override
                            public void onSuccess(Palette palette) {
                                int bgColor = getBackgroundColor(palette);
                                if (bgColor != -1)
                                    linearLayout.setBackgroundColor(bgColor);
                            }
                        });
            }
        } else {
            imageView.setImageDrawable(null);
        }


        container.addView(view, 0);

        return view;
    }

    @Override
    public int getCount() {
        return mImages.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
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
