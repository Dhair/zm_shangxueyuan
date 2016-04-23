package com.zm.shangxueyuan.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

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
    private static final String VIDEO_ID = "video_remark_id";
    private static final String SEPARATOR = File.separator;

    public static int getVideoIdByUrl(String url) {
        Uri uri = Uri.parse(url);
        String id = uri.getQueryParameter(VIDEO_ID);
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getVideoFilePath(Context context, VideoModel videoModel, VideoStatusModel statusModel) {
        DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        if (statusModel.getDownloadStatus() == CommonConstant.DOWN_FINISH) {
            String nativeVideoPath = StorageHelper.getNativeVideoPath(context, videoModel.getTitleUpload());// 存在
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
        DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
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


    public interface DownloadCallback {
        void callBack();
    }
}
