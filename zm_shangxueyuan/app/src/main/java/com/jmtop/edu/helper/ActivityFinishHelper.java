package com.jmtop.edu.helper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.jmtop.edu.constant.CommonConstant;
import com.jmtop.edu.ui.activity.SplashScreenActivity;

import java.lang.ref.WeakReference;

/**
 * Creator: dengshengjin on 16/4/17 15:14
 * Email: deng.shengjin@zuimeia.com
 */
public class ActivityFinishHelper {
    private WeakReference<Activity> mActivityWeakReference;
    private FinishBroadcastReceiver mFinishBroadcastReceiver;
    private Context mContext;

    public ActivityFinishHelper(Activity mActivity) {
        mContext = mActivity.getApplicationContext();
        mActivityWeakReference = new WeakReference<>(mActivity);
    }

    public void registerReceiver() {
        if (mFinishBroadcastReceiver == null) {
            mFinishBroadcastReceiver = new FinishBroadcastReceiver(mActivityWeakReference);
        }
        IntentFilter finishIntent = new IntentFilter();
        finishIntent.addAction(CommonConstant.FINISH_ACTION_NAME + mContext.getPackageName());
        mContext.registerReceiver(mFinishBroadcastReceiver, finishIntent);
    }

    public void unregisterReceiver() {
        if (mFinishBroadcastReceiver != null) {
            mContext.unregisterReceiver(mFinishBroadcastReceiver);
        }
    }

    final class FinishBroadcastReceiver extends BroadcastReceiver {
        private WeakReference<Activity> mActivityWeakReference;

        public FinishBroadcastReceiver(WeakReference<Activity> activityWeakReference) {
            mActivityWeakReference = activityWeakReference;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(CommonConstant.FINISH_ACTION_NAME + mContext.getPackageName())) {
                Activity activity = mActivityWeakReference.get();
                if (activity != null && !(activity instanceof SplashScreenActivity)) {
                    activity.finish();
                }
            }
        }
    }
}
