package com.zm.shangxueyuan.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.NavModel;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.adapter.VideoAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Creator: dengshengjin on 16/4/17 11:42
 * Email: deng.shengjin@zuimeia.com
 */
public class HomeVideoContentFragment extends AbsLoadingEmptyFragment {
    private NavModel mNavModel;
    private static final String MODEL = "model";
    private ListView mListView;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private VideoAdapter mVideoAdapter;

    public static HomeVideoContentFragment newInstance(NavModel navModel) {
        HomeVideoContentFragment fragment = new HomeVideoContentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(MODEL, navModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        mVideoAdapter = new VideoAdapter(getApplicationContext());
        Object object = getArguments().getSerializable(MODEL);
        if (object != null) {
            mNavModel = (NavModel) object;
        }
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_video_content, container, false);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListView.setAdapter(mVideoAdapter);
        showLoading(view);
        return view;
    }

    @Override
    protected void initWidgetActions() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<VideoModel> videoList = VideoDBUtil.queryVideoWithNav(mNavModel.getNavId());
                final List<VideoModel> topVideoList = queryTopVideos(videoList);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideLoading();
                        mVideoAdapter.setVideoList(videoList);
                        mVideoAdapter.notifyDataSetChanged();
                    }
                }, 5000);

            }
        });
    }

    private List<VideoModel> queryTopVideos(List<VideoModel> videoList) {
        if (videoList == null) {
            return null;
        }
        List<VideoModel> topVideoList = new LinkedList<>();
        for (int i = 0; i < videoList.size(); i++) {
            VideoModel videoModel = videoList.get(i);
            if (VideoModel.isTopVideo(videoModel)) {
                topVideoList.add(videoModel);
                videoList.remove(videoModel);
            }
        }
        return topVideoList;
    }
}
