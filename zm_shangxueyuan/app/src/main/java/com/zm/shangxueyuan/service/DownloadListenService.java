package com.zm.shangxueyuan.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.sdk.download.providers.DownloadManager;
import com.sdk.download.providers.downloads.Downloads;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.helper.DownloadManagerHelper;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.utils.ToastUtil;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Creator: dengshengjin on 16/4/24 00:09
 * Email: deng.shengjin@zuimeia.com
 */
public class DownloadListenService extends Service {
    private DownloadBroadcastReceiver mDownloadBroadcastReceiver;
    private DownloadContentObserver mDownloadContentObserver;
    private Executor mExecutor = Executors.newCachedThreadPool();

    @Override
    public void onCreate() {
        super.onCreate();
        if (mDownloadBroadcastReceiver == null) {
            mDownloadBroadcastReceiver = new DownloadBroadcastReceiver();
        }
        registerReceiver(mDownloadBroadcastReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        if (mDownloadContentObserver == null) {
            mDownloadContentObserver = new DownloadContentObserver(null);
        }
        getContentResolver().registerContentObserver(Downloads.getContentURI(getApplicationContext()), true, mDownloadContentObserver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDownloadBroadcastReceiver != null) {
            unregisterReceiver(mDownloadBroadcastReceiver);
        }
        if (mDownloadContentObserver != null) {
            getContentResolver().unregisterContentObserver(mDownloadContentObserver);
        }
    }

    private class DownloadContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public DownloadContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            queryDownload();
        }


    }

    private class DownloadBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    queryDownloadReal(downloadId);
                }
            });
        }
    }

    private void queryDownload() {

    }

    private void queryDownloadReal(long downloadId) {
        final Context context = getApplicationContext();
        DownloadManager mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
        final DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        final Handler mHandler = new Handler(Looper.getMainLooper());
        Cursor cursor = null;
        try {
            cursor = mDownloadManager.query(query);
            while (cursor.moveToNext()) {
                String downloadUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DOWNLOAD_URL));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                long videoId = DownloadManagerHelper.getVideoIdByUrl(downloadUrl);
                int videoType = DownloadManagerHelper.getVideoTypeByUrl(downloadUrl);
                final VideoModel videoModel = VideoDBUtil.queryVideo(videoId);
                if (videoModel == null) {
                    continue;
                }
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                    case DownloadManager.STATUS_PENDING:
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        Log.e("", "url= download success");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(context, String.format(context.getString(R.string.video_download_completed), videoModel.getTitleUpload()));
                            }
                        });
                        break;
                    case DownloadManager.STATUS_FAILED:
                        Log.e("", "url= download fail");
                        mDownloadManager.remove(downloadId);
                        new File(StorageHelper.getNativeVideoPath(context, videoModel.getTitleUpload(), videoType)).delete();
                        break;
                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


}
