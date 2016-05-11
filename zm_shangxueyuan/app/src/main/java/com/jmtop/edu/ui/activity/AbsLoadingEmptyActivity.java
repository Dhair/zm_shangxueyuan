package com.jmtop.edu.ui.activity;

import android.view.View;
import android.view.ViewStub;

import com.jmtop.edu.R;
import com.jmtop.edu.ui.widget.LoadingEmptyView;

/**
 * Creator: dengshengjin on 16/4/17 18:51
 * Email: deng.shengjin@zuimeia.com
 */
public abstract class AbsLoadingEmptyActivity extends AbsActionBarActivity {
    protected ViewStub mViewStub;
    protected LoadingEmptyView mLoadingEmptyView;

    protected void initLoadingEmptyView() {
        if (mViewStub == null) {
            mViewStub = (ViewStub) findViewById(R.id.view_stub);
            mViewStub.inflate();

            mLoadingEmptyView = (LoadingEmptyView) findViewById(R.id.loading_empty_box);
        }
        mLoadingEmptyView.setVisibility(View.VISIBLE);
    }

    protected void showLoading() {
        initLoadingEmptyView();
        if (mLoadingEmptyView != null) {
            mLoadingEmptyView.updateLoadView();
        }
    }

    protected void hideLoading() {
        if (mLoadingEmptyView != null) {
            mLoadingEmptyView.setVisibility(View.GONE);
        }
    }

    protected void showLoadFail(LoadingEmptyView.LoadViewCallback loadViewCallback) {
        initLoadingEmptyView();
        if (mLoadingEmptyView != null) {
            mLoadingEmptyView.updateEmptyView(loadViewCallback);
        }
    }

    protected void showEmpty() {
        initLoadingEmptyView();
        if (mLoadingEmptyView != null) {
            mLoadingEmptyView.updateEmptyView();
        }
    }

    protected void showEmptyWidthWarn(String warn) {
        initLoadingEmptyView();
        if (mLoadingEmptyView != null) {
            mLoadingEmptyView.updateEmptyView(warn, null);
        }
    }

    protected boolean isLoading() {
        return mLoadingEmptyView != null && mLoadingEmptyView.getVisibility() == View.VISIBLE && mLoadingEmptyView.isLoading();
    }
}
