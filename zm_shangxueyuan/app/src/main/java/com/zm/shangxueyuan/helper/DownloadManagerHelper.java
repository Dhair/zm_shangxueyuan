package com.zm.shangxueyuan.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.sdk.download.providers.DownloadManager;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.VideoStatusModel;
import com.zm.shangxueyuan.utils.ToastUtil;
import com.zm.utils.IOUtil;

import java.io.File;
import java.net.URLDecoder;

/**
 * Creator: dengshengjin on 16/4/23 18:15
 * Email: deng.shengjin@zuimeia.com
 */
public class DownloadManagerHelper {
    public static final String VIDEO_ID = "video_remark_id";
    public static final String VIDEO_DOWNLOAD_TYPE = "video_download_type";
    private static final String SEPARATOR = File.separator;

    public static long getVideoIdByUrl(String url) {
        Uri uri = Uri.parse(url);
        String id = uri.getQueryParameter(VIDEO_ID);
        try {
            return Long.parseLong(id);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getVideoTypeByUrl(String url) {
        Uri uri = Uri.parse(url);
        String id = uri.getQueryParameter(VIDEO_DOWNLOAD_TYPE);
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getVideoDownloadURL(String titleUpload, long videoId, int videoType) {
        String playTypeStr = "";
        if (videoType == CommonConstant.SD_MODE) {
            playTypeStr = "ld";
        } else if (videoType == CommonConstant.HD_MODE) {
            playTypeStr = "sd";
        } else if (videoType == CommonConstant.UD_MODE) {
            playTypeStr = "hd";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format(CommonConstant.VIDEO_RES_URL, titleUpload, playTypeStr));
        stringBuilder.append("?");
        stringBuilder.append(VIDEO_ID);
        stringBuilder.append("=");
        stringBuilder.append(videoId);
        stringBuilder.append("&");
        stringBuilder.append(VIDEO_DOWNLOAD_TYPE);
        stringBuilder.append("=");
        stringBuilder.append(videoType);
        return stringBuilder.toString();
    }

    public static String getVideoFilePath(Context context, long videoId, int playType) {
        return downloadedVideo(context, videoId, playType);
    }

    public static boolean continueDownloadVideo(final Context context, long videoId, final String titleUpload, int downloadType) {
        DownloadManager mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
        final Handler mHandler = new Handler(Looper.getMainLooper());
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = null;
        boolean continueDownloadVideo = false;
        boolean hasDownloadRecord = false;
        try {
            cursor = mDownloadManager.query(query);
            while (cursor.moveToNext()) {
                String downloadUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DOWNLOAD_URL));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                long serverVideoId = DownloadManagerHelper.getVideoIdByUrl(downloadUrl);
                int serverVideoType = DownloadManagerHelper.getVideoTypeByUrl(downloadUrl);
                String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                try {
                    localUri = URLDecoder.decode(localUri, "UTF-8");
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                localUri = getFilePath(localUri);
                if (videoId == serverVideoId && serverVideoType == downloadType) {
                    hasDownloadRecord = true;
                    VideoStatusModel videoStatusModel = VideoDBUtil.queryVideoStatus(videoId);
                    if (videoStatusModel == null) {
                        continue;
                    }
                    switch (status) {
                        case DownloadManager.STATUS_PAUSED:
                        case DownloadManager.STATUS_PENDING:
                        case DownloadManager.STATUS_RUNNING:
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(context, String.format(context.getString(R.string.video_downloading), titleUpload));
                                }
                            });
                            continueDownloadVideo = false;
                            break;
                        case DownloadManager.STATUS_SUCCESSFUL:
                            if (IOUtil.isFileExist(localUri)) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(context, String.format(context.getString(R.string.video_download_finish), titleUpload));
                                    }
                                });
                                continueDownloadVideo = false;
                            } else {
                                continueDownloadVideo = true;
                            }
                            break;
                        case DownloadManager.STATUS_FAILED:
                            new File(localUri).delete();//下载失败则删除临文件
                            continueDownloadVideo = true;
                            break;
                    }
                }

            }
            if (!hasDownloadRecord) {
                new File(StorageHelper.getNativeVideoPath(context, titleUpload, downloadType)).delete();//没有任何下载记录则删除临时文件
                continueDownloadVideo = true;
            }
        } catch (Throwable t) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return continueDownloadVideo;

    }

    public static String downloadedVideo(final Context context, long videoId, int videoType) {
        DownloadManager mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = null;
        String downloadedVideo = "";
        try {
            cursor = mDownloadManager.query(query);
            while (cursor.moveToNext()) {
                String downloadUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DOWNLOAD_URL));
                int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                long serverVideoId = DownloadManagerHelper.getVideoIdByUrl(downloadUrl);
                int serverVideoType = DownloadManagerHelper.getVideoTypeByUrl(downloadUrl);
                String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                localUri = URLDecoder.decode(localUri, "UTF-8");
                Log.i("", "native url=(" + serverVideoId + videoId + "),(" + serverVideoType + videoType + ")," + status + "," + localUri);
                if (videoId == serverVideoId && serverVideoType == videoType && status == DownloadManager.STATUS_SUCCESSFUL) {
                    localUri = getFilePath(localUri);
                    if (IOUtil.isFileExist(localUri)) {
                        downloadedVideo = localUri;
                    }
                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return downloadedVideo;

    }

    public static boolean hasDownloadRecord(final Context context, long videoId) {
        DownloadManager mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
        DownloadManager.Query query = new DownloadManager.Query();
        Cursor cursor = null;
        boolean hasDownloadRecord = false;
        try {
            cursor = mDownloadManager.query(query);
            while (cursor.moveToNext()) {
                String downloadUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DOWNLOAD_URL));
                long serverVideoId = DownloadManagerHelper.getVideoIdByUrl(downloadUrl);
                if (videoId == serverVideoId) {
                    hasDownloadRecord = true;
                }

            }
        } catch (Throwable t) {
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return hasDownloadRecord;

    }

    public static String getFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        if (filePath.startsWith("file://")) {
            return filePath.replace("file://", "");
        }
        return filePath;
    }
}
