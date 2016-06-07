
/*
 * TouchInterceptFrameLayout.java
 * Heyandroid
 *
 * Created by Miroslav Ignjatovic on 6/5/2016
 * Copyright (c) 2015 CommonSun All rights reserved.
 */

package com.etiennelawlor.imagegallery.library.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;


public class TouchInterceptFrameLayout extends FrameLayout {

    float mLastX, mLastY, mStartY;
    boolean mIsBeingDragged;
    float mTouchSlop;


    public TouchInterceptFrameLayout(Context context) {
        super(context);
    }

    public TouchInterceptFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchInterceptFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = event.getX();
                mLastY = event.getY();
                mStartY = mLastY;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();
                float xDelta = Math.abs(x - mLastX);
                float yDelta = Math.abs(y - mLastY);

                float yDeltaTotal = y - mStartY;
                if (yDelta > xDelta && Math.abs(yDeltaTotal) > mTouchSlop) {
                    mIsBeingDragged = true;
                    mStartY = y;
                    return true;
                }
                break;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float x = event.getX();
                float y = event.getY();

                float xDelta = Math.abs(x - mLastX);
                float yDelta = Math.abs(y - mLastY);

                float yDeltaTotal = y - mStartY;
                if (!mIsBeingDragged && yDelta > xDelta && Math.abs(yDeltaTotal) > mTouchSlop) {
                    mIsBeingDragged = true;
                    mStartY = y;
                    yDeltaTotal = 0;
                }
                if (yDeltaTotal < 0)
                    yDeltaTotal = 0;

                if (mIsBeingDragged) {
                    scrollTo(0, (int)yDeltaTotal);
                }

                mLastX = x;
                mLastY = y;
                break;
        }

        return true;
    }
}
