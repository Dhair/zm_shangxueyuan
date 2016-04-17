package com.zm.shangxueyuan.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.helper.MenuNavHelper;
import com.zm.shangxueyuan.model.GalleryCategoryModel;
import com.zm.shangxueyuan.model.NavModel;
import com.zm.shangxueyuan.ui.adapter.GalleryNavAdapter;
import com.zm.shangxueyuan.ui.adapter.NavAdapter;
import com.zm.shangxueyuan.ui.provider.BusProvider;
import com.zm.shangxueyuan.ui.provider.event.MenuClickedEvent;
import com.zm.shangxueyuan.ui.provider.event.MenuControlEvent;
import com.zm.shangxueyuan.ui.provider.event.MenuNavInitedEvent;
import com.zm.utils.PhoneUtil;

import java.util.List;

public class HomeMenuFragment extends BaseFragment {
    private GridView mVideoGrid, mGalleryGrid;
    private NavAdapter mVideoAdapter;
    private GalleryNavAdapter mGalleryAdapter;
    private int mMenuWidth;
    private LinearLayout mGalleryTitleBox;

    @Override
    protected void initData() {
        mMenuWidth = PhoneUtil.getDisplayWidth(getContext())
                - getResources().getDimensionPixelOffset(R.dimen.menu_margin) * 2
                - getResources().getDimensionPixelOffset(R.dimen.slidingmenu_offset)
                - getResources().getDimensionPixelOffset(R.dimen.menu_spacing) * 2;
        mMenuWidth = (int) (mMenuWidth / 3.0f);
        mVideoAdapter = new NavAdapter(getApplicationContext(), mMenuWidth);
        mGalleryAdapter = new GalleryNavAdapter(getApplicationContext(), mMenuWidth);
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_menu, container, false);
        mVideoGrid = (GridView) view.findViewById(R.id.video_grid);
        mVideoGrid.setAdapter(mVideoAdapter);
        mGalleryGrid = (GridView) view.findViewById(R.id.gallery_grid);
        mGalleryGrid.setAdapter(mGalleryAdapter);
        mGalleryTitleBox = (LinearLayout) view.findViewById(R.id.gallery_title_box);
        mGalleryTitleBox.setVisibility(View.GONE);
        updateStatusBarHeightV19(view);
        return view;
    }

    private void updateStatusBarHeightV19(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View space = view.findViewById(R.id.notification_space);
            if (space != null) {
                ViewGroup.LayoutParams lp = space.getLayoutParams();
                lp.height = PhoneUtil.getStatusBarHeight(getApplicationContext());
                space.requestLayout();
            }
        }
    }


    @Override
    protected void initWidgetActions() {
        mVideoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BusProvider.getInstance().post(new MenuControlEvent());
                BusProvider.getInstance().post(new MenuClickedEvent(position));
            }
        });
    }

    @Subscribe
    public void updateNavData(MenuNavInitedEvent menuNavInitedEvent) {
        List<NavModel> videoNavList = MenuNavHelper.getInstance().getVideoNavModels();
        mVideoAdapter.setNavList(videoNavList);
        final List<GalleryCategoryModel> galleryNavList = MenuNavHelper.getInstance().getGalleryNavModels();
        mGalleryAdapter.setNavList(galleryNavList);
        mVideoAdapter.notifyDataSetChanged();
        ViewGroup.LayoutParams videoLp = mVideoGrid.getLayoutParams();
        videoLp.height = mVideoGrid.getWidth();
        mVideoGrid.requestLayout();
        if (galleryNavList != null && !galleryNavList.isEmpty()) {
            mGalleryTitleBox.setVisibility(View.VISIBLE);
            mGalleryAdapter.notifyDataSetChanged();
            ViewGroup.LayoutParams galleryLp = mGalleryGrid.getLayoutParams();
            galleryLp.height = mGalleryGrid.getWidth();
            mGalleryGrid.requestLayout();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
    }

}
