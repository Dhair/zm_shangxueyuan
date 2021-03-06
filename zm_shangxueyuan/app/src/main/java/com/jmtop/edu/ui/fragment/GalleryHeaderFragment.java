package com.jmtop.edu.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jmtop.edu.ui.activity.GallerySubActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.jmtop.edu.R;
import com.jmtop.edu.helper.StorageHelper;
import com.jmtop.edu.model.GalleryTopicModel;
import com.jmtop.edu.ui.activity.GalleryListActivity;
import com.jmtop.edu.utils.CommonUtils;
import com.jmtop.edu.utils.ImageLoadUtil;

/**
 * Creator: dengshengjin on 16/4/17 16:42
 * Email: deng.shengjin@zuimeia.com
 */
public class GalleryHeaderFragment extends BaseFragment {
    private static final String MODEL = "model";
    private GalleryTopicModel mGalleryTopicModel;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public static GalleryHeaderFragment newInstance(GalleryTopicModel galleryTopicModel) {
        GalleryHeaderFragment fragment = new GalleryHeaderFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MODEL, galleryTopicModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        Object object = getArguments().getSerializable(MODEL);
        if (object != null) {
            mGalleryTopicModel = (GalleryTopicModel) object;
        }
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoadUtil.initImageLoader(context);
        }
        mImageLoader = ImageLoader.getInstance();
        mOptions = CommonUtils.getDisplayImageOptionsBuilder(R.drawable.top_default).build();
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_video_header_item, container, false);
        ImageView imageView = (ImageView) view.findViewById(R.id.header_image);
        mImageLoader.displayImage(StorageHelper.getImageUrl(mGalleryTopicModel.getImage()), imageView, mOptions);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGalleryTopicModel.isHasSubTopic()) {
                    startActivity(GallerySubActivity.getIntent(getApplicationContext(), mGalleryTopicModel));
                } else {
                    startActivity(GalleryListActivity.getIntent(getApplicationContext(), mGalleryTopicModel));
                }
            }
        });
        return view;
    }

    @Override
    protected void initWidgetActions() {

    }
}
