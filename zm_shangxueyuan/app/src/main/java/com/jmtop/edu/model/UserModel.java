package com.jmtop.edu.model;

import android.content.Context;
import android.text.TextUtils;

import com.jmtop.edu.db.SettingDBUtil;

import org.json.JSONObject;

/**
 * Creator: dengshengjin on 16/4/24 13:15
 * Email: deng.shengjin@zuimeia.com
 */
public class UserModel {
    public String accounts;
    public String true_name;
    public String student_level;
    public String member_level;
    public String point;
    public String token_id;
    public String accounts_id;
    public String type;
    public String jm_token_id;
    public String is_partner;
    public String contact_phone;
    public String email;
    public long userId;

    public static UserModel parse(Context context) {
        String jsonStr = SettingDBUtil.getInstance(context).getUserServer();
        if (TextUtils.isEmpty(jsonStr)) {
            return null;
        }
        return parse(jsonStr);
    }

    public static UserModel parse(String jsonStr) {
        try {

            JSONObject jsonObject = new JSONObject(jsonStr);
            UserModel userModel = new UserModel();
            userModel.accounts = jsonObject.optString("accounts");
            userModel.accounts_id = jsonObject.optString("accounts_id");
            userModel.contact_phone = jsonObject.optString("contact_phone");
            userModel.email = jsonObject.optString("email");
            userModel.true_name = jsonObject.optString("true_name");
            userModel.contact_phone = jsonObject.optString("contact_phone");
            userModel.userId = jsonObject.optLong("id");
            userModel.is_partner = jsonObject.optString("is_partner");
            userModel.jm_token_id = jsonObject.optString("jm_token_id");
            userModel.member_level = jsonObject.optString("member_level");
            userModel.student_level = jsonObject.optString("student_level");
            userModel.point = jsonObject.optString("point");
            userModel.token_id = jsonObject.optString("token_id");
            userModel.type = jsonObject.optString("type");
            return userModel;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public static String getUserAccount(Context context) {
        return SettingDBUtil.getInstance(context).getUserAccount();
    }

    public static boolean isLogin(Context context){
        String jsonStr = SettingDBUtil.getInstance(context).getUserServer();
        return !TextUtils.isEmpty(jsonStr);
    }
}
