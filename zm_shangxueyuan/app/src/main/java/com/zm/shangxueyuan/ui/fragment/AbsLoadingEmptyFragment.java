package com.zm.shangxueyuan.ui.fragment;

import android.view.View;
import android.view.ViewStub;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.ui.widget.LoadingEmptyView;

/**
 * Creator: dengshengjin on 16/4/17 14:56
 * Email: deng.shengjin@zuimeia.com
 */
public abstract class AbsLoadingEmptyFragment extends BaseFragment {
    protected ViewStub mViewStub;
    protected LoadingEmptyView mLoadingEmptyView;

    private void initLoadingEmptyView(View mView) {
        if (mViewStub == null) {
            mViewStub = (ViewStub) mView.findViewById(R.id.view_stub);
            mViewStub.inflate();

            mLoadingEmptyView = (LoadingEmptyView) mView.findViewById(R.id.loading_empty_box);
        }
    }

    protected void showLoading(View mView) {
        initLoadingEmptyView(mView);
        mViewStub.setVisibility(View.VISIBLE);
        if (mLoadingEmptyView != null) {
            mLoadingEmptyView.updateLoadView();
        }
    }

    protected void hideLoading() {
        if (mViewStub != null) {
            mViewStub.setVisibility(View.GONE);
        }
    }

    protected void showEmpty(View mView, String emptyStr) {
        initLoadingEmptyView(mView);
        mViewStub.setVisibility(View.VISIBLE);
        if (mLoadingEmptyView != null) {
            mLoadingEmptyView.updateEmptyView(emptyStr, null);
        }
    }

    protected void hideEmpty() {
        if (mViewStub != null) {
            mViewStub.setVisibility(View.GONE);
        }
    }
}
