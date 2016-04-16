package com.zm.shangxueyuan.utils;

import android.content.Context;

import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.MobClickConstant;

import java.util.HashMap;

public class MobclickAgentUtil {

	// 登录次数
	public static void loginClick(Context context, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(context.getString(R.string.mob_login), value);
		MobclickAgent.onEvent(context, MobClickConstant.USER_STATICS, map);
		Logger.i("", MobClickConstant.USER_STATICS + "Mobclick " + map.toString());
	}

	// 播放次数
	public static void playClick(Context context, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(context.getString(R.string.mob_click_play), value);
		MobclickAgent.onEvent(context, MobClickConstant.VIDEO_STATICS, map);
		Logger.i("", MobClickConstant.VIDEO_STATICS + "Mobclick " + map.toString());
	}

	// 播放次数并登录
	public static void playLoginClick(Context context, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(context.getString(R.string.mob_click_play), value);
		MobclickAgent.onEvent(context, MobClickConstant.USER_STATICS, map);
		Logger.i("", MobClickConstant.USER_STATICS + "Mobclick " + map.toString());
	}

	// 收听次数
	public static void listenClick(Context context, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(context.getString(R.string.mob_click_listen), value);
		MobclickAgent.onEvent(context, MobClickConstant.VIDEO_STATICS, map);
		Logger.i("", MobClickConstant.VIDEO_STATICS + "Mobclick " + map.toString());
	}

	// 收藏次数
	public static void favorClick(Context context, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(context.getString(R.string.mob_click_fav), value);
		MobclickAgent.onEvent(context, MobClickConstant.VIDEO_STATICS, map);
		Logger.i("", MobClickConstant.VIDEO_STATICS + "Mobclick " + map.toString());
	}

	// 下载次数
	public static void downloadClick(Context context, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(context.getString(R.string.mob_click_download), value);
		MobclickAgent.onEvent(context, MobClickConstant.VIDEO_STATICS, map);
		Logger.i("", MobClickConstant.VIDEO_STATICS + "Mobclick " + map.toString());
	}

	// 下载次数并登录
	public static void downloadLoginClick(Context context, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(context.getString(R.string.mob_click_download), value);
		MobclickAgent.onEvent(context, MobClickConstant.USER_STATICS, map);
		Logger.i("", MobClickConstant.USER_STATICS + "Mobclick " + map.toString());
	}

	// video_user_download_by_type
	public static void downloadByType(Context context, String key, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(key, value);
		MobclickAgent.onEvent(context, MobClickConstant.VIDEO_USER_DOWNLOAD_BY_TYPE, map);
		Logger.i("", MobClickConstant.VIDEO_USER_DOWNLOAD_BY_TYPE + "Mobclick " + map.toString());
	}

	// video_user_play_by_type
	public static void playByType(Context context, String key, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(key, value);
		MobclickAgent.onEvent(context, MobClickConstant.VIDEO_USER_PLAY_BY_TYPE, map);
		Logger.i("", MobClickConstant.VIDEO_STATICS + "Mobclick " + map.toString());
	}

	// 分享次数
	public static void shareClick(Context context, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(context.getString(R.string.mob_click_share), value);
		MobclickAgent.onEvent(context, MobClickConstant.VIDEO_STATICS, map);
		Logger.i("", MobClickConstant.VIDEO_STATICS + "Mobclick " + map.toString());
	}

	// 平台分享
	public static void sharePlatmClick(Context context, String platm, String value) {
		HashMap<String, String> map = new HashMap<String, String>();
		platm += context.getString(R.string.share);
		map.put(platm, value);
		MobclickAgent.onEvent(context, MobClickConstant.SHARE_STATICS, map);
		Logger.i("", MobClickConstant.SHARE_STATICS + "Mobclick " + map.toString());
	}
}