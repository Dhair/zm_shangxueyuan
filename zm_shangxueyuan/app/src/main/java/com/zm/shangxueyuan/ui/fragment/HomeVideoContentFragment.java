package com.zm.shangxueyuan.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.SettingDBUtil;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.HotWordsModel;
import com.zm.shangxueyuan.model.NavModel;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.activity.VideoDetailActivity;
import com.zm.shangxueyuan.ui.activity.VideoTopicActivity;
import com.zm.shangxueyuan.ui.activity.WebViewActivity;
import com.zm.shangxueyuan.ui.adapter.VideoAdapter;
import com.zm.shangxueyuan.ui.listener.OnItemClickListener;
import com.zm.shangxueyuan.ui.widget.SlidingMenuHeaderViewPager;

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
    private Executor mExecutor = Executors.newCachedThreadPool();
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
        mVideoAdapter.setOnItemClickListener(new OnItemClickListener<VideoModel>() {

            @Override
            public void onItemClick(View v, VideoModel videoModel, int position) {
                if (VideoModel.isTopicVideo(videoModel)) {
                    getActivity().startActivity(VideoTopicActivity.getIntent(getApplicationContext(), videoModel.getVideoId(), videoModel.getTitle()));
                } else {
                    getActivity().startActivity(VideoDetailActivity.getIntent(getApplicationContext(), videoModel));
                }
            }
        });
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<VideoModel> videoList = VideoDBUtil.queryVideoWithNav(mNavModel.getNavId());
                final List<VideoModel> topVideoList = queryTopVideos(videoList);
                final String[] newsArr = HotWordsModel.parseNews(SettingDBUtil.getInstance(getApplicationContext()).getConfigServer());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }
                        hideLoading();
                        if (topVideoList != null && !topVideoList.isEmpty()) {
                            View headerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_home_video_header, null);
                            initHeaderViews(headerView, topVideoList, newsArr);
                            mListView.addHeaderView(headerView);
                        }
                        mVideoAdapter.setVideoList(videoList);
                        mVideoAdapter.notifyDataSetChanged();
                    }
                }, 500);

            }
        });
    }

    private void initHeaderViews(View headerView, final List<VideoModel> videoList, final String[] newsArr) {
        SlidingMenuHeaderViewPager headerViewPager = (SlidingMenuHeaderViewPager) headerView.findViewById(R.id.header_view_pager);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) headerView.findViewById(R.id.indicator);
        final TextView descText = (TextView) headerView.findViewById(R.id.desc_text);
        VideoHeaderPagerAdapter videoHeaderPagerAdapter = new VideoHeaderPagerAdapter(getChildFragmentManager(), videoList);
        headerViewPager.setAdapter(videoHeaderPagerAdapter);
        circlePageIndicator.setViewPager(headerViewPager);
        headerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                descText.setText(videoList.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (videoList.size() <= 1) {
            circlePageIndicator.setVisibility(View.GONE);
            headerViewPager.setNeedDisableParent(false);
        } else {
            headerViewPager.setNeedDisableParent(true);
        }
        descText.setText(videoList.get(0).getTitle());
        LinearLayout newsTop = (LinearLayout) headerView.findViewById(R.id.news_top_box);
        if (mNavModel.getNavId() == 1 && newsArr != null && newsArr.length == 2) {
            newsTop.setVisibility(View.VISIBLE);
            TextView newsText = (TextView) headerView.findViewById(R.id.news_top_text);
            newsText.setText(newsArr[0]);
            newsText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(WebViewActivity.getIntent(getActivity(), newsArr[1], getString(R.string.news_top)));
                }
            });
        } else {
            newsTop.setVisibility(View.GONE);
        }
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

    private static class VideoHeaderPagerAdapter extends FragmentStatePagerAdapter {
        private List<VideoModel> mVideoList;

        public VideoHeaderPagerAdapter(FragmentManager fm, List<VideoModel> videoList) {
            super(fm);
            mVideoList = videoList;
        }

        @Override
        public Fragment getItem(int position) {
            return VideoHeaderFragment.newInstance(mVideoList.get(position));
        }

        @Override
        public int getCount() {
            if (mVideoList == null) {
                return 0;
            }
            return mVideoList.size();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mVideoAdapter != null) {
            mVideoAdapter.clear();
        }
    }
}
