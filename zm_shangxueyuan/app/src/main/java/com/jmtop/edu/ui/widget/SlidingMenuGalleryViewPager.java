package com.jmtop.edu.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Creator: dengshengjin on 16/4/17 12:56
 * Email: deng.shengjin@zuimeia.com
 */
public class SlidingMenuGalleryViewPager extends ViewPager {
    public final String TAG = SlidingMenuGalleryViewPager.class.getName();
    private Resources resources;
    private static final int TOUCH_STATE_REST = 0;
    private static final int TOUCH_STATE_SCROLLING = 1;
    private int mTouchState = TOUCH_STATE_REST;
    private float mLastMotionX;

    public SlidingMenuGalleryViewPager(Context context) {
        super(context);
        resources = context.getApplicationContext().getResources();
    }

    public SlidingMenuGalleryViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        resources = context.getApplicationContext().getResources();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
            return true;
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                int index = MotionEventCompat.getActionIndex(event);
                mLastMotionX = MotionEventCompat.getX(event, index);
                disallowInterceptTouchEvent(true);
                break;
        }
        return super.onInterceptTouchEvent(event);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        final int action = event.getAction();
        final float x = event.getX();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastMotionX = x;
                disallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:

                int deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;
                if (getCurrentItem() == 0 && getScrollX() <= 0) {// 判断是否是在最左端向右滑动
                    if (deltaX < 0) {
                        disallowInterceptTouchEvent(false);
                        mTouchState = TOUCH_STATE_REST;
                        return false;
                    }
                }
                if (getAdapter() != null && getCurrentItem() >= (getAdapter().getCount() - 1) && getScrollX() >= (getCurrentItem() * getWidth())) {
                    if (deltaX > 0) {// 判断是否是在最右端向左滑动
                        disallowInterceptTouchEvent(false);// true:不允许父滑动,false：允许父滑动
                        mTouchState = TOUCH_STATE_REST;
                        return false;
                    }
                }

                disallowInterceptTouchEvent(true);
                mTouchState = TOUCH_STATE_SCROLLING;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                disallowInterceptTouchEvent(false);
                mTouchState = TOUCH_STATE_REST;
                break;
        }
        return true;
    }

    private void disallowInterceptTouchEvent(boolean flag) {
        int id = resources.getIdentifier("slidingmenumain", "id", getContext().getPackageName());
        SlidingMenu sm = (SlidingMenu) getRootView().findViewById(id);
        if (sm != null) {
            sm.getAboveView().requestDisallowInterceptTouchEvent(flag);
        }
    }

}