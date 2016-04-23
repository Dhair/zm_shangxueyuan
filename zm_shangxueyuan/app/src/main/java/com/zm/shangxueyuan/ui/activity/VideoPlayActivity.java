package com.zm.shangxueyuan.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.VideoView;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.helper.DownloadManagerHelper;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;
import com.zm.shangxueyuan.utils.MobclickAgentUtil;
import com.zm.shangxueyuan.utils.ToastUtil;
import com.zm.shangxueyuan.utils.network.HttpUtils;

public class VideoPlayActivity extends AbsActivity implements SurfaceHolder.Callback {
    private VideoView mVideoView;
    private ProgressDialog progressDialog;
    private MediaController controller;
    public static final String STATUS_MODEL = "mStatusModel";
    public static final String VIDEO_MODEL = "videoModel";
    private VideoStatusModel mStatusModel;
    private VideoModel videoModel;
    private String videoUrl;
    private AlertDialog.Builder builder;

    public static Intent getIntent(Context context, VideoStatusModel statusModel, VideoModel videoModel) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(STATUS_MODEL, statusModel);
        intent.putExtra(VIDEO_MODEL, videoModel);
        return intent;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    finish();
                    break;
                case 1:
                    if (mVideoView != null && mVideoView.isPlaying() && mVideoView.getCurrentPosition() > 0) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    } else {
                        mHandler.removeMessages(1);
                        mHandler.sendEmptyMessageDelayed(1, 200);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        mStatusModel = (VideoStatusModel) getIntent().getSerializableExtra(STATUS_MODEL);
        videoModel = (VideoModel) getIntent().getSerializableExtra(VIDEO_MODEL);
        videoUrl = StorageHelper.getVideoURL(videoModel.getTitleUpload(), mStatusModel.getPlayType());
        builder = new AlertDialog.Builder(VideoPlayActivity.this).setTitle(R.string.tips).setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        finish();
                    }

                });
    }

    @Override
    protected void onPreSetContentView() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_video_play;
    }

    @Override
    protected void initWidgets() {


        mVideoView = (VideoView) findViewById(R.id.surface_view);
        String nativeVideoUrl = DownloadManagerHelper.getVideoFilePath(getApplicationContext(), videoModel, mStatusModel);

        if (!TextUtils.isEmpty(nativeVideoUrl)) {// 文件存在且下载标记
            Log.i("", "native url=" + nativeVideoUrl);
            mVideoView.setVideoPath(nativeVideoUrl);
        } else {
            Log.i("", "server url=" + videoUrl);
            if (HttpUtils.isMobileDataEnable(getApplicationContext()) && CommonConstant.IS_WARN_2G_3G) {
                builder.setMessage(getString(R.string.net_2_3g));
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mVideoView.setVideoURI(Uri.parse(videoUrl));

                        controller = new MediaController(VideoPlayActivity.this);
                        controller.setMediaPlayer(mVideoView);
                        controller.setAnchorView(mVideoView);
                        mVideoView.setMediaController(controller);
                        mVideoView.getHolder().addCallback(VideoPlayActivity.this);
                        mVideoView.requestFocus();
                        mVideoView.setKeepScreenOn(true);

                    }
                });
                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        finish();
                    }
                });
                builder.create().show();
                return;
            } else {
                mVideoView.setVideoURI(Uri.parse(videoUrl));
            }
        }
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
        progressDialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
                return true;
            }
        });
        if (!isFinishing()) {
            if (progressDialog != null) {
                progressDialog.show();
            }
        }
        controller = new MediaController(VideoPlayActivity.this);
        controller.setMediaPlayer(mVideoView);
        controller.setAnchorView(mVideoView);
        mVideoView.setMediaController(controller);
        mVideoView.getHolder().addCallback(this);
        mVideoView.requestFocus();
        mVideoView.setKeepScreenOn(true);

    }

    @Override
    protected void initWidgetsActions() {

        mVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.start();
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                mStatusModel.setPlayDate(System.currentTimeMillis());
                mStatusModel.save();
                MobclickAgentUtil.playClick(getApplicationContext(), videoModel.getTitle());
            }
        });

        mVideoView.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer player) {
                finish();
            }
        });

        mVideoView.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer player, int position, int id) {
                ToastUtil.showToast(getApplicationContext(), getString(R.string.video_play_fail));
                finish();
                return true;
            }
        });

    }

    ;

    @Override
    protected void onPause() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.destroyDrawingCache();
        }
        if (progressDialog != null) {
            progressDialog.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        controller.show(3000);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder arg0) {
        finish();
    }

}
