package com.zm.shangxueyuan.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Creator: dengshengjin on 16/4/17 15:50
 * Email: deng.shengjin@zuimeia.com
 */
public class SlidingMenuHeaderViewPager extends ViewPager {
    private Resources mResources;
    private boolean mNeedDisableParent;

    public SlidingMenuHeaderViewPager(Context context) {
        super(context);
        init(context);
    }

    public SlidingMenuHeaderViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mNeedDisableParent = true;
        mResources = context.getApplicationContext().getResources();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mNeedDisableParent) {
            disallowInterceptTouchEvent();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void disallowInterceptTouchEvent() {
        int id1 = mResources.getIdentifier("video_view_pager", "id", getContext().getPackageName());
        ViewGroup viewGroup = (ViewGroup) getRootView().findViewById(id1);
        viewGroup.requestDisallowInterceptTouchEvent(true);

    }

    //true 禁用 false 不禁用
    public void setNeedDisableParent(boolean needDisableParent) {
        mNeedDisableParent = needDisableParent;
    }
}
