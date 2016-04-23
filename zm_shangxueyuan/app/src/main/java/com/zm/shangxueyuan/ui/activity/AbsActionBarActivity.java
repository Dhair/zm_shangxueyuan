package com.zm.shangxueyuan.ui.activity;

import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zm.shangxueyuan.R;

import butterknife.Bind;

/**
 * Creator: dengshengjin on 16/4/16 11:40
 * Email: deng.shengjin@zuimeia.com
 */
public abstract class AbsActionBarActivity extends AbsActivity {

    @Bind(R.id.title_text)
    TextView mTitle;

    @Bind(R.id.header_box)
    RelativeLayout mHeaderBox;

    @Bind(R.id.right_text)
    TextView mRight;

    @Bind(R.id.back_image)
    ImageView mBack;

    private View.OnClickListener mOnBackOnClickListener;


    @Override
    protected void initActionBarActions() {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnBackOnClickListener != null) {
                    mOnBackOnClickListener.onClick(v);
                }
                finish();
            }
        });
    }

    public void setActionBackActions(View.OnClickListener onBackOnClickListener) {
        mOnBackOnClickListener = onBackOnClickListener;
    }

    protected void setActionTitle(int resId) {
        setActionTitle(getString(resId));
    }

    protected void setActionTitle(String res) {
        if (mTitle != null) {
            mTitle.setText(res);
        }
    }

    protected void setActionTools(int resId, View.OnClickListener onClickListener) {
        if (mRight != null) {
            mRight.setVisibility(View.VISIBLE);
            mRight.setText(getString(resId));
            if (onClickListener != null) {
                mRight.setOnClickListener(onClickListener);
            }
        }

    }

    protected void setActionToolsBg(int bgResId,int backResId,int titleResId){
        mHeaderBox.setBackgroundResource(bgResId);
        mBack.setImageResource(backResId);
        mTitle.setTextColor(ContextCompat.getColor(getContext(),titleResId));
    }
}
