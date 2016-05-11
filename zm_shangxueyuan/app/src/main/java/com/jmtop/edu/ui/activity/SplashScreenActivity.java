package com.jmtop.edu.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.onlineconfig.UmengOnlineConfigureListener;
import com.jmtop.edu.R;
import com.jmtop.edu.constant.UpdateConstant;
import com.jmtop.edu.db.SettingDBUtil;
import com.jmtop.edu.model.GalleryCategoryModel;
import com.jmtop.edu.model.GalleryTopicModel;
import com.jmtop.edu.model.NavModel;
import com.jmtop.edu.model.VideoModel;
import com.jmtop.edu.restful.ReqRestAdapter;
import com.jmtop.edu.restful.RestfulRequest;
import com.jmtop.edu.utils.PermissionUtil;
import com.jmtop.edu.utils.network.Connectivity;
import com.zm.utils.AppUtil;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SplashScreenActivity extends AbsActivity {

    public static final int DELAY_TIME = 1000;
    public static final int GO_MAIN = -100;
    public static final int GO_UPDATE = -101;
    public static final int GO_FINISH = -102;
    private static final int EXTERNAL_REQUEST_CODE = 1 << 2;

    private SplashHandler mSplashHandler = new SplashHandler(this);
    private RestfulRequest mRequest;
    private Executor mExecutor = Executors.newCachedThreadPool();


    private static class SplashHandler extends Handler {
        private WeakReference<SplashScreenActivity> mWeakReference;

        public SplashHandler(SplashScreenActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case GO_UPDATE:
                    if (getActivity() != null) {
                        getActivity().updateActions(msg);
                    }
                    break;
                case GO_MAIN:
                    if (getActivity() != null && !getActivity().isFinishing()) {
                        SplashScreenActivity splashScreenActivity = getActivity();
                        if (!PermissionUtil.requestExternalStorage(splashScreenActivity, EXTERNAL_REQUEST_CODE)) {
                            splashScreenActivity.startMain();
                        }
                    }
                    break;
                case GO_FINISH:
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                    break;
                default:
                    break;
            }
        }

        private SplashScreenActivity getActivity() {
            if (mWeakReference != null) {
                return mWeakReference.get();
            }
            return null;
        }
    }

    private void startMain() {
        Intent intent = new Intent(SplashScreenActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        mSplashHandler.sendEmptyMessageDelayed(GO_FINISH, 600);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case EXTERNAL_REQUEST_CODE:
                startMain();
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    protected void initData() {
        mRequest = ReqRestAdapter.getInstance(getContext()).create(RestfulRequest.class);
    }

    @Override
    protected void initWidgets() {

    }

    @Override
    protected void initWidgetsActions() {
        updateVideoData();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_splash_screen;
    }

    private void updateVideoData() {
        boolean isLoadData = SettingDBUtil.getInstance(getApplicationContext()).checkDBUpgrade();// 数据库是否升级
        long lastLoadDataTime = SettingDBUtil.getInstance(getApplicationContext()).getLastLoadDataTime();// 是否第一次成功请求数据
        long date = isLoadData ? 0l : lastLoadDataTime;
        if (lastLoadDataTime <= 0l) {// 第一次
            if (Connectivity.isConnectedFast(getApplicationContext())) {
                queryVideoData(date, false);
            } else {
                showDialog(date);
            }
        } else {
            queryVideoData(date, true);
        }
    }

    private void queryVideoData(final long date, final boolean existData) {
        if (existData) {//有缓存数据则直接进入
            mSplashHandler.sendEmptyMessageDelayed(GO_MAIN, DELAY_TIME + 500);
            checkAppUpdated();
        }
        mRequest.queryVideo(date, new Callback<JSONObject>() {
            @Override
            public void success(final JSONObject jsonObject, Response response) {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        boolean parseCategories = NavModel.parseVideoCategory(jsonObject);
                        boolean parseVideos = VideoModel.parseVideos(jsonObject);
                        if (parseCategories && parseVideos) {
                            SettingDBUtil.getInstance(getApplicationContext()).setLoadDataTime(Calendar.getInstance().getTimeInMillis() / 1000);
                            queryGalleryData(existData, date);
                        } else {
                            if (!existData) {
                                mSplashHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        showDialog(date);//不存在数据则展示
                                    }
                                });
                            } else {
                                queryGalleryData(existData, date);
                            }
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                if (!existData) {
                    showDialog(date);
                } else {
                    queryGalleryData(existData, date);
                }
            }
        });

    }

    private void queryGalleryData(final boolean existData, final long date) {
        mSplashHandler.post(new Runnable() {
            @Override
            public void run() {
                mRequest.queryGallery("", new Callback<JSONObject>() {
                    @Override
                    public void success(final JSONObject jsonObject, Response response) {
                        mExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                boolean parseGalleryCategory = GalleryCategoryModel.parseCategory(jsonObject);
                                boolean parseGalleryTopics = GalleryTopicModel.parseTopics(getApplicationContext(), jsonObject);
                                if (parseGalleryCategory && parseGalleryTopics) {
                                    queryConfigData();
                                    if (existData) {
                                        return;
                                    }
                                    mSplashHandler.sendEmptyMessageDelayed(GO_MAIN, DELAY_TIME + 500);
                                } else {
                                    if (existData) {
                                        return;
                                    }
                                    mSplashHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showDialog(date);//不存在数据则展示
                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (existData) {
                            return;
                        }
                        mSplashHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showDialog(date);//不存在数据则展示
                            }
                        });
                    }
                });
            }
        });
    }

    private void queryConfigData() {
        mSplashHandler.post(new Runnable() {
            @Override
            public void run() {
                mRequest.queryConfig("", new Callback<JSONObject>() {
                    @Override
                    public void success(final JSONObject jsonObject, Response response) {
                        mExecutor.execute(new Runnable() {
                            @Override
                            public void run() {
                                SettingDBUtil.getInstance(getContext()).setConfigServer(jsonObject.toString());
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });
            }
        });
    }

    private void showDialog(final long date) {
        if (isFinishing()) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreenActivity.this).setTitle(R.string.tips);
        builder.setMessage(getString(R.string.net_need));
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.try_agin, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                queryVideoData(date, false);
            }
        });
        builder.create().show();
    }

    private void checkAppUpdated() {
        MobclickAgent.updateOnlineConfig(this);
        MobclickAgent.setOnlineConfigureListener(new UmengOnlineConfigureListener() {
            @Override
            public void onDataReceived(final JSONObject data) {

                Message msg = mSplashHandler.obtainMessage();
                msg.what = GO_UPDATE;
                msg.obj = data;
                mSplashHandler.sendMessageDelayed(msg, DELAY_TIME + 2000);
            }
        });

    }

    private void updateActions(Message msg) {
        JSONObject data = (JSONObject) msg.obj;
        if (data != null) {
            String versionCode = data.optString(UpdateConstant.LAST_VER_CODE);
            if (TextUtils.isEmpty(versionCode)) {
                return;
            }
            int currentVersionCode = AppUtil.getVersionCode(getApplicationContext());
            int lastVersionCode = Integer.parseInt(versionCode);
            if (currentVersionCode < lastVersionCode) {
                String apkUrl = data.optString(UpdateConstant.APK_URL);
                String updateDesc = data.optString(UpdateConstant.UPDATE_DESC);

                Intent intent = new Intent(UpdateConstant.RECEIVER_NAME + getPackageName());
                intent.putExtra(UpdateConstant.APK_URL, apkUrl);
                intent.putExtra(UpdateConstant.UPDATE_DESC, updateDesc);
                intent.setPackage(getPackageName());

                sendBroadcast(intent);
            }
        } else {
            String versionCode = MobclickAgent.getConfigParams(getApplicationContext(), UpdateConstant.LAST_VER_CODE);
            int currentVersionCode = AppUtil.getVersionCode(getApplicationContext());
            if (TextUtils.isEmpty(versionCode)) {
                return;
            }
            int lastVersionCode = Integer.parseInt(versionCode);
            if (currentVersionCode < lastVersionCode) {
                String apkUrl = MobclickAgent.getConfigParams(getApplicationContext(), UpdateConstant.APK_URL);
                String updateDesc = MobclickAgent.getConfigParams(getApplicationContext(), UpdateConstant.UPDATE_DESC);

                Intent intent = new Intent(UpdateConstant.RECEIVER_NAME + getPackageName());
                intent.putExtra(UpdateConstant.APK_URL, apkUrl);
                intent.putExtra(UpdateConstant.UPDATE_DESC, updateDesc);
                intent.setPackage(getPackageName());
                sendBroadcast(intent);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
