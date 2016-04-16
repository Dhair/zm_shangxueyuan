package com.zm.shangxueyuan.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;

import com.zm.shangxueyuan.R;


public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
        initData();
    }

    private void initData() {
        setCancelable(false);
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    cancel();
                }
                return true;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_dialog_loading);

    }

    @Override
    public void show() {
        if (isShowing()) {
            return;
        }
        super.show();
    }

    @Override
    public void cancel() {
        super.cancel();
    }
}
