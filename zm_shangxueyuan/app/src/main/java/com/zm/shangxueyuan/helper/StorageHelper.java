package com.zm.shangxueyuan.helper;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.utils.IOUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageHelper {

    public static final boolean DEVELOPER_MODE = false;


    // Image cache name
    private static final String IMAGE_CACHE_DIR_NAME = File.separator + CommonConstant.APP_BASE_DIR_NAME + File.separator + "images" + File.separator;
    private static final String VIDEO_CACHE_DIR_NAME = File.separator + CommonConstant.APP_BASE_DIR_NAME + File.separator + "videos" + File.separator;

    public static String getImgDir(Context context) {
        String baseCacheLocation = IOUtil.getBaseLocalLocation(context);
        String images = baseCacheLocation + IMAGE_CACHE_DIR_NAME;
        return createFileDir(images);
    }

    public static String getVideoDir(Context context) {
        String baseCacheLocation = IOUtil.getBaseLocalLocation(context);
        String images = baseCacheLocation + VIDEO_CACHE_DIR_NAME;
        return createFileDir(images);
    }

    private static String createFileDir(String path) {
        if (!IOUtil.isDirExist(path)) {
            boolean isMakeSuccess = IOUtil.makeDirs(path);
            if (!isMakeSuccess) {
                return "";
            }
        }
        return path;
    }

    // 获取视频url
    public static String getVideoURL(String titleUpload, int videoType) {
        String playTypeStr = "";
        if (videoType == CommonConstant.SD_MODE) {
            playTypeStr = "ld";
        } else if (videoType == CommonConstant.HD_MODE) {
            playTypeStr = "sd";
        } else if (videoType == CommonConstant.UD_MODE) {
            playTypeStr = "hd";
        }
        return String.format(CommonConstant.VIDEO_RES_URL, titleUpload, playTypeStr);
    }

    public static String getImageUrl(String image) {
        return String.format(CommonConstant.PIC_RES_URL, image);
    }

    public static String getLocalVideoFileName(String titleUpload, int videoType) {
        return titleUpload + "_" + videoType + ".mp4";
    }

    public static String getNativeVideoPath(Context context, String titleUpload, int videoType) {
        return getVideoDir(context) + getLocalVideoFileName(titleUpload, videoType);
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
}
