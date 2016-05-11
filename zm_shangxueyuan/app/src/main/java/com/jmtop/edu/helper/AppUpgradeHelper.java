package com.jmtop.edu.helper;

import android.app.Activity;
import android.content.IntentFilter;

import com.jmtop.edu.constant.UpdateConstant;
import com.jmtop.edu.receiver.AppUpgradeBroadcastReceiver;

/**
 * Creator: dengshengjin on 16/4/17 15:14
 * Email: deng.shengjin@zuimeia.com
 */
public class AppUpgradeHelper {
    private AppUpgradeBroadcastReceiver mAppUpgradeBroadcastReceiver;
    private Activity mActivity;

    public AppUpgradeHelper(Activity activity) {
        mActivity = activity;
    }

    public void registerReceiver() {
        if (mAppUpgradeBroadcastReceiver == null) {
            mAppUpgradeBroadcastReceiver = new AppUpgradeBroadcastReceiver();
        }
        IntentFilter finishIntent = new IntentFilter();
        finishIntent.addAction(UpdateConstant.RECEIVER_NAME + mActivity.getPackageName());
        mActivity.registerReceiver(mAppUpgradeBroadcastReceiver, finishIntent);
    }

    public void unregisterReceiver() {
        if (mAppUpgradeBroadcastReceiver != null) {
            mActivity.unregisterReceiver(mAppUpgradeBroadcastReceiver);
        }
    }

}
