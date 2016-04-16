package com.zm.shangxueyuan.ui.fragment;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.squareup.otto.Subscribe;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;
import com.zm.shangxueyuan.ui.adapter.ContentDownloadAdapter;
import com.zm.shangxueyuan.ui.adapter.ContentItemAdapter;
import com.zm.shangxueyuan.ui.provider.event.ContentEditEvent;

import java.util.Iterator;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-15_下午11:41:02
 * @Description 我的下载
 */
public final class DownloadFragment extends FavorFragment {
    private final static int GO_NOTIFY = 0;
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (getActivity() == null) {
                return;
            }
            ((ContentItemAdapter) adapter).setVideoList(videoList);
            adapter.notifyDataSetChanged();
            mHandler.removeMessages(GO_NOTIFY);
        }

        ;
    };

    public static DownloadFragment newInstance() {
        DownloadFragment fragment = new DownloadFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void getData() {
        adapter = new ContentDownloadAdapter(getApplicationContext(), null, imgImageLoader, 0);
        videoList = VideoDBUtil.queryDownloadedVideos();
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.download_nodata_str);
    }

    @Override
    @Subscribe
    public void menuClick(ContentEditEvent event) {
        super.menuClick(event);
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_grid, container, false);
        headerGridView = (StickyGridHeadersGridView) view.findViewById(R.id.grid_view);
        headerGridView.setAdapter(adapter);
        PauseOnScrollListener listener = new PauseOnScrollListener(imgImageLoader, true, true);
        headerGridView.setOnScrollListener(listener);
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (videoList != null) {
                    for (int i = 0; i < videoList.size(); i++) {
                        VideoModel videoModel = videoList.get(i);
                        if (!StorageHelper.checkDownloaded(getApplicationContext(), videoModel.getTitleUpload())) {
                            videoList.remove(i);
                        }
                    }
                }
                mHandler.sendEmptyMessage(GO_NOTIFY);

            }

            ;
        }.start();

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        mHandler.removeMessages(GO_NOTIFY);
    }

    /**
     * 设置成未播放
     */
    @Override
    protected void onStopEvent() {
        DownloadManager mDownloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        ActiveAndroid.beginTransaction();
        try {
            for (Iterator<Long> it = set.iterator(); it.hasNext(); ) {
                long videoId = it.next();
                VideoStatusModel videoStatusModel = VideoDBUtil.getStatus(videoId);
                if (videoStatusModel != null) {
                    mDownloadManager.remove(videoStatusModel.getDownId());
                    videoStatusModel.setDownId(0l);
                    videoStatusModel.setDownloadDate(0l);
                    videoStatusModel.setDownloadStatus(CommonConstant.DOWN_NONE);
                    videoStatusModel.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
