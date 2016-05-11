package com.jmtop.edu.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * Creator: dengshengjin on 16/4/17 13:00
 * Email: deng.shengjin@zuimeia.com
 */
public class SlidingMenuTabLayout extends TabLayout {
    private Resources mResources;

    public SlidingMenuTabLayout(Context context) {
        super(context);
        mResources = context.getApplicationContext().getResources();
    }

    public SlidingMenuTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mResources = context.getApplicationContext().getResources();
    }

    public SlidingMenuTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mResources = context.getApplicationContext().getResources();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        disallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    private void disallowInterceptTouchEvent(boolean flag) {
        int id = mResources.getIdentifier("slidingmenumain", "id", getContext().getPackageName());
        SlidingMenu sm = (SlidingMenu) getRootView().findViewById(id);
        if (sm != null) {
            sm.getAboveView().requestDisallowInterceptTouchEvent(flag);
        }
    }
}
