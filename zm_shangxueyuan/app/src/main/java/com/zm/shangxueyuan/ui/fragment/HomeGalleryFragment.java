package com.zm.shangxueyuan.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zm.shangxueyuan.R;

/**
 * Creator: dengshengjin on 16/4/17 10:56
 * Email: deng.shengjin@zuimeia.com
 */
public class HomeGalleryFragment extends BaseFragment {

    public static HomeGalleryFragment newInstance() {
        HomeGalleryFragment fragment = new HomeGalleryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_gallery, container, false);
        return view;
    }

    @Override
    protected void initWidgetActions() {

    }
}
