package com.jmtop.edu.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.jmtop.edu.constant.VideoDBConstant;
import com.jmtop.edu.db.GalleryDBUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Creator: dengshengjin on 16/4/16 20:49
 * Email: deng.shengjin@zuimeia.com
 */
@Table(name = VideoDBConstant.T_CATEGORY)
public class GalleryCategoryModel extends Model implements Serializable {
    private static final long serialVersionUID = 1969390824678305685L;
    @Column(name = VideoDBConstant.GALLERY_CATEGORY_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long categoryId;

    @Column(name = VideoDBConstant.GALLERY_CATEGORY_IMAGE)
    private String image;

    @Column(name = VideoDBConstant.GALLERY_CATEGORY_TITLE)
    private String title;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static boolean parseCategory(JSONObject jsonObject) {
        if (jsonObject == null) {
            return false;
        }
        JSONArray categoryArr = jsonObject.optJSONArray("categories");
        if (categoryArr == null || categoryArr.length() == 0) {
            return false;
        }
        List<GalleryCategoryModel> list = new LinkedList<>();
        for (int i = 0, len = categoryArr.length(); i < len; i++) {
            JSONObject categoryObj = categoryArr.optJSONObject(i);
            GalleryCategoryModel galleryCategoryModel = new GalleryCategoryModel();
            galleryCategoryModel.setImage(categoryObj.optString("image"));
            galleryCategoryModel.setCategoryId(categoryObj.optInt("id"));
            galleryCategoryModel.setTitle(categoryObj.optString("title"));
            list.add(galleryCategoryModel);
        }
        if (!list.isEmpty()) {
            GalleryDBUtil.clearGalleryCategory();
            GalleryDBUtil.saveCategory(list);
            return true;
        }
        return false;
    }
}
