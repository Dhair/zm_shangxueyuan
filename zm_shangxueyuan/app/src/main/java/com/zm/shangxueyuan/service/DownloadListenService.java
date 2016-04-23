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
import android.support.annotation.Nullable;

import com.sdk.download.providers.DownloadManager;
import com.sdk.download.providers.downloads.Downloads;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.helper.DownloadManagerHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;

import java.util.Calendar;
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
            queryDownload();
        }
    }

    private void queryDownload() {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                queryDownloadReal();
            }
        });

    }

    private void queryDownloadReal() {
        Context context = getApplicationContext();
        DownloadManager mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
        final DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = null;
        try {
            cursor = mDownloadManager.query(query);
            while (cursor.moveToNext()) {
                String downloadUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DOWNLOAD_URL));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                long videoId = DownloadManagerHelper.getVideoIdByUrl(downloadUrl);
                VideoStatusModel videoStatusModel = VideoDBUtil.queryVideoStatus(videoId);
                if (videoStatusModel == null) {
                    continue;
                }
                VideoModel videoModel = VideoDBUtil.queryVideo(videoId);
                if (videoModel == null) {
                    continue;
                }
                switch (status) {
                    case DownloadManager.STATUS_PAUSED:
                    case DownloadManager.STATUS_PENDING:
                    case DownloadManager.STATUS_RUNNING:
                        break;
                    case DownloadManager.STATUS_SUCCESSFUL:
                        if (videoStatusModel.getDownloadStatus() == CommonConstant.DOWN_FINISH) {
                            continue;
                        }
                        videoStatusModel.setDownloadStatus(CommonConstant.DOWN_FINISH);
                        videoStatusModel.setDownloadDate(Calendar.getInstance().getTimeInMillis());
                        videoStatusModel.save();
                        break;
                    case DownloadManager.STATUS_FAILED:
                        videoStatusModel.setDownloadStatus(CommonConstant.DOWN_NONE);
                        videoStatusModel.setDownloadDate(Calendar.getInstance().getTimeInMillis());
                        videoStatusModel.save();
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
