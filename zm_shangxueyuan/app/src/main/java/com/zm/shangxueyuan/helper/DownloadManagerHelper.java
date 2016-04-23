package com.zm.shangxueyuan.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.sdk.download.providers.DownloadManager;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;
import com.zm.utils.IOUtil;

import java.io.File;

/**
 * Creator: dengshengjin on 16/4/23 18:15
 * Email: deng.shengjin@zuimeia.com
 */
public class DownloadManagerHelper {
    public static final String VIDEO_ID = "video_remark_id";
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
        return stringBuilder.toString();
    }

    public static String getVideoFilePath(Context context, VideoModel videoModel, VideoStatusModel statusModel) {
        DownloadManager mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
        Log.e("", "url=success getVideoFilePath"+statusModel.getDownloadStatus());
        if (statusModel.getDownloadStatus() == CommonConstant.DOWN_FINISH) {
            String nativeVideoPath = StorageHelper.getNativeVideoPath(context, videoModel.getTitleUpload());// 存在
            Log.e("", "url=success getVideoFilePath"+nativeVideoPath+","+statusModel.getDownloadType()+","+ statusModel.getPlayType());
            if (!IOUtil.isFileExist(nativeVideoPath)) {
                mDownloadManager.remove(statusModel.getDownId());
                return null;
            }
            if (statusModel.getDownloadType() == statusModel.getPlayType()) {
                return nativeVideoPath;
            }
        }
        return null;
    }

    public static boolean isDownloaded(Context context, String titleUpload) {
        String downloadDir = StorageHelper.getVideoDir(context);
        if (IOUtil.isFileExist(downloadDir + SEPARATOR + StorageHelper.getLocalVideoFileName(titleUpload))) {
            return true;
        }
        return false;
    }

    public static boolean isDownloaded(Context context, VideoModel videoModel, VideoStatusModel statusModel, DownloadCallback callback) {
        return isDownloaded(context, statusModel.getDownloadStatus(), statusModel.getDownId(), videoModel.getTitleUpload(), callback);
    }

    private static boolean isDownloaded(Context context, long downStatus, long downId, String titleUpload, DownloadCallback callback) {
        DownloadManager mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
        if (downStatus == CommonConstant.DOWN_FINISH) {
            String nativeVideoPath = StorageHelper.getNativeVideoPath(context, titleUpload);
            if (TextUtils.isEmpty(nativeVideoPath)) {
                mDownloadManager.remove(downId);
                if (callback != null) {
                    callback.callBack();
                }
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRecordDownloadProvider(Context context, long downloadId) {
        if (downloadId <= 0) {
            return false;
        }
        DownloadManager mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor c = null;
        boolean hasRecordDownloadProvider = false;
        try {
            c = mDownloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                hasRecordDownloadProvider = true;
            }
        } catch (Throwable t) {
        } finally {
            if (c != null) {
                c.close();
            }
        }

        return hasRecordDownloadProvider;
    }


    public interface DownloadCallback {
        void callBack();
    }
}
