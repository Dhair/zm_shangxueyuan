package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zm.shangxueyuan.R;
import com.zm.utils.KeyBoardUtil;

import butterknife.Bind;

/**
 * Creator: dengshengjin on 16/4/20 08:49
 * Email: deng.shengjin@zuimeia.com
 */
public class SearchActivity extends AbsLoadingEmptyActivity {
    @Bind(R.id.search_edit)
    EditText mKeywordText;

    @Bind(R.id.search_video)
    TextView mSearchVideoText;

    @Bind(R.id.search_gallery)
    TextView mSearchGalleryText;

    @Bind(R.id.search_tips_title)
    TextView mTipsTitleText;

    @Bind(R.id.list_view)
    ListView mListView;

    private Handler mHandler = new Handler();

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initWidgets() {
        setActionTitle(R.string.search_title);
        setActionBackActions(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenSoftKeyboard()) {
                    KeyBoardUtil.hideSoftKeyboard(SearchActivity.this);
                }
            }
        });

    }

    private boolean isOpenSoftKeyboard() {
        if (KeyBoardUtil.isOpenSoftKeyboard(mKeywordText, getApplicationContext())) {
            return true;
        }
        return false;
    }

    @Override
    protected void initWidgetsActions() {
        mSearchVideoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mSearchGalleryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mKeywordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    KeyBoardUtil.hideSoftKeyboard(SearchActivity.this);

                    return true;
                }
                return false;

            }
        });
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                KeyBoardUtil.showSoftKeyboard(mKeywordText, SearchActivity.this);
            }
        }, 300);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }
}
