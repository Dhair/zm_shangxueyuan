package com.zm.shangxueyuan.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sdk.download.providers.DownloadManager;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.helper.DownloadManagerHelper;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;
import com.zm.shangxueyuan.service.DownloadListenService;
import com.zm.shangxueyuan.utils.CommonUtils;
import com.zm.shangxueyuan.utils.ImageLoadUtil;
import com.zm.shangxueyuan.utils.ToastUtil;
import com.zm.shangxueyuan.utils.network.HttpUtils;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Creator: dengshengjin on 16/4/16 14:55
 * Email: deng.shengjin@zuimeia.com
 */
public class VideoDetailActivity extends AbsActionBarActivity {
    public static final String VIDEO_MODEL = "model";
    private VideoModel mVideoModel;
    private VideoStatusModel mStatusModel;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    @Bind(R.id.detail_img)
    ImageView mDetailImage;

    @Bind(R.id.fav_btn)
    ImageView mFavBtn;

    @Bind(R.id.download_btn)
    ImageView mDownloadBtn;

    @Bind(R.id.share_btn)
    ImageView mShareBtn;

    @Bind(R.id.type_box)
    RelativeLayout mTypeBox;

    @Bind(R.id.type_btn)
    TextView mTypeBtn;

    @Bind(R.id.detail_text)
    TextView mDetailText;

    @Bind(R.id.play_box)
    RelativeLayout mPlayBox;
    DownloadManager mDownloadManager;
    private Executor mExecutor = Executors.newCachedThreadPool();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private AlertDialog.Builder builder;

    public static Intent getIntent(Context context, VideoModel videoModel) {
        Intent intent = new Intent(context, VideoDetailActivity.class);
        intent.putExtra(VideoDetailActivity.VIDEO_MODEL, videoModel);
        return intent;
    }

