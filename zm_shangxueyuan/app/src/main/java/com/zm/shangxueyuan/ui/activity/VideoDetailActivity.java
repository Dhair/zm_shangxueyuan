package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.content.Intent;
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
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;
import com.zm.shangxueyuan.utils.CommonUtils;
import com.zm.shangxueyuan.utils.ImageLoadUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;

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

    @Bind(R.id.type_button)
    TextView mTypeBtn;

    @Bind(R.id.detail_text)
    TextView mDetailText;

    @Bind(R.id.play_box)
    RelativeLayout mPlayBox;

    private Executor mExecutor = Executors.newCachedThreadPool();
    private Handler mHandler = new Handler(Looper.getMainLooper());

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
    }

    @Override
    protected void initWidgets() {
        setActionTitle(mVideoModel.getTitle());
        registerForContextMenu(mTypeBtn);
        registerForContextMenu(mDownloadBtn);
    }

    @Override
    protected void initWidgetsActions() {
        mTypeBtn.setOnClickListener(new View.OnClickListener() {
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
                        mStatusModel.setFavStatus(CommonConstant.UNFAV_STATUS);
                    }
                } else {
                    mFavBtn.setSelected(true);
                    if (mStatusModel != null) {
                        mStatusModel.setFavStatus(CommonConstant.FAV_STATUS);
                    }
                }
            }
        });
        mPlayBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(VideoPlayActivity.getIntent(VideoDetailActivity.this, mStatusModel, mVideoModel));
            }
        });
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mStatusModel = VideoDBUtil.queryVideoStatus(mVideoModel);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        onLoadDataUpdateUI();
                    }
                });

            }
        });
    }

    private void onLoadDataUpdateUI() {
        if (mStatusModel.getFavStatus() == CommonConstant.FAV_STATUS) {
            mFavBtn.setSelected(true);
        } else {
            mFavBtn.setSelected(false);
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
        if (v.getId() == mTypeBtn.getId()) { // 播放
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

    private void onDownloadEvent(int downloadType) {
        if (mStatusModel != null) {
            mStatusModel.setDownloadType(downloadType);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
}
