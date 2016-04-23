package com.sdk.download.utils;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.download_sdk.download.provider.R;

import java.lang.reflect.Method;
import java.util.Locale;


/**
 * 
 * @author deng.shengjin
 * @version create_time:2014-3-17_下午5:51:17
 * @Description toast工具
 */
public class ToastUtil {

	public static void showToast(Context context, int textRes) {
		showToast(context, context.getString(textRes), Toast.LENGTH_SHORT);
	}

	public static void showToast(Context context, int textRes, int toastDuration) {
		showToast(context, context.getString(textRes), toastDuration);
	}

	public static void showToast(Context context, String text) {
		showToast(context, text, Toast.LENGTH_SHORT);
	}

	public static void showToast(Context context, String text, int duration) {
		View view = LayoutInflater.from(context).inflate(R.layout.sdk_download_layout_toast, null);
		TextView textView = (TextView) view.findViewById(R.id.toast_text);
		textView.setText(text);
		Toast toast = new Toast(context);
		if (hasSmartBar()) {
			toast.setGravity(Gravity.BOTTOM, 0, 200);
		} else {
			toast.setGravity(Gravity.BOTTOM, 0, 100);
		}

		if (duration < 0) {
			toast.setDuration(Toast.LENGTH_SHORT);
		} else {
			toast.setDuration(duration);
		}
		toast.setView(view);
		toast.show();

	}

	public static boolean isMeizu() {
		String brand = Build.BRAND;
		if (brand == null) {
			return false;
		}

		return brand.toLowerCase(Locale.ENGLISH).indexOf("meizu") > -1;
	}

	public static boolean hasSmartBar() {
		if (!isMeizu()) {
			return false;
		}

		try {
			Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
			return ((Boolean) method.invoke(null)).booleanValue();
		} catch (Exception e) {
		}

		if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
			return false;
		}
		return true;
	}
}
