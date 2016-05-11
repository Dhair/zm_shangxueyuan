package com.jmtop.edu.utils;

import java.lang.reflect.Method;
import java.util.Locale;

import android.os.Build;

public class DeviceUtil {
	
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
			Method method = Class.forName("android.os.Build").getMethod(
					"hasSmartBar");
			return ((Boolean) method.invoke(null)).booleanValue();
		} catch (Exception e) {
		}
		
		if (Build.DEVICE.equals("mx") || Build.DEVICE.equals("m9")) {
			return false;
		}
		return true;
	}
}
