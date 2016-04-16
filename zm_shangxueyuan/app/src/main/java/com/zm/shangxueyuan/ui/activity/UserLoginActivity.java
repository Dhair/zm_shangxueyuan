package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.zm.shangxueyuan.R;

import butterknife.Bind;

/**
 * Creator: dengshengjin on 16/4/16 12:04
 * Email: deng.shengjin@zuimeia.com
 */
public class UserLoginActivity extends AbsActionBarActivity {
    private String mUrl;

    @Bind(R.id.web_view)
    WebView mWebView;

    @Bind(R.id.loading_box)
    RelativeLayout mLoadingBox;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, UserLoginActivity.class);
        return intent;
    }

    @Override
    protected void initData() {
        mUrl = "http://www.shzhibo.net/wx_edu/#user-login";
    }

    @Override
    protected void initWidgets() {
        setActionTitle(R.string.user);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheMaxSize(1024 * 1024 * 4);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);

        String appCachePath = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setAllowFileAccess(true);
        CookieManager.getInstance().setAcceptCookie(true);

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }

            @Override
            public void onReachedMaxAppCacheSize(long spaceNeeded, long totalUsedQuota, WebStorage.QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(spaceNeeded * 2);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (isFinishing()) {
                    return;
                }
                mLoadingBox.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (isFinishing()) {
                    return;
                }
                mLoadingBox.setVisibility(View.GONE);
            }

        });
        mWebView.loadUrl(mUrl);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (this.mWebView != null) {
                this.mWebView.stopLoading();
                this.mWebView.removeAllViews();
                this.mWebView.destroy();
                this.mWebView = null;
                System.gc();
            }
        } catch (Throwable t) {

        }
    }

    @Override
    protected void initWidgetsActions() {

    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_login;
    }
}
