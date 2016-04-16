package com.zm.shangxueyuan.helper;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.text.TextUtils;

import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;
import com.zm.utils.IOUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageHelper {

    public static final boolean DEVELOPER_MODE = false;
    private static final int AUDIO_SIGN = 8;
    private static final String VIDEO_ID = "video_remark_id";

    // Image cache name
    private static final String IMAGE_CACHE_DIR_NAME = File.separator + CommonConstant.APP_BASE_DIR_NAME + File.separator + "images" + File.separator;
    private static final String VIDEO_CACHE_DIR_NAME = File.separator + CommonConstant.APP_BASE_DIR_NAME + File.separator + "videos" + File.separator;
    private static final String TEMP_CACHE_DIR_NAME = File.separator + CommonConstant.APP_BASE_DIR_NAME + File.separator + "temp" + File.separator;
    ;

    public static String getImgDir(Context context) {
        String baseCacheLocation = IOUtil.getBaseLocalLocation(context);
        String images = baseCacheLocation + IMAGE_CACHE_DIR_NAME;
        return createFileDir(images);
    }

    public static String getTmpDir(Context context) {
        String baseCacheLocation = IOUtil.getBaseLocalLocation(context);
        String images = baseCacheLocation + TEMP_CACHE_DIR_NAME;
        return createFileDir(images);
    }

    private static String createFileDir(String path) {
        if (!IOUtil.isDirExist(path)) {
            boolean isMakeSucc = IOUtil.makeDirs(path);
            if (!isMakeSucc) {
                return "";
            }
        }
        return path;
    }


    public static String getAudioURL(VideoModel model) {
        String videoName = Uri.encode(model.getTitleUpload());
        String videoUrl = CommonConstant.REQUEST_RES_URL + "video/" + videoName + "/" + videoName + ".mp3";
        videoUrl = videoUrl + "?" + VIDEO_ID + "=" + model.getVideoId();
        return videoUrl;
    }

    // 获取视频url
    public static String getVideoURL(String titleUpload, int playType) {
        String playTypeStr = "";
        if (playType == CommonConstant.SD_MODE) {
            playTypeStr = "ld";
        } else if (playType == CommonConstant.HD_MODE) {
            playTypeStr = "sd";
        } else if (playType == CommonConstant.UD_MODE) {
            playTypeStr = "hd";
        }
        return String.format(CommonConstant.VIDEO_RES_URL, titleUpload, playTypeStr);
    }

    public static String getImageUrl(String image) {
        return String.format(CommonConstant.PIC_RES_URL, image);
    }

    public static String getNativeVideoName(String titleUpload) {
        return titleUpload + ".mp4";
    }

    // 获取视频Id
    public static int getVideoRemarkId(String url) {
        Uri uri = Uri.parse(url);
        String id = uri.getQueryParameter(VIDEO_ID);
        try {
            return Integer.parseInt(id);
        } catch (Exception e) {
            return 0;
        }
    }

    // 获取sd卡存储路径
    public static List<String> getAllSdCardPaths(Context context) {

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState())) {
                List<String> pathList = new ArrayList<>();
                pathList.add(Environment.getExternalStorageDirectory().getPath());
                return pathList;
            } else {
                return null;
            }
        }

        String[] paths;
        Method getVolumePathsMethod;
        try {
            getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
        } catch (NoSuchMethodException e) {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                List<String> pathList = new ArrayList<>();
                pathList.add(Environment.getExternalStorageDirectory().getPath());
                return pathList;
            } else {
                return null;
            }
        }
        try {
            paths = (String[]) getVolumePathsMethod.invoke(context.getSystemService(Context.STORAGE_SERVICE));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }

        if (paths == null || paths.length == 0) {
            return null;
        }
        return Arrays.asList(paths);

    }

    public static boolean checkDownloaded(Context context, String titleUpload) {
        List<String> pathList = getAllSdCardPaths(context);
        for (int i = 0, size = pathList.size(); i < size; i++) {
            String path = pathList.get(i);
            if (IOUtil.isFileExist(path + getNativeVideoName(titleUpload))) {
                return true;
            }
        }
        return false;
    }

    public static String getNativeVideoPath(Context context, String titleUpload) {
        List<String> pathList = getAllSdCardPaths(context);
        for (int i = 0, size = pathList.size(); i < size; i++) {
            String path = pathList.get(i);
            if (IOUtil.isFileExist(path + getNativeVideoName(titleUpload))) {
                return path + getNativeVideoName(titleUpload);
            }
        }
        return null;
    }

    public static boolean checkDownloaded(Context context, VideoModel videoModel, VideoStatusModel statusModel, MyCallBack callback) {
        return checkDownloaded(context, statusModel.getDownloadStatus(), statusModel.getDownId(), videoModel.getTitleUpload(), callback);
    }

    private static boolean checkDownloaded(Context context, long downStatus, long downId, String titleUpload, MyCallBack callback) {
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

    public interface MyCallBack {
        void callBack();
    }

    public static String getDownloadedPath(Context context, VideoModel videoModel, VideoStatusModel statusModel) {
        DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        if (statusModel.getDownloadStatus() == CommonConstant.DOWN_FINISH) {// 下载完成
            String nativeVideoPath = getNativeVideoPath(context, videoModel.getTitleUpload());// 存在
            if (TextUtils.isEmpty(nativeVideoPath)) {
                mDownloadManager.remove(statusModel.getDownId());
                return null;
            }
            if (statusModel.getDownloadType() == statusModel.getPlayType()) {// 播放和下载类型相同
                return nativeVideoPath;
            }
        }
        return null;
    }

    public static boolean isDownloadIng(long downStatus) {
        if (downStatus == CommonConstant.DOWN_ING) {
            return true;
        }
        return false;
    }
}
