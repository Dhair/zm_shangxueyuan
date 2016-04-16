package com.zm.shangxueyuan.ui.fragment;

import android.os.Bundle;

import com.activeandroid.ActiveAndroid;
import com.squareup.otto.Subscribe;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.VideoStatusModel;
import com.zm.shangxueyuan.ui.adapter.ContentDeleteItemAdapter;
import com.zm.shangxueyuan.ui.provider.event.ContentEditEvent;

import java.util.Iterator;

public final class HistoryFragment extends FavorFragment {

    public static HistoryFragment newInstance() {
        HistoryFragment fragment = new HistoryFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void getData() {
        adapter = new ContentDeleteItemAdapter(getApplicationContext(), null, imgImageLoader, 0);
        videoList = VideoDBUtil.queryHistoryVideos();
    }

    @Override
    @Subscribe
    public void menuClick(ContentEditEvent event) {
        super.menuClick(event);
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.history_nodata_str);
    }

    /**
     * 设置成未播放
     */
    @Override
    protected void onStopEvent() {
        ActiveAndroid.beginTransaction();
        try {
            for (Iterator<Long> it = set.iterator(); it.hasNext(); ) {
                long videoId = it.next();
                VideoStatusModel videoStatusModel = VideoDBUtil.getStatus(videoId);
                if (videoStatusModel != null) {
                    videoStatusModel.setPlayDate(0l);
                    videoStatusModel.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
