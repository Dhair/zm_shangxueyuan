package com.jmtop.edu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jmtop.edu.R;
import com.jmtop.edu.constant.CommonConstant;
import com.jmtop.edu.db.SettingDBUtil;
import com.jmtop.edu.model.UserModel;

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

    @Bind(R.id.personal_tips1)
    TextView mPersonalTips1;

    @Bind(R.id.personal_tips2)
    TextView mPersonalTips2;

    @Bind(R.id.logout)
    TextView mLogout;

    @Bind(R.id.user_arrow)
    ImageView mArrow;

    private boolean mIsLogin;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PersonalActivity.class);
        return intent;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidgets() {
        setActionTitle(R.string.personal);
        setActionToolsBg(R.color.colorPrimary, R.drawable.icon_back_white, R.color.white);
        mLogout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserModel userModel = UserModel.parse(getApplicationContext());
        if (userModel != null) {
//            onLoginEvent(userModel);
        } else {
//            onLogoutEvent();
        }
    }

    private void onLoginEvent(UserModel userModel) {
        mIsLogin = true;
        mPersonalTips1.setText(userModel.true_name);
        mPersonalTips2.setText(UserModel.getUserAccount(getApplicationContext()));
        mLogout.setVisibility(View.VISIBLE);
        mArrow.setVisibility(View.GONE);
    }

    private void onLogoutEvent() {
        mIsLogin = false;
        mPersonalTips1.setText(getString(R.string.user_login_title));
        mPersonalTips2.setText(getString(R.string.user_login_tips));
        mLogout.setVisibility(View.GONE);
        mArrow.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initWidgetsActions() {
        mUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsLogin) {
//                    startActivity(UserLoginActivity.getIntent(PersonalActivity.this));
                }
                startActivity(WebViewActivity.getIntent(PersonalActivity.this, CommonConstant.LOGIN_WEB_URl, getString(R.string.user)));
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
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutEvent();
                SettingDBUtil.getInstance(getApplicationContext()).removeUser();
            }
        });
        setHeaderLineInvisible();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_personal;
    }

    @Override
    public int getStatusBarColor() {
        return R.color.colorPrimary;
    }

}
