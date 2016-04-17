package com.zm.shangxueyuan.ui.fragment;

import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.provider.event.MineDataEditEvent;

import java.util.List;

public final class HistoryFragment extends AbsMineFragment {

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected List<VideoModel> getVideoList() {
        return VideoDBUtil.queryHistoryVideos();
    }

    @Subscribe
    public void menuClick(MineDataEditEvent event) {
        onEditEvent();
    }

    protected String getEmptyTxt() {
        return getString(R.string.history_nodata_str);
    }

    @Override
    protected void onStopFragmentEvent() {
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                VideoDBUtil.playHistoryDelete(getSelectedIds());
            }
        });
    }
}
