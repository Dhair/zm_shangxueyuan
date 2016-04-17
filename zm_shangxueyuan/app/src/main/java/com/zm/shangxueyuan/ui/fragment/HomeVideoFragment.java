package com.zm.shangxueyuan.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.helper.MenuNavHelper;
import com.zm.shangxueyuan.model.NavModel;
import com.zm.shangxueyuan.ui.provider.BusProvider;
import com.zm.shangxueyuan.ui.provider.event.MenuControlEvent;

import java.util.List;

/**
 * Creator: dengshengjin on 16/4/17 10:56
 * Email: deng.shengjin@zuimeia.com
 */
public class HomeVideoFragment extends BaseFragment {
    private List<NavModel> mNavModels;
    private VideoPagerAdapter mVideoPagerAdapter;
    private ViewPager mVideoPager;
    private ImageButton mMenuBtn;

    public static HomeVideoFragment newInstance() {
        HomeVideoFragment fragment = new HomeVideoFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initData() {
        mNavModels = MenuNavHelper.getInstance().getVideoNavModels();
        mVideoPagerAdapter = new VideoPagerAdapter(getChildFragmentManager(), mNavModels);
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_video, container, false);
        mVideoPager = (ViewPager) view.findViewById(R.id.video_view_pager);
        mVideoPager.setAdapter(mVideoPagerAdapter);

        TabLayout mTabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mVideoPager);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);//设置TabLayout的Tab操作屏幕可滚动

        mMenuBtn= (ImageButton) view.findViewById(R.id.menu_btn);
        return view;
    }

    @Override
    protected void initWidgetActions() {
        mMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new MenuControlEvent());
            }
        });
    }

    private static class VideoPagerAdapter extends FragmentStatePagerAdapter {
        private List<NavModel> mNavModels;

        public VideoPagerAdapter(FragmentManager fm, List<NavModel> navModels) {
            super(fm);
            mNavModels = navModels;
        }

        @Override
        public Fragment getItem(int position) {
            return HomeVideoContentFragment.newInstance(mNavModels.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mNavModels.get(position).getTitle();
        }

        @Override
        public int getCount() {
            if (mNavModels == null) {
                return 0;
            }
            return mNavModels.size();
        }
    }
}