    @Override
    protected void initData() {
        mVideoModel = (VideoModel) getIntent().getSerializableExtra(VIDEO_MODEL);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoadUtil.initImageLoader(getApplicationContext());
        }
        mImageLoader = ImageLoader.getInstance();
        mOptions = CommonUtils.getDisplayImageOptionsBuilder(R.drawable.play_default).build();
        mImageLoader.displayImage(StorageHelper.getImageUrl(mVideoModel.getImage()), mDetailImage, mOptions);
        mDetailText.setText(mVideoModel.getContent());
        mDownloadManager = new DownloadManager(getApplicationContext(), getContentResolver(), getPackageName());
        builder = new AlertDialog.Builder(VideoDetailActivity.this).setTitle(R.string.tips).setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                });
    }

    @Override
    protected void initWidgets() {
        setActionTitle(mVideoModel.getTitle());
        registerForContextMenu(mTypeBox);
        registerForContextMenu(mDownloadBtn);
    }

    @Override
    protected void initWidgetsActions() {
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShareEvent();
            }
        });
        mTypeBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoModel.getClarity().equals("ld")) {
                    if (mStatusModel != null) {
                        mStatusModel.setPlayType(CommonConstant.SD_MODE);
                    }
                    return;
                }
                v.showContextMenu();
            }
        });
        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoModel.getClarity().equals("ld")) {
                    onDownloadEvent(CommonConstant.SD_MODE);
                    return;
                }
                v.showContextMenu();
            }
        });
        mFavBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFavBtn.isSelected()) {
                    mFavBtn.setSelected(false);
                    if (mStatusModel != null) {
                        mStatusModel.setFavStatus(CommonConstant.UN_FAV_STATUS);
                        mStatusModel.setFavDate(Calendar.getInstance().getTimeInMillis());
                    }
                } else {
                    mFavBtn.setSelected(true);
                    if (mStatusModel != null) {
                        mStatusModel.setFavStatus(CommonConstant.FAV_STATUS);
                        mStatusModel.setFavDate(Calendar.getInstance().getTimeInMillis());
                    }
                }
            }
        });
        mPlayBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(VideoPlayActivity.getIntent(VideoDetailActivity.this, mVideoModel));
                saveVideoStatus();
            }
        });
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mStatusModel = VideoDBUtil.queryVideoStatus(mVideoModel);
                final boolean hasRecord = DownloadManagerHelper.hasRecordDownloadProvider(getApplicationContext(), mStatusModel.getDownId());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        onLoadDataUpdateUI(hasRecord);
                    }
                });

            }
        });
    }

    private void onLoadDataUpdateUI(boolean hasRecord) {
        if (mStatusModel.getFavStatus() == CommonConstant.FAV_STATUS) {
            mFavBtn.setSelected(true);
        } else {
            mFavBtn.setSelected(false);
        }
        if (mStatusModel.getDownloadType() > CommonConstant.DOWN_NONE && mStatusModel.getDownId() > 0 && hasRecord) {
            mDownloadBtn.setSelected(true);
        }
        if (!hasRecord) {
            mStatusModel.setDownloadType(CommonConstant.DOWN_NONE);
        }
        int playType = mStatusModel.getPlayType();
        if (playType <= 0) {
            if (mVideoModel.getClarity().equals("hd")) {//超清
                mTypeBtn.setText(getString(R.string.video_hd_mode));
                mStatusModel.setPlayType(CommonConstant.HD_MODE);
            } else if (mVideoModel.getClarity().equals("sd")) {//超清 or 高清
                mTypeBtn.setText(getString(R.string.video_hd_mode));
                mStatusModel.setPlayType(CommonConstant.HD_MODE);
            } else if (mVideoModel.getClarity().equals("ld")) {//标清
                mTypeBtn.setText(getString(R.string.video_sd_mode));
                mStatusModel.setPlayType(CommonConstant.SD_MODE);
            }
        } else {
            //TODO 判断是否下载完成
            if (playType == CommonConstant.HD_MODE) {
                mTypeBtn.setText(getString(R.string.video_hd_mode));
            } else if (playType == CommonConstant.SD_MODE) {
                mTypeBtn.setText(getString(R.string.video_sd_mode));
            } else if (playType == CommonConstant.UD_MODE) {
                mTypeBtn.setText(getString(R.string.video_ud_mode));
            }
        }

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_video_detail;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == mTypeBox.getId()) { // 播放
            menu.setHeaderTitle(getString(R.string.video_context_menu));
            if (mVideoModel.getClarity().equals("hd")) {//超清
                menu.add(0, 1, Menu.NONE, getString(R.string.video_ud_mode));
                menu.add(0, 2, Menu.NONE, getString(R.string.video_hd_mode));
                menu.add(0, 3, Menu.NONE, getString(R.string.video_sd_mode));
            } else if (mVideoModel.getClarity().equals("sd")) {//超清/高清
                menu.add(0, 2, Menu.NONE, getString(R.string.video_hd_mode));
                menu.add(0, 3, Menu.NONE, getString(R.string.video_sd_mode));
            }
        } else if (v.getId() == mDownloadBtn.getId()) {//下载
            menu.setHeaderTitle(getString(R.string.video_context_menu));
            if (mVideoModel.getClarity().equals("hd")) {//超清
                menu.add(0, 4, Menu.NONE, getString(R.string.video_ud_mode));
                menu.add(0, 5, Menu.NONE, getString(R.string.video_hd_mode));
                menu.add(0, 6, Menu.NONE, getString(R.string.video_sd_mode));
            } else if (mVideoModel.getClarity().equals("sd")) {//超清/高清
                menu.add(0, 5, Menu.NONE, getString(R.string.video_hd_mode));
                menu.add(0, 6, Menu.NONE, getString(R.string.video_sd_mode));
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                if (mStatusModel != null) {
                    mStatusModel.setPlayType(CommonConstant.UD_MODE);
                }
                mTypeBtn.setText(getString(R.string.video_ud_mode));
                break;
            case 2:
                if (mStatusModel != null) {
                    mStatusModel.setPlayType(CommonConstant.HD_MODE);
                }
                mTypeBtn.setText(getString(R.string.video_hd_mode));
                break;
            case 3:
                if (mStatusModel != null) {
                    mStatusModel.setPlayType(CommonConstant.SD_MODE);
                }
                mTypeBtn.setText(getString(R.string.video_sd_mode));
                break;
            case 4:
                onDownloadEvent(CommonConstant.UD_MODE);
                break;
            case 5:
                onDownloadEvent(CommonConstant.HD_MODE);
                break;
            case 6:
                onDownloadEvent(CommonConstant.SD_MODE);
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveVideoStatus();
    }

    private void onShareEvent() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();

        oks.setTitle(getString(R.string.share));
        String videoUrl = StorageHelper.getVideoURL(mVideoModel.getTitleUpload(), mStatusModel.getPlayType());
        oks.setTitleUrl(videoUrl);
        String shareText = String.format(getString(R.string.share_url), mVideoModel.getTitle(), videoUrl);
        oks.setText(shareText);
        oks.setUrl(videoUrl);
        oks.setComment("");
        oks.setSite(getString(R.string.app_name));
        oks.setSiteUrl(videoUrl);
        oks.show(this);
    }

    private boolean onPreDownloadEvent(final int downloadType) {
        if (!HttpUtils.isNetworkAvailable(getApplicationContext())) {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.net_fail));
            return false;
        }
        if (HttpUtils.isMobileDataEnable(getApplicationContext()) && CommonConstant.IS_WARN_2G_3G) {
            builder.setMessage(getString(R.string.net_2_3g));
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    onDownloadEvent(downloadType);
                }
            });
            builder.create().show();
            return false;
        }
        return true;
    }

    private void onDownloadEvent(int downloadType) {
        if (!onPreDownloadEvent(downloadType)) {
            return;
        }
        try {
            String videoUrl = DownloadManagerHelper.getVideoDownloadURL(mVideoModel.getTitleUpload(), mVideoModel.getVideoId(), downloadType);
            Uri srcUri = Uri.parse(videoUrl);
            DownloadManager.Request request = new DownloadManager.Request(srcUri);
            request.setVisibleInDownloadsUi(true);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            File downloadFile = new File(StorageHelper.getNativeVideoPath(getApplicationContext(), mVideoModel.getTitleUpload()));
            request.setDestinationUri(Uri.fromFile(downloadFile));
            request.setDescription(mVideoModel.getTitle());
            request.setTitle(mVideoModel.getTitle());
            long downloadId = mDownloadManager.enqueue(request);
            if (downloadId < 0) {
                ToastUtil.showToast(getApplicationContext(), R.string.download_fail);
                return;
            }
            if (mStatusModel != null) {
                mStatusModel.setDownloadType(downloadType);
                mStatusModel.setDownloadStatus(CommonConstant.DOWN_ING);
                mStatusModel.setDownloadDate(Calendar.getInstance().getTimeInMillis());
                mStatusModel.setDownId(downloadId);
                mDownloadBtn.setSelected(true);
                saveVideoStatus();
                startDownloadListen();
            }
        } catch (Throwable t) {
            t.printStackTrace();
            ToastUtil.showToast(getApplicationContext(), R.string.download_fail);
        }
    }

    //onDestroy onClickPlay onClickDownload
    private void saveVideoStatus() {
        if (mStatusModel != null) {
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    if (mStatusModel != null) {
                        mStatusModel.save();
                    }
                }
            });
        }
    }

    private void startDownloadListen() {
        Intent intent = new Intent(getApplicationContext(), DownloadListenService.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(getPackageName());
        getApplicationContext().startService(intent);
    }
}
