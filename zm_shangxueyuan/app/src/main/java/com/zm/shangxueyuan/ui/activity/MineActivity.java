package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.squareup.otto.Subscribe;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.ui.fragment.AbsDownloadFragment;
import com.zm.shangxueyuan.ui.fragment.AbsMineFragment;
import com.zm.shangxueyuan.ui.fragment.DownloadFragment;
import com.zm.shangxueyuan.ui.fragment.FavorFragment;
import com.zm.shangxueyuan.ui.fragment.HistoryFragment;
import com.zm.shangxueyuan.ui.provider.BusProvider;
import com.zm.shangxueyuan.ui.provider.event.EmptyDataEvent;
import com.zm.shangxueyuan.ui.provider.event.MineDataEditEvent;

/**
 * Creator: dengshengjin on 16/4/16 12:02
 * Email: deng.shengjin@zuimeia.com
 */
public class MineActivity extends AbsActionBarActivity {
    public static final String MINE_TITLE = "MineTitle";
    public static final String MINE_TYPE = "MineType";
    public static final int MINE_TYPE_DOWNLOAD = 1 << 1;
    public static final int MINE_TYPE_COLLECT = 1 << 2;
    public static final int MINE_TYPE_RECORD_HISTORY = 1 << 3;
    private Fragment mFragment;
    private int mType;

    public static Intent getIntent(Context context, int mineType) {
        return getIntent(context, mineType, null);
    }

    public static Intent getIntent(Context context, int mineType, String title) {
        Intent intent = new Intent(context, MineActivity.class);
        intent.putExtra(MINE_TYPE, mineType);
        if (!TextUtils.isEmpty(title)) {
            intent.putExtra(MINE_TITLE, title);
        }
        return intent;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidgets() {

        mType = getIntent().getIntExtra(MINE_TYPE, 0);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mFragment == null) {
            switch (mType) {
                case MINE_TYPE_RECORD_HISTORY:
                    mFragment = HistoryFragment.newInstance();
                    setActionTitle(R.string.mine_record);
                    break;
                case MINE_TYPE_DOWNLOAD:
                    mFragment = DownloadFragment.newInstance();
                    setActionTitle(R.string.mine_download);
                    break;
                case MINE_TYPE_COLLECT:
                    mFragment = FavorFragment.newInstance();
                    setActionTitle(R.string.mine_favor);
                    break;
                default:
                    break;
            }
        }
        if (mFragment != null) {
            ft.replace(R.id.content_box, mFragment);
        }
        ft.commit();
    }


    private void onEditAction() {
        updateRightUI();
        BusProvider.getInstance().post(new MineDataEditEvent());
    }

    private View.OnClickListener mRightClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onEditAction();
        }
    };

    private void updateRightUI() {
        if (mFragment != null) {
            if (isEditStatus()) {//从编辑-》完成
                setActionTools(R.string.finish, mRightClickListener);
            } else {//进入编辑状态 从完成-》编辑
                setActionTools(R.string.edit, mRightClickListener);
            }
        }

    }

    private boolean isEditStatus() {
        if (mFragment instanceof AbsMineFragment) {
            return ((AbsMineFragment) mFragment).isEditStatus();
        } else if (mFragment instanceof AbsDownloadFragment) {
            return ((AbsDownloadFragment) mFragment).isEditStatus();
        }
        return false;
    }

    @Override
    protected void initWidgetsActions() {

    }

    @Subscribe
    public void onEmptyDataEvent(EmptyDataEvent event) {
        Log.e("", "DownloadChangeObserver onEmptyDataEvent " + "," + event.isEmpty());
        if (!event.isEmpty()) {
            updateRightUI();
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_mine;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Always unregister when an object no longer should be on the bus.
        try {
            BusProvider.getInstance().unregister(this);
        } catch (Exception e) {
        }
    }
}
