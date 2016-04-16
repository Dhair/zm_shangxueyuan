package com.zm.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

import java.util.List;

public class AppUtil {

	public static String getAppName(Context context) {
		return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
	}

	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static int getVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getPackageName(Context context) {
		return context.getPackageName();
	}

	public static Drawable getAppIcon(Context context) {
		return context.getApplicationInfo().loadIcon(context.getPackageManager());
	}

	public static int getPid(Context context, String processName) {
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
			if (processName.equals(appProcess.processName)) {
				return appProcess.pid;
			}
		}
		return 0;
	}

	public static String getAppMetaData(Context context, String metaData) {
		ApplicationInfo ai = null;
		try {
			ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
			return ai.metaData.getString(metaData);
		} catch (NameNotFoundException e) {
		}
		return "";
	}

	public static boolean isAppInstalled(Context context, String packageName) {
		PackageInfo packageInfo;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);

		} catch (NameNotFoundException e) {
			packageInfo = null;
		}

		return packageInfo != null;
	}

}
