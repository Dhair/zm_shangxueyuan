package com.jmtop.edu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.jmtop.edu.R;
import com.zm.utils.AppUtil;

import butterknife.Bind;

/**
 * Creator: dengshengjin on 16/4/16 10:21
 * Email: deng.shengjin@zuimeia.com
 */
public class AboutActivity extends AbsActionBarActivity {
    @Bind(R.id.version)
    TextView mVersion;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        return intent;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidgets() {
        setActionTitle(R.string.about);
        mVersion.setText(String.format(getString(R.string.version), AppUtil.getVersionName(getContext())));
    }

    @Override
    protected void initWidgetsActions() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_about;
    }
}
