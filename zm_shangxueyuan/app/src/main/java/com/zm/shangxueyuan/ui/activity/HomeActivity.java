package com.zm.shangxueyuan.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.ui.fragment.HomeContentFragment;
import com.zm.shangxueyuan.ui.fragment.HomeMenuFragment;

/**
 * Creator: dengshengjin on 16/4/16 22:31
 * Email: deng.shengjin@zuimeia.com
 */
public class HomeActivity extends AbsSlidingActivity {
    private Fragment menuFragment, contentFragment;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        initLogic(bundle);
    }

    private void initLogic(Bundle savedInstanceState) {
        initMenuViews(savedInstanceState);
        initContentViews();
    }

    private void initMenuViews(Bundle savedInstanceState) {
        setBehindContentView(R.layout.activity_home_menu_frame);
        if (savedInstanceState == null) {
            FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
            menuFragment = new HomeMenuFragment();
            t.replace(R.id.menu_frame, menuFragment);
            t.commit();
        } else {
            menuFragment = getSupportFragmentManager().findFragmentById(R.id.menu_frame);
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
}
