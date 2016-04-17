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
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.GalleryDBUtil;
import com.zm.shangxueyuan.model.GalleryCategoryModel;
import com.zm.shangxueyuan.model.GalleryTopicModel;
import com.zm.shangxueyuan.ui.adapter.GalleryAdapter;
import com.zm.shangxueyuan.ui.widget.SlidingMenuGalleryViewPager;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Creator: dengshengjin on 16/4/17 10:56
 * Email: deng.shengjin@zuimeia.com
 */
public class HomeGalleryFragment extends AbsLoadingEmptyFragment {
    private Executor mExecutor = Executors.newCachedThreadPool();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ExpandableListView mListView;
    private GalleryAdapter mGalleryAdapter;

    public static HomeGalleryFragment newInstance() {
        HomeGalleryFragment fragment = new HomeGalleryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        mGalleryAdapter = new GalleryAdapter(getApplicationContext());
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_gallery, container, false);
        mListView = (ExpandableListView) view.findViewById(R.id.list_view);
        mListView.setAdapter(mGalleryAdapter);
        showLoading(view);

        return view;
    }

    @Override
    protected void initWidgetActions() {
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return true;
            }
        });
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<GalleryCategoryModel> galleryCategoryList = GalleryDBUtil.query();
                final List<GalleryTopicModel> galleryTopicList = GalleryDBUtil.queryTopics();
                final List<GalleryTopicModel> topTopicList = queryTopTopics(galleryTopicList);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }
                        hideLoading();

                        if (topTopicList != null && !topTopicList.isEmpty()) {
                            View headerView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.fragment_home_gallery_header, null);
                            initHeaderViews(headerView, topTopicList);
                            mListView.addHeaderView(headerView);
                        }
                        mGalleryAdapter.setCategoryList(galleryCategoryList);
                        mGalleryAdapter.setTopicList(galleryTopicList);
                        mGalleryAdapter.notifyDataSetChanged();
                        if (galleryCategoryList != null) {
                            for (int i = 0, size = galleryCategoryList.size(); i < size; i++) {
                                mListView.expandGroup(i);
                            }
                        }
                    }
                }, 500);

            }
        });
    }

    private List<GalleryTopicModel> queryTopTopics(List<GalleryTopicModel> topics) {
        if (topics == null) {
            return null;
        }
        List<GalleryTopicModel> topTopics = new LinkedList<>();
        for (int i = 0; i < topics.size(); i++) {
            GalleryTopicModel topicModel = topics.get(i);
            if (GalleryTopicModel.isTopTopic(topicModel)) {
                topTopics.add(topicModel);
                topics.remove(topicModel);
            }
        }
        return topTopics;
    }

    private void initHeaderViews(View headerView, final List<GalleryTopicModel> topicList) {
        SlidingMenuGalleryViewPager headerViewPager = (SlidingMenuGalleryViewPager) headerView.findViewById(R.id.header_view_pager);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) headerView.findViewById(R.id.indicator);
        final TextView descText = (TextView) headerView.findViewById(R.id.desc_text);
        GalleryHeaderPagerAdapter headerPagerAdapter = new GalleryHeaderPagerAdapter(getChildFragmentManager(), topicList);
        headerViewPager.setAdapter(headerPagerAdapter);
        circlePageIndicator.setViewPager(headerViewPager);
        headerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                descText.setText(topicList.get(position).getTitle());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (topicList.size() <= 1) {
            circlePageIndicator.setVisibility(View.GONE);
        } else {
        }
        descText.setText(topicList.get(0).getTitle());
    }

    private static class GalleryHeaderPagerAdapter extends FragmentStatePagerAdapter {
        private List<GalleryTopicModel> mGalleryTopicList;

        public GalleryHeaderPagerAdapter(FragmentManager fm, List<GalleryTopicModel> galleryTopicModels) {
            super(fm);
            mGalleryTopicList = galleryTopicModels;
        }

        @Override
        public Fragment getItem(int position) {
            return GalleryHeaderFragment.newInstance(mGalleryTopicList.get(position));
        }

        @Override
        public int getCount() {
            if (mGalleryTopicList == null) {
                return 0;
            }
            return mGalleryTopicList.size();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mGalleryAdapter != null) {
            mGalleryAdapter.clear();
        }
    }

}
