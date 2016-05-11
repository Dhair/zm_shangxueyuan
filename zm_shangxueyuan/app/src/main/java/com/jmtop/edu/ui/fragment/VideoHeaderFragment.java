package com.jmtop.edu.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.jmtop.edu.R;
import com.jmtop.edu.helper.DialogUIHelper;
import com.jmtop.edu.helper.StorageHelper;
import com.jmtop.edu.model.UserModel;
import com.jmtop.edu.model.VideoModel;
import com.jmtop.edu.ui.activity.VideoDetailActivity;
import com.jmtop.edu.ui.activity.VideoTopicActivity;
import com.jmtop.edu.utils.CommonUtils;
import com.jmtop.edu.utils.ImageLoadUtil;

/**
 * Creator: dengshengjin on 16/4/17 16:42
 * Email: deng.shengjin@zuimeia.com
 */
public class VideoHeaderFragment extends BaseFragment {
    private static final String MODEL = "model";
    private VideoModel mVideoModel;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public static VideoHeaderFragment newInstance(VideoModel videoModel) {
        VideoHeaderFragment fragment = new VideoHeaderFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MODEL, videoModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        Object object = getArguments().getSerializable(MODEL);
        if (object != null) {
            mVideoModel = (VideoModel) object;
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
        TextView topicText = (TextView) view.findViewById(R.id.flag_text);
        TextView vipText = (TextView) view.findViewById(R.id.vip_text);
        if (VideoModel.isTopicVideo(mVideoModel)) {
            topicText.setVisibility(View.VISIBLE);
        } else {
            topicText.setVisibility(View.GONE);
            if (mVideoModel.isLoginValid()) {
                vipText.setVisibility(View.VISIBLE);
            } else {
                vipText.setVisibility(View.GONE);
            }
        }
        if (mVideoModel.isLoginValid()) {
            vipText.setVisibility(View.VISIBLE);
        } else {
            vipText.setVisibility(View.GONE);
        }
        mImageLoader.displayImage(StorageHelper.getImageUrl(mVideoModel.getImage()), imageView, mOptions);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoModel.isLoginValid() && !UserModel.isLogin(getApplicationContext())) {
                    DialogUIHelper.showLoginTips(getActivity());
                } else {
                    if (VideoModel.isTopicVideo(mVideoModel)) {
                        getActivity().startActivity(VideoTopicActivity.getIntent(getApplicationContext(), mVideoModel.getVideoId(), mVideoModel.getTitle()));
                    } else {
                        getActivity().startActivity(VideoDetailActivity.getIntent(getApplicationContext(), mVideoModel));
                    }
                }
            }
        });
        return view;
    }

    @Override
    protected void initWidgetActions() {

    }
}
