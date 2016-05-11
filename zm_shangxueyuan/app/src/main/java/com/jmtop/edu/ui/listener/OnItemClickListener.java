package com.jmtop.edu.ui.listener;

import android.view.View;

/**
 * Creator: dengshengjin on 16/4/17 18:57
 * Email: deng.shengjin@zuimeia.com
 */
public interface OnItemClickListener<T> {
    void onItemClick(View v, T t, int position);
}
