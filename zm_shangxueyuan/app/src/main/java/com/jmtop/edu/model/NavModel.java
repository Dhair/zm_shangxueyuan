package com.jmtop.edu.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.jmtop.edu.constant.VideoDBConstant;
import com.jmtop.edu.db.NavDBUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-10_下午5:16:42
 * @Description 导航model
 */
@Table(name = VideoDBConstant.T_NAV)
public class NavModel extends Model implements Serializable {

    private static final long serialVersionUID = 1969390824678305683L;

    @Column(name = VideoDBConstant.NAV_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long navId;

    @Column(name = VideoDBConstant.TYPE)
    private String type;

    @Column(name = VideoDBConstant.TITLE)
    private String title;

    @Column(name = VideoDBConstant.IMAGE)
    private String image;

    @Column(name = VideoDBConstant.ORDER_ID)
    private int orderId;


    public long getNavId() {
        return navId;
    }

    public void setNavId(long navId) {
        this.navId = navId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "NavModel{" +
                "navId=" + navId +
                ", type=" + type +
                ", title='" + title + '\'' +
                ", image='" + image + '\'' +
                ", orderId=" + orderId +
                '}';
    }

    public static boolean parseVideoCategory(JSONObject jsonObject) {
        if (jsonObject == null) {
            return false;
        }
        JSONArray categoryArr = jsonObject.optJSONArray("categories");
        if (categoryArr == null || categoryArr.length() == 0) {
            return false;
        }
        List<NavModel> navList = new LinkedList<>();
        for (int i = 0, len = categoryArr.length(); i < len; i++) {
            JSONObject categoryObj = categoryArr.optJSONObject(i);
            NavModel navModel = new NavModel();
            navModel.setImage(categoryObj.optString("image"));
            navModel.setOrderId(categoryObj.optInt("order"));
            navModel.setType(categoryObj.optString("type"));
            navModel.setNavId(categoryObj.optLong("id"));
            navModel.setTitle(categoryObj.optString("title"));
            navList.add(navModel);
        }
        if (!navList.isEmpty()) {
            NavDBUtil.clearNav();
            NavDBUtil.saveNav(navList);
            return true;
        }
        return false;
    }
}
