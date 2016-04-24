package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.umeng.analytics.MobclickAgent;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.helper.ActivityFinishHelper;
import com.zm.shangxueyuan.helper.AppUpgradeHelper;
import com.zm.utils.PhoneUtil;

import butterknife.ButterKnife;

/**
 * Creator: dengshengjin on 16/4/16 10:05
 * Email: deng.shengjin@zuimeia.com
 */
public abstract class AbsActivity extends AppCompatActivity implements IStatusBar {
    private Context mContext;
    private Handler mHandler;
    private ActivityFinishHelper mActivityFinishHelper;
    private AppUpgradeHelper mAppUpgradeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        onPreSetContentView();
        setContentView(R.layout.activity_abs);
        initAbsData();
        initAbsWidgets();
        ButterKnife.bind(this);
        initActionBarActions();
        initData();
        initWidgets();
        initWidgetsActions();
    }

    private void initAbsData() {
        mActivityFinishHelper = new ActivityFinishHelper(AbsActivity.this);
        mActivityFinishHelper.registerReceiver();
        mAppUpgradeHelper = new AppUpgradeHelper(AbsActivity.this);
    }

    private void initAbsWidgets() {
        updateStatusBarHeightV19();
        updateActionBarColorV19();
        updateStatusBarColorV21();
        updateStatusBarHeightV21();
        updateAbsContentView();
    }

    protected void onPreSetContentView() {

    }

    protected void initActionBarActions() {

    }

    private void updateStatusBarHeightV19() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT && !PhoneUtil.isFullScreen(this)) {
            ViewGroup mStatusBarBox = (ViewGroup) findViewById(R.id.status_bar_box);
            if (mStatusBarBox != null) {
                ViewGroup.LayoutParams lp = mStatusBarBox.getLayoutParams();
                lp.height = PhoneUtil.getStatusBarHeight(getApplicationContext());
                mStatusBarBox.requestLayout();
                if (getStatusBarColor() > 0) {
                    mStatusBarBox.setBackgroundResource(getStatusBarColor());
                } else {
                    mStatusBarBox.setBackgroundResource(R.color.colorPrimaryDark);
                }
            }

        }
    }

    private void updateActionBarColorV19() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            if (getSupportActionBar() != null) {
                if (getStatusBarColor() > 0) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), getStatusBarColor())));
                } else {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)));
                }
            }
        }
    }

    private void updateStatusBarColorV21() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && getStatusBarColor() > 0) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getStatusBarColor());
        }
    }

    private void updateStatusBarHeightV21() {
        boolean isWindowTranslucentStatus = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !PhoneUtil.isFullScreen(this) && isWindowTranslucentStatus) {
            ViewGroup mStatusBarBox = (ViewGroup) findViewById(R.id.status_bar_box);
            if (mStatusBarBox != null) {
                ViewGroup.LayoutParams lp = mStatusBarBox.getLayoutParams();
                lp.height = PhoneUtil.getStatusBarHeight(getApplicationContext());
                mStatusBarBox.requestLayout();
                if (getStatusBarColor() > 0) {
                    mStatusBarBox.setBackgroundResource(getStatusBarColor());
                } else {
                    mStatusBarBox.setBackgroundResource(R.color.colorPrimaryDark);
                }
            }

        }
    }

    private void updateAbsContentView() {
        FrameLayout mAbsBox = (FrameLayout) findViewById(R.id.abs_box);
        if (mAbsBox != null) {
            LayoutInflater mInflater = LayoutInflater.from(AbsActivity.this);
            int contentView = getContentView();
            if (contentView <= 0) {
                throw new NullPointerException("Please return the contentView method");
            }
            mAbsBox.addView(mInflater.inflate(contentView, mAbsBox, false));
        }
    }

    public Context getContext() {
        return mContext;
    }

    public Handler getHandler() {
        if (mHandler == null) {
            synchronized (this) {
                if (mHandler == null) {
                    mHandler = new Handler(Looper.getMainLooper());
                }
            }
        }

        return mHandler;
    }

    protected abstract void initData();

    protected abstract void initWidgets();

    protected abstract void initWidgetsActions();

    @Override
    public int getStatusBarColor() {
        return 0;
    }

    @LayoutRes
    protected abstract int getContentView();


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
        MobclickAgent.onPause(this);
        mAppUpgradeHelper.registerReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if (mActivityFinishHelper != null) {
            mActivityFinishHelper.unregisterReceiver();
            mAppUpgradeHelper.unregisterReceiver();
        }
    }

}
