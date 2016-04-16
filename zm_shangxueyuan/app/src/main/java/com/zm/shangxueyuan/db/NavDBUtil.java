package com.zm.shangxueyuan.db;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.zm.shangxueyuan.constant.VideoDBConstant;
import com.zm.shangxueyuan.model.NavModel;

import java.util.LinkedList;
import java.util.List;


/**
 * @author deng.shengjin
 * @version create_time:2014-3-11_下午1:56:19
 * @Description 导航查询
 */
public class NavDBUtil {

    public static synchronized List<NavModel> queryNav(Context context) {
        List<NavModel> navList = new Select().from(NavModel.class)
                .orderBy(VideoDBConstant.ORDER_ID + " ASC").execute();
        if (navList == null) {
            navList = new LinkedList<>();
        }
        return navList;
    }

    public static synchronized void clearNav() {
        try {
            new Delete().from(NavModel.class).execute();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static synchronized void saveNav(List<NavModel> navList) {
        if (navList == null) {
            return;
        }
        ActiveAndroid.beginTransaction();
        try {
            for (NavModel navModel : navList) {
                navModel.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
