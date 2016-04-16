package com.zm.shangxueyuan.receiver;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.UpdateConstant;
import com.zm.shangxueyuan.ui.activity.SplashScreenActivity;


public class AppUpgradeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String url = intent.getStringExtra(UpdateConstant.APK_URL);
        String updateDesc = intent.getStringExtra(UpdateConstant.UPDATE_DESC);
        showUpdateDialog(context, url, updateDesc);
    }

    private void showUpdateDialog(final Context context, final String url, String message) {
        if (context instanceof SplashScreenActivity) {
            return;
        }
        Dialog alertDialog = new AlertDialog.Builder(context).setMessage(message).setTitle(R.string.tips)
                .setNegativeButton(R.string.next_time, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                }).setPositiveButton(R.string.app_upgrade, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        try {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                            context.startActivity(intent);
                        } catch (Exception e) {
                            try {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                context.startActivity(intent);
                            } catch (Exception e1) {
                            }
                        }

                    }
                }).create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}
