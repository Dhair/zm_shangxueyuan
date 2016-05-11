package com.jmtop.edu.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jmtop.edu.R;

public class ToastUtil {
    public static void showToast(Context context, int resId) {
        showToast(context, context.getString(resId));
    }

    public static void showToast(Context context, String text) {
        showToast(context, text, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String text, int duration) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_toast, null);
        TextView textView = (TextView) view.findViewById(R.id.toast_text);
        textView.setText(text);
        Toast toast = new Toast(context);
        if (DeviceUtil.hasSmartBar()) {
            toast.setGravity(Gravity.BOTTOM, 0, 500);
        } else {
            toast.setGravity(Gravity.BOTTOM, 0, 300);
        }

        if (duration < 0) {
            toast.setDuration(Toast.LENGTH_SHORT);
        } else {
            toast.setDuration(duration);
        }
        toast.setView(view);
        toast.show();
    }

}
