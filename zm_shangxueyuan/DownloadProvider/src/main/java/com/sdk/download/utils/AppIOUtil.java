package com.sdk.download.utils;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * @author deng.shengjin
 * @version create_time:2014-11-18_下午8:54:48
 * @Description 应用工具类
 */
public class AppIOUtil {

	public static Drawable apkInfo(String absPath, Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			absPath = absPath.replace("file://", "");
			PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
			if (pkgInfo != null) {
				ApplicationInfo appInfo = pkgInfo.applicationInfo;
				appInfo.sourceDir = absPath;
				appInfo.publicSourceDir = absPath;
				return pm.getApplicationIcon(appInfo);// 得到图标信息
			}
		} catch (Throwable t) {
		}
		return null;
	}

	public static boolean isApkFile(String filePath) {
		try {
			return filePath.endsWith(".apk");
		} catch (Throwable t) {

		}
		return false;
	}

	public static String getApkPackageName(String absPath, Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			absPath = absPath.replace("file://", "");
			PackageInfo pkgInfo = pm.getPackageArchiveInfo(absPath, PackageManager.GET_ACTIVITIES);
			if (pkgInfo != null) {
				ApplicationInfo appInfo = pkgInfo.applicationInfo;
				appInfo.sourceDir = absPath;
				appInfo.publicSourceDir = absPath;
				return appInfo.packageName;
			}
		} catch (Throwable t) {
		}
		return null;
	}

	public static void installApk(Context context, String filePath) {
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			filePath = filePath.replace("file://", "");
			intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} catch (Throwable t) {
		}
	}

	public static boolean isFileExist(String path) {
		try {
			File file = new File(path);
			return file.exists() && file.isFile();
		} catch (Throwable t) {
			return false;
		}
	}

	public static boolean deleteFile(String path) {
		try {
			File file = new File(path);
			return file.delete();
		} catch (Throwable t) {
			return false;
		}
	}
}
