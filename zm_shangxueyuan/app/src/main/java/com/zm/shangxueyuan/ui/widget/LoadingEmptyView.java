package com.zm.shangxueyuan.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zm.shangxueyuan.R;


public class LoadingEmptyView extends FrameLayout {
    private Context context;

    public LoadingEmptyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    public LoadingEmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public LoadingEmptyView(Context context) {
        super(context);
        initViews(context);
    }

    private ProgressBar pro;
    private TextView loadText, noDataText;

    private void initViews(Context context) {
        this.context = context;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_loading_empty, this, true);
        pro = (ProgressBar) view.findViewById(R.id.loading_pro);
        loadText = (TextView) view.findViewById(R.id.loading_text);
        noDataText = (TextView) view.findViewById(R.id.loading_no_data_text);
    }

    public void updateEmptyView() {
        updateEmptyView(context.getString(R.string.data_load_empty), null);
    }

    public void updateEmptyView(LoadViewCallback myCallback) {
        updateEmptyView(context.getString(R.string.data_load_try_again), myCallback);
    }

    public void updateLoadView() {
        updateLoadView(context.getString(R.string.loading));
    }

    public void updateLoadView(String str) {
        if (noDataText != null) {
            noDataText.setVisibility(View.GONE);
        }
        if (pro != null) {
            pro.setVisibility(View.VISIBLE);
        }
        if (loadText != null) {
            loadText.setText(str);
            loadText.setVisibility(View.VISIBLE);
        }
    }

    public void updateEmptyView(String str, final LoadViewCallback myCallback) {
        if (noDataText != null) {
            noDataText.setText(str);
            noDataText.setVisibility(View.VISIBLE);
        }
        if (pro != null) {
            pro.setVisibility(View.GONE);
        }
        if (loadText != null) {
            loadText.setVisibility(View.GONE);
        }
        if (myCallback == null) {
            setClickable(false);
            setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });
        } else {
            setClickable(true);
            setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    myCallback.callback();
                }
            });
        }
    }

    public interface LoadViewCallback {
        void callback();
    }

}
