package com.jmtop.edu.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.squareup.otto.Subscribe;
import com.umeng.message.ALIAS_TYPE;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.jmtop.edu.R;
import com.jmtop.edu.constant.CommonConstant;
import com.jmtop.edu.helper.ActivityFinishHelper;
import com.jmtop.edu.helper.AppUpgradeHelper;
import com.jmtop.edu.helper.MenuNavHelper;
import com.jmtop.edu.ui.fragment.HomeContentFragment;
import com.jmtop.edu.ui.fragment.HomeMenuFragment;
import com.jmtop.edu.ui.provider.BusProvider;
import com.jmtop.edu.ui.provider.event.MenuControlEvent;
import com.jmtop.edu.ui.provider.event.MenuNavInitedEvent;
import com.jmtop.edu.utils.ToastUtil;

/**
 * Creator: dengshengjin on 16/4/16 22:31
 * Email: deng.shengjin@zuimeia.com
 */
public class HomeActivity extends AbsSlidingActivity {
    private Fragment mMenuFragment, contentFragment;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private ActivityFinishHelper mActivityFinishHelper;
    private AppUpgradeHelper mAppUpgradeHelper;
    private PushAgent mPushAgent;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        init(bundle);
        onPushEvent();
    }

    private void onPushEvent() {

        mPushAgent = PushAgent.getInstance(getApplicationContext());
        mPushAgent.enable(new IUmengRegisterCallback() {

            @Override
            public void onRegistered(final String registrationId) {
            }
        });
        mPushAgent.setDebugMode(false);
        new AddAliasTask("All", ALIAS_TYPE.QQ).execute();

    }

    class AddAliasTask extends AsyncTask<Void, Void, Boolean> {

        String alias;
        String aliasType;

        public AddAliasTask(String aliasString, String aliasTypeString) {
            this.alias = aliasString;
            this.aliasType = aliasTypeString;
        }

        protected Boolean doInBackground(Void... params) {
            try {
                return mPushAgent.addAlias(alias, aliasType);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.e("", "AddAliasTask success" + result);
        }

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        MenuNavHelper.getInstance().queryNavData(new MenuNavHelper.NavInitedCallback() {
            @Override
            public void callback() {
                long delayTimeMills = (mMenuFragment != null && mMenuFragment.isVisible()) ? 0l : 100l;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        BusProvider.getInstance().post(new MenuNavInitedEvent());
                    }
                }, delayTimeMills);
            }
        });
    }

    private void init(Bundle savedInstanceState) {
        initMenuViews(savedInstanceState);
        initContentViews();
        mActivityFinishHelper = new ActivityFinishHelper(HomeActivity.this);
        mActivityFinishHelper.registerReceiver();
        mAppUpgradeHelper = new AppUpgradeHelper(HomeActivity.this);
    }

    private void initMenuViews(Bundle savedInstanceState) {
        setBehindContentView(R.layout.activity_home_menu_frame);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            mMenuFragment = new HomeMenuFragment();
            t.replace(R.id.menu_frame, mMenuFragment);
            t.commit();
        } else {
            mMenuFragment = getSupportFragmentManager().findFragmentById(R.id.menu_frame);
        }

        // customize the SlidingMenu
        final SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        slidingMenu.setFadeDegree(0.35f);
        slidingMenu.setBehindScrollScale(0.0f);
        slidingMenu.setShadowDrawable(R.drawable.sliding_menu_shadow);
        slidingMenu.setFadeEnabled(false);
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.setBehindCanvasTransformer(null);

    }

    private void initContentViews() {
        setContentView(R.layout.activity_home_content_frame);
        FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
        contentFragment = new HomeContentFragment();
        t.replace(R.id.content_frame, contentFragment);
        t.commit();
    }

    @Subscribe
    public void onMenuEvent(MenuControlEvent event) {
        final SlidingMenu slidingMenu = getSlidingMenu();
        if (slidingMenu.isMenuShowing()) {
            slidingMenu.showContent();
        } else {
            slidingMenu.showMenu();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
        mAppUpgradeHelper.registerReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Always unregister when an object no longer should be on the bus.
        BusProvider.getInstance().unregister(this);
        mAppUpgradeHelper.unregisterReceiver();
    }

    private long mExitTimeMills = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (System.currentTimeMillis() - mExitTimeMills > 2000) {// 如果两次按键时间间隔大于800毫秒，则不退出
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.exit_warn));
                    mExitTimeMills = System.currentTimeMillis();// 更新firstTime
                    return true;
                } else {
                    Intent intent = new Intent(CommonConstant.FINISH_ACTION_NAME + getPackageName());
                    sendBroadcast(intent);
                    finish();
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mActivityFinishHelper != null) {
            mActivityFinishHelper.unregisterReceiver();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return onMyKeyUp(keyCode, event);
    }

    public boolean onMyKeyUp(int keyCode, KeyEvent event) {
        if (getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.ECLAIR) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()) {
                onBackPressed();
                return true;
            }
        }
        return false;
    }
}
