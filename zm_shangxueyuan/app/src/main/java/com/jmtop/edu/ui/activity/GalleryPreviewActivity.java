package com.jmtop.edu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.jmtop.edu.R;
import com.jmtop.edu.helper.FileTransferService;
import com.jmtop.edu.helper.StorageHelper;
import com.jmtop.edu.model.GalleryModel;
import com.jmtop.edu.ui.fragment.GalleryFragment;
import com.jmtop.edu.ui.widget.HackyViewPager;
import com.jmtop.edu.ui.widget.LoadingDialog;
import com.jmtop.edu.utils.PermissionUtil;
import com.jmtop.edu.utils.ToastUtil;

import java.io.File;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Creator: dengshengjin on 16/4/20 08:48
 * Email: deng.shengjin@zuimeia.com
 */
public class GalleryPreviewActivity extends AbsActionBarActivity {
    public static List<GalleryModel> mGalleryList;
    private static final String MODEL = "model";
    private static final String POSITION = "position";
    private int mPosition;
    private static final String LOCKED = "isLocked";
    @Bind(R.id.view_pager)
    HackyViewPager mViewPager;

    @Bind(R.id.share_btn)
    RelativeLayout mShareBtn;

    @Bind(R.id.download_btn)
    RelativeLayout mDownloadBtn;

    private LoadingDialog mLoadingDialog;
    private GalleryPagerAdapter mPagerAdapter;
    private static final int EXTERNAL_REQUEST_CODE = 1 << 3;
    private Executor mExecutor = Executors.newFixedThreadPool(3);
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private FileTransferService mFileTransferService;

    public static Intent getIntent(Context context, int currPosition, LinkedList<GalleryModel> galleryModels) {
        Intent intent = new Intent(context, GalleryPreviewActivity.class);
        intent.putExtra(MODEL, galleryModels);
        intent.putExtra(POSITION, currPosition);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(LOCKED, false);
            mViewPager.setLocked(isLocked);
        }
    }

    @Override
    protected void initData() {
        mFileTransferService = new FileTransferService(getApplicationContext());
        mPosition = getIntent().getIntExtra(POSITION, 0);
        Object object = getIntent().getSerializableExtra(MODEL);
        if (object != null) {
            mGalleryList = (List<GalleryModel>) object;
        }
        mPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), mGalleryList);
        mLoadingDialog = new LoadingDialog(GalleryPreviewActivity.this, R.style.dialog_style);
    }

    @Override
    protected void initWidgets() {
        setActionTitle(R.string.gallery_detail);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(mPosition, false);
    }

    private boolean isViewPagerActive() {
        return (mViewPager != null && mViewPager instanceof HackyViewPager);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(LOCKED, mViewPager.isLocked());
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void initWidgetsActions() {
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showShare();
            }
        });
        mDownloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!PermissionUtil.requestExternalStorage(GalleryPreviewActivity.this, EXTERNAL_REQUEST_CODE)) {
                    onDownloadEvent();
                }

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_REQUEST_CODE:
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onDownloadEvent();
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.gallery_permission_fail);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void onDownloadEvent() {
        File file = ImageLoader.getInstance().getDiscCache().get(mPagerAdapter.getDetailUrl(mViewPager.getCurrentItem()));
        if (file == null || !file.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.gallery_fail);
            return;
        }
        final GalleryModel galleryModel = mPagerAdapter.getModel(mViewPager.getCurrentItem());
        mLoadingDialog.show(R.string.downloading);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String imageUrl = galleryModel.getImageRealUrl();
                final String destFilePath = StorageHelper.getPictureCacheDir(getApplicationContext());
                final File destFile = new File(destFilePath + "zhongmai_" + Calendar.getInstance().getTimeInMillis() + ".jpg");
                final boolean downloadSuccess = mFileTransferService.downloadFile(imageUrl, destFile);
                final GalleryModel currGalleryModel = mPagerAdapter.getModel(mViewPager.getCurrentItem());
                if (!currGalleryModel.getImageRealUrl().equals(imageUrl)) {
                    return;
                }
                if (downloadSuccess) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingDialog.cancel();
                            ToastUtil.showToast(getApplicationContext(), String.format(getString(R.string.gallery_success), destFilePath));
                        }
                    });
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mLoadingDialog.cancel();
                            ToastUtil.showToast(getApplicationContext(), R.string.gallery_fail);
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadingDialog.cancel();
    }

    private MediaScannerConnection mConnection;

    public boolean addToSysGallery(String fileAbsolutePath, String fileName) {
        try {
            final String resultUrl = MediaStore.Images.Media.insertImage(getContentResolver(), fileAbsolutePath, fileName, fileName);
            mConnection = new MediaScannerConnection(getContext().getApplicationContext(), new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    if (mConnection != null) {
                        String srcUrl = getFilePathByContentResolver(getApplicationContext(), Uri.parse(resultUrl));
                        mConnection.scanFile(srcUrl, "image/jpeg");
                    }
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    if (mConnection != null) {
                        mConnection.disconnect();
                    }
                }
            });
            mConnection.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getFilePathByContentResolver(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        Cursor c = context.getContentResolver().query(uri, null, null, null, null);
        String filePath = null;
        if (null == c) {
            return "";
        }
        try {
            if ((c.getCount() != 1) || !c.moveToFirst()) {
            } else {
                filePath = c.getString(
                        c.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
            }
        } finally {
            c.close();
        }
        return filePath;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gallery_preview;
    }

    private static class GalleryPagerAdapter extends FragmentStatePagerAdapter {
        private List<GalleryModel> mGalleryList;

        public GalleryPagerAdapter(FragmentManager fm, List<GalleryModel> videoList) {
            super(fm);
            mGalleryList = videoList;
        }

        @Override
        public Fragment getItem(int position) {
            return GalleryFragment.newInstance(mGalleryList.get(position));
        }

        public String getDetailUrl(int position) {
            return mGalleryList.get(position).getImageDetailUrl();
        }

        public GalleryModel getModel(int position) {
            return mGalleryList.get(position);
        }


        @Override
        public int getCount() {
            if (mGalleryList == null) {
                return 0;
            }
            return mGalleryList.size();
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        oks.disableSSOWhenAuthorize();
        GalleryModel galleryModel = mPagerAdapter.getModel(mViewPager.getCurrentItem());
        String imageUrl = mPagerAdapter.getDetailUrl(mViewPager.getCurrentItem());
        oks.setImagePath(ImageLoader.getInstance().getDiscCache().get(imageUrl).getAbsolutePath());
        oks.setTitle(galleryModel.getTitleUpload());
        String pictureUrl = galleryModel.getImageRealUrl();
        oks.setTitleUrl(pictureUrl);
        oks.setComment("");
        oks.setSite(getString(R.string.app_name));
        oks.setSiteUrl(pictureUrl);
        oks.show(this);


    }
}
