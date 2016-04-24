package com.zm.shangxueyuan.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.sdk.download.providers.DownloadManager;
import com.squareup.otto.Subscribe;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.model.VideoDownloadModel;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.activity.VideoDetailActivity;
import com.zm.shangxueyuan.ui.activity.VideoTopicActivity;
import com.zm.shangxueyuan.ui.adapter.VideoDownloadAdapter;
import com.zm.shangxueyuan.ui.listener.OnItemClickListener;
import com.zm.shangxueyuan.ui.provider.BusProvider;
import com.zm.shangxueyuan.ui.provider.event.EmptyDataEvent;
import com.zm.shangxueyuan.ui.provider.event.MineDataEditEvent;
import com.zm.shangxueyuan.utils.ImageLoadUtil;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Creator: dengshengjin on 16/4/17 21:19
 * Email: deng.shengjin@zuimeia.com
 */
public abstract class AbsDownloadFragment extends AbsLoadingEmptyFragment {
    private Executor mExecutor = Executors.newFixedThreadPool(5);
    private VideoDownloadAdapter mVideoAdapter;
    private ListView mListView;
    private ImageLoader mImageLoader;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    protected DownloadManager mDownloadManager;

    @Override
    protected void initData() {
        mVideoAdapter = new VideoDownloadAdapter(getApplicationContext(), false, true);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoadUtil.initImageLoader(context);
        }
        mImageLoader = ImageLoader.getInstance();
        mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_video_content, container, false);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListView.setAdapter(mVideoAdapter);
        PauseOnScrollListener listener = new PauseOnScrollListener(mImageLoader, true, true);
        mListView.setOnScrollListener(listener);
        registerDataSetObserver();
        return view;
    }

    @Override
    protected void initWidgetActions() {
        mVideoAdapter.setOnItemClickListener(new OnItemClickListener<VideoDownloadModel>() {

            @Override
            public void onItemClick(View v, final VideoDownloadModel downloadModel, int position) {
                if (mVideoAdapter.isNeedShowDelete()) {
                    showDeleteAlert(downloadModel);
                } else {
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            final VideoModel videoModel = VideoDownloadModel.convert(downloadModel.mVideoId);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (VideoModel.isTopicVideo(videoModel)) {
                                        startActivity(VideoTopicActivity.getIntent(getApplicationContext(), videoModel.getVideoId(), videoModel.getTitle()));
                                    } else {
                                        startActivity(VideoDetailActivity.getIntent(getApplicationContext(), videoModel));
                                    }
                                }
                            });
                        }
                    });

                }
            }
        });
        mVideoAdapter.setOnDownloadItemClickListener(new OnItemClickListener<VideoDownloadModel>() {
            @Override
            public void onItemClick(View v, VideoDownloadModel downloadModel, int position) {
                if (downloadModel == null) {
                    return;
                }
                if (downloadModel.mStatus == DownloadManager.STATUS_PENDING || downloadModel.mStatus == DownloadManager.STATUS_RUNNING) {
                    mDownloadManager.pauseDownload(downloadModel.mDownloadId);
                } else if (downloadModel.mStatus == DownloadManager.STATUS_PAUSED) {
                    mDownloadManager.resumeDownload(downloadModel.mDownloadId);
                }
            }
        });
    }

    private void showDeleteAlert(final VideoDownloadModel downloadModel) {
        new AlertDialog.Builder(getActivity()).setMessage(R.string.video_delete_request).setTitle(R.string.tips)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mVideoAdapter.removeVideoModel(downloadModel);
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadManager.remove(downloadModel.mDownloadId);
                        new File(downloadModel.mFilePath).delete();
                    }
                });

            }
        }).create().show();
    }

    protected String getEmptyTxt() {
        return getString(R.string.history_nodata_str);
    }

    @Subscribe
    public void menuClick(MineDataEditEvent event) {
        onEditEvent();
    }

    protected void onEditEvent() {
        if (mVideoAdapter.isNeedShowDelete()) {// 处于编辑状态-->完成状态
            mVideoAdapter.setNeedShowDelete(false);
        } else {// 处于完成状态-->编辑状态
            mVideoAdapter.setNeedShowDelete(true);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Always unregister when an object no longer should be on the bus.
        try {
            BusProvider.getInstance().unregister(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.no_anim);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.no_anim);
    }

    @Override
    public void onStop() {
        super.onStop();
        onStopFragmentEvent();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unregisterDataSetObserver();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mVideoAdapter != null) {
            mVideoAdapter.clear();
        }
    }

    protected void onStopFragmentEvent() {

    }

    public Executor getExecutor() {
        return mExecutor;
    }

    public Handler getHandler() {
        return mHandler;
    }

    private DataSetObserver mObserver;

    protected void registerDataSetObserver() {
        if (mObserver == null) {
            mObserver = new DataSetObserver() {

                @Override
                public void onChanged() {
                    super.onChanged();
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mVideoAdapter.isEmpty()) {
                                showEmpty(getView(), getEmptyTxt());
                                BusProvider.getInstance().post(new EmptyDataEvent(true));
                            } else {
                                hideEmpty();
                                BusProvider.getInstance().post(new EmptyDataEvent(false));
                            }
                        }
                    });

                }

                @Override
                public void onInvalidated() {
                    super.onInvalidated();
                    onChanged();
                }
            };
        }
        mVideoAdapter.registerDataSetObserver(mObserver);
    }

    protected void unregisterDataSetObserver() {
        mVideoAdapter.unregisterDataSetObserver(mObserver);
    }

    public boolean isEditStatus() {
        return mVideoAdapter.isNeedShowDelete();
    }

    public VideoDownloadAdapter getVideoAdapter() {
        return mVideoAdapter;
    }
}
