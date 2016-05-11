package com.jmtop.edu.ui.fragment;

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
import com.squareup.otto.Subscribe;
import com.jmtop.edu.R;
import com.jmtop.edu.helper.DialogUIHelper;
import com.jmtop.edu.model.UserModel;
import com.jmtop.edu.model.VideoModel;
import com.jmtop.edu.ui.activity.VideoDetailActivity;
import com.jmtop.edu.ui.activity.VideoTopicActivity;
import com.jmtop.edu.ui.adapter.VideoAdapter;
import com.jmtop.edu.ui.listener.OnItemClickListener;
import com.jmtop.edu.ui.provider.BusProvider;
import com.jmtop.edu.ui.provider.event.EmptyDataEvent;
import com.jmtop.edu.ui.provider.event.MineDataEditEvent;
import com.jmtop.edu.utils.ImageLoadUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Creator: dengshengjin on 16/4/17 21:19
 * Email: deng.shengjin@zuimeia.com
 */
public abstract class AbsMineFragment extends AbsLoadingEmptyFragment {
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private Set<Long> mSelectedIds = new HashSet<>();
    private VideoAdapter mVideoAdapter;
    private ListView mListView;
    protected ImageLoader mImageLoader;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void initData() {
        mVideoAdapter = getVideoAdapter();
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoadUtil.initImageLoader(context);
        }
        mImageLoader = ImageLoader.getInstance();
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_video_content, container, false);
        mListView = (ListView) view.findViewById(R.id.list_view);
        mListView.setAdapter(mVideoAdapter);
        PauseOnScrollListener listener = new PauseOnScrollListener(mImageLoader, true, true);
        mListView.setOnScrollListener(listener);
        registerDataSetObserver();
        showLoading(view);
        return view;
    }

    @Override
    protected void initWidgetActions() {
        mVideoAdapter.setOnItemClickListener(new OnItemClickListener<VideoModel>() {

            @Override
            public void onItemClick(View v, VideoModel videoModel, int position) {
                if (mVideoAdapter.isNeedShowDelete()) {
                    showDeleteAlert(videoModel);
                } else {
                    if (videoModel.isLoginValid() && !UserModel.isLogin(getApplicationContext())) {
                        DialogUIHelper.showLoginTips((getActivity()));
                    } else {
                        if (VideoModel.isTopicVideo(videoModel)) {
                            startActivity(VideoTopicActivity.getIntent(getApplicationContext(), videoModel.getVideoId(), videoModel.getTitle()));
                        } else {
                            startActivity(VideoDetailActivity.getIntent(getApplicationContext(), videoModel));
                        }
                    }
                }
            }
        });
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<VideoModel> videoList = getVideoList();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() == null || getActivity().isFinishing()) {
                            return;
                        }
                        hideLoading();
                        mVideoAdapter.setVideoList(videoList);
                        mVideoAdapter.notifyDataSetChanged();
                    }
                }, 500);
            }
        });
    }

    private void showDeleteAlert(final VideoModel videoModel) {
        new AlertDialog.Builder(getActivity()).setMessage(R.string.video_delete_request).setTitle(R.string.tips)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSelectedIds.add(videoModel.getVideoId());
                mVideoAdapter.removeVideoModel(videoModel);
            }
        }).create().show();
    }

    protected VideoAdapter getVideoAdapter() {
        return new VideoAdapter(getApplicationContext(), false);
    }

    protected List<VideoModel> getVideoList() {
        return null;
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

    public Set<Long> getSelectedIds() {
        return mSelectedIds;
    }

    private DataSetObserver mObserver;

    private void registerDataSetObserver() {
        if (mObserver == null) {
            mObserver = new DataSetObserver() {

                @Override
                public void onChanged() {
                    super.onChanged();
                    if (mVideoAdapter.isEmpty()) {
                        showEmpty(getView(), getEmptyTxt());
                        BusProvider.getInstance().post(new EmptyDataEvent(true));
                    } else {
                        hideEmpty();
                        BusProvider.getInstance().post(new EmptyDataEvent(false));
                    }
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

    private void unregisterDataSetObserver() {
        mVideoAdapter.unregisterDataSetObserver(mObserver);
    }

    public boolean isEditStatus() {
        return mVideoAdapter.isNeedShowDelete();
    }
}
