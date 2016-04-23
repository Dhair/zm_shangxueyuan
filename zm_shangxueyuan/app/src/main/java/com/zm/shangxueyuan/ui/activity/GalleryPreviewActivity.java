package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.model.GalleryModel;
import com.zm.shangxueyuan.ui.fragment.GalleryFragment;
import com.zm.shangxueyuan.ui.widget.HackyViewPager;
import com.zm.shangxueyuan.utils.PermissionUtil;
import com.zm.shangxueyuan.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import butterknife.Bind;

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

    private GalleryPagerAdapter mPagerAdapter;
    private static final int EXTERNAL_REQUEST_CODE = 1 << 3;

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
        mPosition = getIntent().getIntExtra(POSITION, 0);
        Object object = getIntent().getSerializableExtra(MODEL);
        if (object != null) {
            mGalleryList = (List<GalleryModel>) object;
        }
        mPagerAdapter = new GalleryPagerAdapter(getSupportFragmentManager(), mGalleryList);
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
        String imageUrl = mPagerAdapter.getDetailUrl(mViewPager.getCurrentItem());
        File file = ImageLoader.getInstance().getDiscCache().get(imageUrl);
        if (file == null || !file.exists()) {
            ToastUtil.showToast(getApplicationContext(), R.string.gallery_fail);
            return;
        }
        addToSysGallery(file.getAbsolutePath(), "zhongmei_" + System.currentTimeMillis() + "_shangxueyuan.jpg");
        ToastUtil.showToast(getApplicationContext(), R.string.gallery_success);
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
        } catch (FileNotFoundException e) {
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


        @Override
        public int getCount() {
            if (mGalleryList == null) {
                return 0;
            }
            return mGalleryList.size();
        }
    }
}
