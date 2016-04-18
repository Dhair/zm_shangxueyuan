package com.zm.shangxueyuan.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.otto.Subscribe;
import com.umeng.analytics.MobclickAgent;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.helper.MenuNavHelper;
import com.zm.shangxueyuan.ui.activity.MineActivity;
import com.zm.shangxueyuan.ui.activity.PersonalActivity;
import com.zm.shangxueyuan.ui.provider.BusProvider;
import com.zm.shangxueyuan.ui.provider.event.MenuClickedEvent;
import com.zm.shangxueyuan.ui.provider.event.MenuNavInitedEvent;
import com.zm.utils.PhoneUtil;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-11_上午11:58:00
 * @Description 首页内容页面
 */
public class HomeContentFragment extends BaseFragment {

    private RelativeLayout mDownloadBtn, mFavBtn, mHistoryBtn, mPersonalBtn, mSearchBtn;
    private LinearLayout mTabBox;
    private TextView mVideoTab, mGalleryTab;
    private HomeVideoFragment mHomeVideoFragment;
    private static final String VIDEO_TAG = "videoTAG";
    private HomeGalleryFragment mHomeGalleryFragment;
    private static final String GALLERY_TAG = "galleryTAG";
    private FragmentManager mFm;
    private Bundle mSavedInstanceState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSavedInstanceState = savedInstanceState;
        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void initData() {
        mFm = getChildFragmentManager();
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_content, container, false);
        mDownloadBtn = (RelativeLayout) view.findViewById(R.id.download_btn);
        mFavBtn = (RelativeLayout) view.findViewById(R.id.fav_btn);
        mHistoryBtn = (RelativeLayout) view.findViewById(R.id.history_btn);
        mPersonalBtn = (RelativeLayout) view.findViewById(R.id.personal_btn);
        mSearchBtn = (RelativeLayout) view.findViewById(R.id.search_btn);

        mTabBox = (LinearLayout) view.findViewById(R.id.tab_box);
        mVideoTab = (TextView) view.findViewById(R.id.tab_video);
        mGalleryTab = (TextView) view.findViewById(R.id.tab_gallery);
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
        mPersonalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PersonalActivity.getIntent(getActivity()));
            }
        });
        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MineActivity.getIntent(getActivity(), MineActivity.MINE_TYPE_DOWNLOAD));
            }
        });
        mHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MineActivity.getIntent(getActivity(), MineActivity.MINE_TYPE_RECORD_HISTORY));
            }
        });
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MineActivity.getIntent(getActivity(), MineActivity.MINE_TYPE_COLLECT));
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mVideoTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeToVideoFragment();
                if (mVideoTab.isSelected()) {
                    return;
                }
                mVideoTab.setSelected(true);
                mGalleryTab.setSelected(false);
            }
        });
        mGalleryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeGalleryFragment();
                if (mGalleryTab.isSelected()) {
                    return;
                }
                mVideoTab.setSelected(false);
                mGalleryTab.setSelected(true);
            }
        });
    }

    private Fragment findFragment(String TAG) {
        if (mSavedInstanceState == null) {
            return null;
        }
        return mFm.findFragmentByTag(TAG);
    }

    private void changeToVideoFragment() {
        FragmentTransaction mFt = mFm.beginTransaction();
        if (mHomeVideoFragment == null) {
            mHomeVideoFragment = (HomeVideoFragment) findFragment(VIDEO_TAG);
        }
        if (mHomeVideoFragment == null) {
            mHomeVideoFragment = HomeVideoFragment.newInstance();
            mFt.add(R.id.content_box, mHomeVideoFragment, VIDEO_TAG);
            if (mHomeGalleryFragment != null) {
                mFt.hide(mHomeGalleryFragment);
            }
        } else {
            mFt.show(mHomeVideoFragment);
            if (mHomeGalleryFragment != null) {
                mFt.hide(mHomeGalleryFragment);
            }
        }
        mFt.commit();
    }

    private void changeGalleryFragment() {
        FragmentTransaction mFt = mFm.beginTransaction();
        if (mHomeGalleryFragment == null) {
            mHomeGalleryFragment = (HomeGalleryFragment) findFragment(GALLERY_TAG);
        }
        if (mHomeGalleryFragment == null) {
            mHomeGalleryFragment = HomeGalleryFragment.newInstance();
            mFt.add(R.id.content_box, mHomeGalleryFragment, GALLERY_TAG);
            if (mHomeVideoFragment != null) {
                mFt.hide(mHomeVideoFragment);
            }
        } else {
            mFt.show(mHomeGalleryFragment);
            if (mHomeVideoFragment != null) {
                mFt.hide(mHomeVideoFragment);
            }
        }
        mFt.commit();
    }

    @Subscribe
    public void updateNavData(MenuNavInitedEvent menuNavInitedEvent) {
        if (MenuNavHelper.getInstance().existGalleryNav()) {
            mTabBox.setVisibility(View.VISIBLE);

            mVideoTab.setSelected(true);
            mGalleryTab.setSelected(false);
            changeToVideoFragment();
        }
    }

    @Subscribe
    public void onMenuClickedEvent(MenuClickedEvent menuClickedEvent) {
        if (menuClickedEvent != null) {
            if (menuClickedEvent.getType() == MenuClickedEvent.VIDEO_CLICK) {
                if (mHomeVideoFragment != null) {
                    mHomeVideoFragment.onMenuClickedEvent(menuClickedEvent.getPosition());
                }
                if (mVideoTab != null && mVideoTab.isSelected()) {
                    return;
                }
                changeToVideoFragment();
                mVideoTab.setSelected(true);
                mGalleryTab.setSelected(false);
            } else if (menuClickedEvent.getType() == MenuClickedEvent.GALLERY_CLICK) {
                if (mHomeGalleryFragment != null) {
                    mHomeGalleryFragment.onMenuClickedEvent(menuClickedEvent.getPosition());
                }
                if (mGalleryTab != null && mGalleryTab.isSelected()) {
                    return;
                }
                changeGalleryFragment();
                mVideoTab.setSelected(false);
                mGalleryTab.setSelected(true);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());

    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
    }
}
