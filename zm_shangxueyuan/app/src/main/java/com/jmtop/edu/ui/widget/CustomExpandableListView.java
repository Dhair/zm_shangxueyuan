package com.jmtop.edu.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ExpandableListView;

/**
 * Creator: dengshengjin on 16/4/17 13:04
 * Email: deng.shengjin@zuimeia.com
 */
public class CustomExpandableListView extends ExpandableListView {
    private float mLastMotionX;
    private float mLastMotionY;
    private boolean mIsBeingDragged;
    private int mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();

    public CustomExpandableListView(Context context) {
        super(context);
    }

    public CustomExpandableListView(Context context, AttributeSet attrs) {
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
