package com.zm.shangxueyuan.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.ui.activity.UserLoginActivity;

/**
 * Creator: dengshengjin on 16/4/24 15:40
 * Email: deng.shengjin@zuimeia.com
 */
public class DialogUIHelper {
    public static void showLoginTips(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.tips);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });
        builder.setMessage(activity.getString(R.string.login_tips));
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(UserLoginActivity.getIntent(activity));
            }
        });
        builder.create().show();
    }
}
