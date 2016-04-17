package com.zm.shangxueyuan.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ListView;

/**
 * Creator: dengshengjin on 16/4/17 13:04
 * Email: deng.shengjin@zuimeia.com
 */
public class CustomListView extends ListView {
    private float mLastMotionX;
    private float mLastMotionY;
    private boolean mIsBeingDragged;
    private int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    public CustomListView(Context context) {
        super(context);
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        final int action = ev.getAction();
        final float x = ev.getRawX();
        final float y = ev.getRawY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                mLastMotionY = y;
                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE:
                final float dy = y - mLastMotionY;
                final float xDiff = Math.abs(x - mLastMotionX);
                final float yDiff = Math.abs(dy);
                if (yDiff > mTouchSlop && yDiff > xDiff) {
                    mIsBeingDragged = true;
                    mLastMotionY = y;
                }
                break;
        }
        return mIsBeingDragged;
    }
}
