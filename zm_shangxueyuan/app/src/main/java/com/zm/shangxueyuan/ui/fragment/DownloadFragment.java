package com.zm.shangxueyuan.ui.fragment;

import android.os.Bundle;

import com.squareup.otto.Subscribe;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.adapter.VideoAdapter;
import com.zm.shangxueyuan.ui.provider.event.MineDataEditEvent;

import java.util.List;

public final class DownloadFragment extends AbsMineFragment {

    public static DownloadFragment newInstance() {
        DownloadFragment fragment = new DownloadFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected List<VideoModel> getVideoList() {
        List<VideoModel> videoList = VideoDBUtil.queryDownloadedVideos();
        if (videoList != null) {
            for (int i = 0; i < videoList.size(); i++) {
                VideoModel videoModel = videoList.get(i);
//                if (!StorageHelper.checkDownloaded(getApplicationContext(), videoModel.getTitleUpload())) {
//                    videoList.remove(i);
//                }
            }
        }
        return videoList;
    }

    @Subscribe
    public void menuClick(MineDataEditEvent event) {
        onEditEvent();
    }

    @Override
    protected VideoAdapter getVideoAdapter() {
        return new VideoAdapter(getApplicationContext(), false, true);
    }

    protected String getEmptyTxt() {
        return getString(R.string.download_nodata_str);
    }

    @Override
    protected void onStopFragmentEvent() {
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                VideoDBUtil.downloadDelete(getApplicationContext(), getSelectedIds());
            }
        });
    }

    @Override
    protected void initWidgetActions() {
        super.initWidgetActions();
    }

}
