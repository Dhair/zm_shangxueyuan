package com.zm.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

public class DeviceUtil {

    public static boolean isMiui(Context context) {

        if ("xiaomi".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }

        Intent i = new Intent("miui.intent.action.APP_PERM_EDITOR");
        i.setClassName("com.android.settings", "com.miui.securitycenter.permission.AppPermissionsEditor");
        if (isIntentAvailable(context, i)) {
            return true;
        }
        //双重判断
        boolean isMiUi = "miui".equalsIgnoreCase(Build.ID);

        if ("xiaomi".equalsIgnoreCase(Build.BRAND)) {
            isMiUi = true;
        }
        if (Build.MODEL != null) {
            String str = Build.MODEL.toLowerCase();
            if (str.contains("xiaomi")) {
                isMiUi = true;
            }
            if (str.contains("miui")) {
                isMiUi = true;
            }
        }
        return isMiUi;
    }

    public static boolean isSumsungV4_4_4() {
        if (isSamsung()) {
            if (Build.VERSION.RELEASE.startsWith("4.4.4")) {
                return true;
            } else if (Build.VERSION.RELEASE.startsWith("4.4.2") && Build.DEVICE.startsWith("klte")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSumsungV5() {
        if (isSamsung()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSumsungCorePrime() {
        if (isSamsung()) {
            if (Build.DISPLAY.contains("G3608ZMU1AOA4")) {
                return true;
            }
        }
        return false;
    }

    public static boolean isFlyme() {
        return Build.DISPLAY.startsWith("Flyme");
    }

    public static boolean isFlyme4() {
        return Build.DISPLAY.startsWith("Flyme OS 4");
    }

    public static boolean isFlyme2() {
        return Build.DISPLAY.startsWith("Flyme 2");
    }

    public static boolean isOnePlusLOLLIPOP() {
        return Build.BRAND.equals("ONEPLUS") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isMiuiV6(Context context) {
        if (isMiui(context)) {
            if ("V6".equalsIgnoreCase(SysUtil.getSystemProperty("ro.miui.ui.version.name"))) {
                return true;
            }
        }

        return false;
    }

    public static boolean isMiuiV7(Context context) {
        if (isMiui(context)) {
            if ("V7".equalsIgnoreCase(SysUtil.getSystemProperty("ro.miui.ui.version.name"))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isIntentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_ACTIVITIES);
        return list.size() > 0;
    }

    public static boolean isSamsung() {
        if ("samsung".equalsIgnoreCase(Build.BRAND) || "samsung".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }

        return false;
    }

    public static boolean isLG() {
        if ("lge".equalsIgnoreCase(Build.BRAND) || "lge".equalsIgnoreCase(Build.MANUFACTURER)) {
            if (Build.MODEL != null) {
                String str = Build.MODEL.toLowerCase();
                if (str.contains("lg")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isMeizuMx3() {
        if (isMeizu()) {
            return "mx3".equalsIgnoreCase(Build.DEVICE);
        }

        return false;
    }

    public static boolean isHtcOs() {
        if (Build.BRAND != null && Build.BRAND.toLowerCase().contains("htc")
                && Build.MANUFACTURER != null && Build.MANUFACTURER.toLowerCase().contains("htc")
                && Build.MODEL != null && Build.MODEL.toLowerCase().contains("htc")) {
            return true;
        }
        return false;
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

    @SuppressLint("NewApi")
    public static boolean hasVirtualButtons(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            boolean hasPermanentMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
            boolean hasVirtualButtons = !hasPermanentMenuKey;
            return hasVirtualButtons;
        } else {
            return false;
        }
    }

    public static String getDeviceId(Context context) {
        String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        if (TextUtils.isEmpty(deviceId) || "000000000000000".equals(deviceId)) {
            WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            deviceId = wm.getConnectionInfo().getMacAddress();
        }

        return deviceId;
    }

    public static boolean isLenovo() {
        if ("lenovo".equalsIgnoreCase(Build.BRAND) || "lenovo".equalsIgnoreCase(Build.MANUFACTURER)) {
            return true;
        }
        return false;
    }

    public static long getTotalMemory() {
        String memInfoFile = "/proc/meminfo";// 系统内存信息文件

        long initial_memory = 0;

        try {
            FileReader localFileReader = new FileReader(memInfoFile);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            String totalMemInfo = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            String[] memInfos = totalMemInfo.split("\\s+");

            initial_memory = Integer.valueOf(memInfos[1]).intValue() * 1024;// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();

//			Formatter.formatFileSize(getBaseContext(), initial_memory);// Byte转换为KB或者MB，内存大小规格化
        } catch (Exception e) {
        }

        return initial_memory;
    }

    public static boolean setMIUIStatusBarDarkMode(Activity activity, boolean darkMode) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), darkMode ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean setFlymeStatusBarDarkIcon(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class
                        .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class
                        .getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }
}
