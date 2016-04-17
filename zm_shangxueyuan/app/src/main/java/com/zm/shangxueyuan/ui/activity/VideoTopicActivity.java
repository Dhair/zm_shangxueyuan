package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.adapter.VideoAdapter;
import com.zm.shangxueyuan.ui.listener.OnItemClickListener;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;

/**
 * Creator: dengshengjin on 16/4/17 18:48
 * Email: deng.shengjin@zuimeia.com
 */
public class VideoTopicActivity extends AbsLoadingEmptyActivity {
    @Bind(R.id.list_view)
    ListView mListView;

    private VideoAdapter mVideoAdapter;
    private Executor mExecutor = Executors.newCachedThreadPool();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static final String TOPIC_ID = "topicId";
    public static final String TOPIC_TITLE = "title";
    private long mTopicId;
    private String mTitle;

    public static Intent getIntent(Context context, long topicId, String title) {
        Intent intent = new Intent(context, VideoTopicActivity.class);
        intent.putExtra(VideoTopicActivity.TOPIC_ID, topicId);
        intent.putExtra(VideoTopicActivity.TOPIC_TITLE, title);
        return intent;
    }

    @Override
    protected void initData() {
        mVideoAdapter = new VideoAdapter(getApplicationContext(), false);
        mTopicId = getIntent().getLongExtra(TOPIC_ID, 0l);
        mTitle = getIntent().getStringExtra(TOPIC_TITLE);
    }

    @Override
    protected void initWidgets() {
        setActionTitle(mTitle);
        mListView.setAdapter(mVideoAdapter);
        showLoading();
    }

    @Override
    protected void initWidgetsActions() {
        mVideoAdapter.setOnItemClickListener(new OnItemClickListener<VideoModel>() {

            @Override
            public void onItemClick(View v, VideoModel videoModel, int position) {
                if (VideoModel.isTopicVideo(videoModel)) {
                    startActivity(VideoTopicActivity.getIntent(getApplicationContext(), videoModel.getVideoId(), videoModel.getTitle()));
                } else {
                    startActivity(VideoDetailActivity.getIntent(getApplicationContext(), videoModel));
                }
            }
        });
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final List<VideoModel> videoList = VideoDBUtil.queryTopicVideos(mTopicId);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
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

    @Override
    protected int getContentView() {
        return R.layout.activity_video_topic;
    }
}
