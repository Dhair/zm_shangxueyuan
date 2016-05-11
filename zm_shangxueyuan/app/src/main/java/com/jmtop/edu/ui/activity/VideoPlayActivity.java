package com.jmtop.edu.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.jmtop.edu.R;
import com.jmtop.edu.constant.CommonConstant;
import com.jmtop.edu.db.VideoDBUtil;
import com.jmtop.edu.helper.DownloadManagerHelper;
import com.jmtop.edu.helper.StorageHelper;
import com.jmtop.edu.model.VideoModel;
import com.jmtop.edu.utils.MobclickAgentUtil;
import com.jmtop.edu.utils.ToastUtil;
import com.jmtop.edu.utils.network.HttpUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class VideoPlayActivity extends AbsActivity implements SurfaceHolder.Callback {
    private VideoView mVideoView;
    private ProgressDialog progressDialog;
    private MediaController controller;
    private static final String VIDEO_MODEL = "videoModel";
    private static final String PLAY_TYPE = "playType";
    private static final String DOWNLOAD_TYPE = "downloadType";
    private int mPlayType;
    private VideoModel videoModel;
    private String videoUrl;
    private AlertDialog.Builder builder;
    private Executor mExecutor = Executors.newCachedThreadPool();

    public static Intent getIntent(Context context, VideoModel videoModel, int playType) {
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(VIDEO_MODEL, videoModel);
        intent.putExtra(PLAY_TYPE, playType);
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
        videoModel = (VideoModel) getIntent().getSerializableExtra(VIDEO_MODEL);
        mPlayType = getIntent().getIntExtra(PLAY_TYPE, CommonConstant.SD_MODE);
        videoUrl = StorageHelper.getVideoURL(videoModel.getTitleUpload(), mPlayType);
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
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final String nativeVideoUrl = DownloadManagerHelper.getVideoFilePath(getApplicationContext(), videoModel.getVideoId(), mPlayType);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
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
                        progressDialog = new ProgressDialog(VideoPlayActivity.this);
                        progressDialog.setMessage(getString(R.string.loading));
                        progressDialog.setCancelable(false);
                        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
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
                        mVideoView.getHolder().addCallback(VideoPlayActivity.this);
                        mVideoView.requestFocus();
                        mVideoView.setKeepScreenOn(true);
                    }
                });
            }
        });

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
                MobclickAgentUtil.playClick(getApplicationContext(), videoModel.getTitle());
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        VideoDBUtil.modifyPlayed(videoModel);
                    }
                });
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
