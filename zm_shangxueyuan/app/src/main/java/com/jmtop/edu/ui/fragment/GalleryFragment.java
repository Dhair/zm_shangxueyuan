package com.jmtop.edu.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.jmtop.edu.R;
import com.jmtop.edu.model.GalleryModel;
import com.jmtop.edu.ui.widget.LoadingEmptyView;
import com.jmtop.edu.utils.CommonUtils;
import com.jmtop.edu.utils.ImageLoadUtil;

import uk.co.senab.photoview.PhotoView;

/**
 * Creator: dengshengjin on 16/4/23 00:01
 * Email: deng.shengjin@zuimeia.com
 */
public class GalleryFragment extends AbsLoadingEmptyFragment {
    private GalleryModel mGalleryModel;
    private static final String MODEL = "model";
    private FrameLayout mContainer;
    private View mView;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public static GalleryFragment newInstance(GalleryModel model) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MODEL, model);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        Object object = getArguments().getSerializable(MODEL);
        if (object != null) {
            mGalleryModel = (GalleryModel) object;
        }
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoadUtil.initImageLoader(context);
        }
        mImageLoader = ImageLoader.getInstance();
        mOptions = CommonUtils.getDisplayImageOptionsBuilder().build();
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mView = view;
        mContainer = (FrameLayout) view.findViewById(R.id.container);
        return view;
    }


    @Override
    protected void initWidgetActions() {
        onLoadImage();
    }

    private void onLoadImage() {
        mImageLoader.loadImage(mGalleryModel.getImageDetailUrl(), mOptions, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                if (mView != null) {
                    showLoading(mView);
                }
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                if (mView != null) {
                    showLoadFail(mView, new LoadingEmptyView.LoadViewCallback() {
                        @Override
                        public void callback() {
                            onLoadImage();
                        }
                    });
                }
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (bitmap == null || bitmap.isRecycled()) {
                    return;
                }
                hideLoading();
                PhotoView photoView = new PhotoView(getApplicationContext());
                photoView.setImageBitmap(bitmap);
                mContainer.addView(photoView, FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                if (mView != null) {
                    showLoadFail(mView, new LoadingEmptyView.LoadViewCallback() {
                        @Override
                        public void callback() {
                            onLoadImage();
                        }
                    });
                }
            }
        });
    }
}
