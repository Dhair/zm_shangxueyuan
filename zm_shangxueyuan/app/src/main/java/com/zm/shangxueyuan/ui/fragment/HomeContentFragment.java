package com.zm.shangxueyuan.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.ui.provider.BusProvider;
import com.zm.utils.PhoneUtil;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-11_上午11:58:00
 * @Description 首页内容页面
 */
public class HomeContentFragment extends BaseFragment {


    @Override
    protected void initData() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_content, container, false);
        updateStatusBarHeightV19(view);
        return view;
    }

    private void updateStatusBarHeightV19(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View space = view.findViewById(R.id.notification_space);
            if (space != null) {
                ViewGroup.LayoutParams lp = space.getLayoutParams();
                lp.height = PhoneUtil.getStatusBarHeight(getApplicationContext());
                space.requestLayout();
            }
        }
    }

    @Override
    protected void initWidgetActions() {
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Always unregister when an object no longer should be on the bus.
        try {
            BusProvider.getInstance().unregister(this);
        } catch (Exception e) {
        }
    }
}
