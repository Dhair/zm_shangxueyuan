package com.jmtop.edu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.jmtop.edu.R;
import com.jmtop.edu.restful.ReqRestAdapter;
import com.jmtop.edu.restful.RestfulRequest;
import com.jmtop.edu.ui.widget.LoadingDialog;
import com.jmtop.edu.utils.ToastUtil;
import com.zm.utils.KeyBoardUtil;

import org.json.JSONObject;

import butterknife.Bind;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creator: dengshengjin on 16/4/16 12:02
 * Email: deng.shengjin@zuimeia.com
 */
public class FeedbackActivity extends AbsActionBarActivity {

    @Bind(R.id.content_edit)
    EditText mContent;

    @Bind(R.id.phone_edit)
    EditText mPhone;
    private Handler mHandler = new Handler();
    private LoadingDialog mLoadingDialog;
    private RestfulRequest mRequest;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        return intent;
    }

    @Override
    protected void initData() {
        mLoadingDialog = new LoadingDialog(FeedbackActivity.this, R.style.dialog_style);
        mRequest = ReqRestAdapter.getInstance(getContext()).create(RestfulRequest.class);
    }

    @Override
    protected void initWidgets() {
        setActionTitle(R.string.feedback);
        setActionTools(R.string.send, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSendAction();
            }
        });
        setActionBackActions(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenSoftKeyboard()) {
                    KeyBoardUtil.hideSoftKeyboard(FeedbackActivity.this);
                }
            }
        });
    }

    private void onSendAction() {
        String content = mContent.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        String phone = mPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            return;
        }
        mLoadingDialog.show(R.string.feedbacking);
        mRequest.feedback(content, phone, new Callback<JSONObject>() {
            @Override
            public void success(JSONObject jsonObject, Response response) {
                ToastUtil.showToast(getApplicationContext(), R.string.feedback_success);
                if (isFinishing()) {
                    return;
                }
                FeedbackActivity.this.finish();
                mLoadingDialog.cancel();
            }

            @Override
            public void failure(RetrofitError error) {
                ToastUtil.showToast(getApplicationContext(), R.string.feedback_fail);
                if (isFinishing()) {
                    return;
                }
                mLoadingDialog.cancel();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLoadingDialog.cancel();
    }

    private boolean isOpenSoftKeyboard() {
        if (KeyBoardUtil.isOpenSoftKeyboard(mContent, getApplicationContext())) {
            return true;
        }
        if (KeyBoardUtil.isOpenSoftKeyboard(mPhone, getApplicationContext())) {
            return true;
        }

        return false;
    }

    @Override
    protected void initWidgetsActions() {
        mPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    KeyBoardUtil.hideSoftKeyboard(FeedbackActivity.this);
                    onSendAction();
                    return true;
                }
                return false;

            }
        });
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                KeyBoardUtil.showSoftKeyboard(mContent, FeedbackActivity.this);
            }
        }, 300);

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_feedback;
    }
}
