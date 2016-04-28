package com.zm.shangxueyuan.db;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.zm.shangxueyuan.utils.db.SharePrefHelper;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-19_下午7:28:59
 * @Description 设置
 */
public class SettingDBUtil {
    private static SettingDBUtil mSettingDBUtil;
    private Context context;
    private final static String LOAD_DATA = "load_data";
    private final static String AA_DB_VERSION = "AA_DB_VERSION";
    private final static String CONFIG_SERVER = "configServer";
    private final static String USER_SERVER = "userServer";
    private final static String USER_ACCOUNT = "userAccount";
    private final static String USER_TOKEN_ID = "userTokenId";
    private final static String USER_ACCOUNTS_ID = "userAccountsId";
    private final static String GALLERY_TOPIC = "GalleryTopic";

    private SettingDBUtil(Context context) {
        this.context = context;
    }

    public static SettingDBUtil getInstance(Context context) {
        if (mSettingDBUtil == null) {
            mSettingDBUtil = new SettingDBUtil(context);
        }
        return mSettingDBUtil;
    }


    public long getLastLoadDataTime() {
        return SharePrefHelper.getInstance(context).getPref(LOAD_DATA, 0l);
    }

    public void setLoadDataTime(long value) {
        SharePrefHelper.getInstance(context).setPref(LOAD_DATA, value);
    }

    public String getConfigServer() {
        return SharePrefHelper.getInstance(context).getPref(CONFIG_SERVER, "");
    }

    public void setConfigServer(String config) {
        SharePrefHelper.getInstance(context).setPref(CONFIG_SERVER, config);
    }

    public String getUserServer() {
        return SharePrefHelper.getInstance(context).getPref(USER_SERVER, "");
    }

    public void setUserServer(String config) {
        SharePrefHelper.getInstance(context).setPref(USER_SERVER, config);
    }

    public String getUserAccount() {
        return SharePrefHelper.getInstance(context).getPref(USER_ACCOUNT, "");
    }

    public void setUserAccount(String account) {
        SharePrefHelper.getInstance(context).setPref(USER_ACCOUNT, account);
    }

    public String getUserTokenId() {
        return SharePrefHelper.getInstance(context).getPref(USER_TOKEN_ID, "");
    }

    public void setUserTokenId(String userTokenId) {
        SharePrefHelper.getInstance(context).setPref(USER_TOKEN_ID, userTokenId);
    }

    public String getUserAccountsId() {
        return SharePrefHelper.getInstance(context).getPref(USER_ACCOUNTS_ID, "");
    }

    public void setUserAccountsId(String accountsId) {
        SharePrefHelper.getInstance(context).setPref(USER_ACCOUNTS_ID, accountsId);
    }

    public String getGalleryTopic() {
        return SharePrefHelper.getInstance(context).getPref(GALLERY_TOPIC, "");
    }

    public void setGalleryTopic(String galleryTopic) {
        SharePrefHelper.getInstance(context).setPref(GALLERY_TOPIC, galleryTopic);
    }

    public void removeUser() {
        SharePrefHelper.getInstance(context).removePref(USER_SERVER);
        SharePrefHelper.getInstance(context).removePref(USER_ACCOUNT);
        SharePrefHelper.getInstance(context).removePref(USER_TOKEN_ID);
        SharePrefHelper.getInstance(context).removePref(USER_ACCOUNTS_ID);

    }

    private int getAADBMetaVersion() {
        ApplicationInfo ai = null;
        try {
            ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return ai.metaData.getInt(AA_DB_VERSION);
        } catch (NameNotFoundException e) {
        }
        return 0;
    }

    private int getAADBPrefVersion() {
        return SharePrefHelper.getInstance(context).getPref(AA_DB_VERSION, 0);
    }

    public boolean checkDBUpgrade() {// 判断是否需要更新
        int metaVersion = getAADBMetaVersion();
        int prefVersion = getAADBPrefVersion();
        if (metaVersion != prefVersion) {// 需要更新
            setAADBMetaVersion(metaVersion);
            return true;
        } else {// 无需更新
            return false;
        }
    }

    private void setAADBMetaVersion(int metaVersion) {
        SharePrefHelper.getInstance(context).setPref(AA_DB_VERSION, metaVersion);
    }
}
