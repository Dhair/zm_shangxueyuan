package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.content.Intent;

/**
 * Creator: dengshengjin on 16/4/16 14:55
 * Email: deng.shengjin@zuimeia.com
 */
public class VideoDetailActivity extends AbsActionBarActivity {
    public static final String VIDEO_ID = "videoId";

    public static Intent getIntent(Context context, long videoId) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(VideoDetailActivity.VIDEO_ID, videoId);
        return intent;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidgets() {

    }

    @Override
    protected void initWidgetsActions() {

    }

    @Override
    protected int getContentView() {
        return 0;
    }
}
