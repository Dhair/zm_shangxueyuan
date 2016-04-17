package com.zm.shangxueyuan.ui.fragment;

import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.provider.event.MineDataEditEvent;

import java.util.List;

public class FavorFragment extends AbsMineFragment {

    public static FavorFragment newInstance() {
        FavorFragment fragment = new FavorFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected List<VideoModel> getVideoList() {
        return VideoDBUtil.queryCollectedVideos();
    }


    protected String getEmptyTxt() {
        return getString(R.string.fav_nodata_str);
    }

    @Subscribe
    public void menuClick(MineDataEditEvent event) {
        onEditEvent();
    }

    @Override
    protected void onStopFragmentEvent() {
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                VideoDBUtil.collectDelete(getSelectedIds());
            }
        });
    }
}