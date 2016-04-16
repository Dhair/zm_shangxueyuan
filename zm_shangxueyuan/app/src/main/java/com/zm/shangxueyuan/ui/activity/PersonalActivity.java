package com.zm.shangxueyuan.ui.activity;

import android.view.View;
import android.widget.LinearLayout;

import com.zm.shangxueyuan.R;

import butterknife.Bind;

/**
 * Creator: dengshengjin on 16/4/16 12:04
 * Email: deng.shengjin@zuimeia.com
 */
public class PersonalActivity extends AbsActionBarActivity {

    @Bind(R.id.user_login)
    LinearLayout mUserLogin;

    @Bind(R.id.mine_download)
    LinearLayout mDownload;

    @Bind(R.id.mine_history)
    LinearLayout mHistory;

    @Bind(R.id.mine_favor)
    LinearLayout mFavor;

    @Bind(R.id.mine_feedback)
    LinearLayout mFeedback;

    @Bind(R.id.mine_about)
    LinearLayout mAbout;

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidgets() {
        setActionTitle(R.string.personal);
    }

    @Override
    protected void initWidgetsActions() {
        mUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(UserLoginActivity.getIntent(PersonalActivity.this));
            }
        });
        mDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MineActivity.getIntent(PersonalActivity.this, MineActivity.MINE_TYPE_DOWNLOAD));
            }
        });
        mHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MineActivity.getIntent(PersonalActivity.this, MineActivity.MINE_TYPE_RECORD_HISTORY));
            }
        });
        mFavor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MineActivity.getIntent(PersonalActivity.this, MineActivity.MINE_TYPE_COLLECT));
            }
        });
        mFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(FeedbackActivity.getIntent(PersonalActivity.this));
            }
        });
        mAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AboutActivity.getIntent(PersonalActivity.this));
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_personal;
    }
}
