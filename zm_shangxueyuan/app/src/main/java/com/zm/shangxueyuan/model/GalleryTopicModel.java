package com.zm.shangxueyuan.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.zm.shangxueyuan.constant.VideoDBConstant;
import com.zm.shangxueyuan.db.GalleryDBUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Creator: dengshengjin on 16/4/16 20:49
 * Email: deng.shengjin@zuimeia.com
 */
@Table(name = VideoDBConstant.T_GALLERY_TOPIC)
public class GalleryTopicModel extends Model implements Serializable {
    private static final long serialVersionUID = 1969390824678305684L;
    @Column(name = VideoDBConstant.GALLERY_TOPIC_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long topicId;

    @Column(name = VideoDBConstant.GALLERY_TOPIC_UPLOAD_TITLE)
    private String uploadTitle;

    @Column(name = VideoDBConstant.GALLERY_TOPIC_SUB_TITLE)
    private String subTitle;

    @Column(name = VideoDBConstant.GALLERY_TOPIC_TITLE)
    private String title;

    @Column(name = VideoDBConstant.GALLERY_TOPIC_IMAGE)
    private String image;

    @Column(name = VideoDBConstant.GALLERY_TOPIC_TOP)
    private boolean isTop;

    @Column(name = VideoDBConstant.GALLERY_TOPIC_CATEGORY)
    private long categoryId;

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setIsTop(boolean isTop) {
        this.isTop = isTop;
    }

    public long getTopicId() {
        return topicId;
    }

    public void setTopicId(long topicId) {
        this.topicId = topicId;
    }

    public String getUploadTitle() {
        return uploadTitle;
    }

    public void setUploadTitle(String uploadTitle) {
        this.uploadTitle = uploadTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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

    public static boolean parseTopics(JSONObject jsonObject) {
        if (jsonObject == null) {
            return false;
        }
        List<GalleryTopicModel> list = new LinkedList<>();
        JSONArray topTopicsArr = jsonObject.optJSONArray("top_topics");
        if (topTopicsArr != null) {
            for (int i = 0, len = topTopicsArr.length(); i < len; i++) {
                JSONObject topTopicObj = topTopicsArr.optJSONObject(i);
                GalleryTopicModel topicModel = new GalleryTopicModel();
                topicModel.setUploadTitle(topTopicObj.optString("upload_title"));
                topicModel.setImage(topTopicObj.optString("image"));
                topicModel.setSubTitle(topTopicObj.optString("sub_title"));
                topicModel.setTopicId(topTopicObj.optLong("id"));
                topicModel.setTitle(topTopicObj.optString("title"));
                topicModel.setIsTop(true);
                topicModel.setCategoryId(0l);
                list.add(topicModel);
            }
        }
        JSONArray categoryArr = jsonObject.optJSONArray("categories");
        if (categoryArr != null) {
            for (int i = 0, len = categoryArr.length(); i < len; i++) {
                JSONObject categoryObj = categoryArr.optJSONObject(i);
                JSONArray topicArr = categoryObj.optJSONArray("topics");
                if (topicArr != null) {
                    for (int j = 0, len2 = topicArr.length(); j < len2; j++) {
                        JSONObject topicObj = topicArr.optJSONObject(j);
                        GalleryTopicModel topicModel = new GalleryTopicModel();
                        topicModel.setUploadTitle(topicObj.optString("upload_title"));
                        topicModel.setImage(topicObj.optString("image"));
                        topicModel.setSubTitle(topicObj.optString("sub_title"));
                        topicModel.setTopicId(topicObj.optLong("id"));
                        topicModel.setTitle(topicObj.optString("title"));
                        topicModel.setCategoryId(categoryObj.optLong("id"));
                        topicModel.setIsTop(false);
                        list.add(topicModel);
                    }
                }
            }
        }
        if (!list.isEmpty()) {
            GalleryDBUtil.clearGalleryTopics();
            GalleryDBUtil.saveTopic(list);
            return true;
        }
        return false;
    }

    public static boolean isTopTopic(GalleryTopicModel videoModel) {
        return videoModel.isTop() && videoModel.getCategoryId() == 0;
    }

    public static List<GalleryTopicModel> parseGalleryTopics(JSONObject jsonObject) {
        if (jsonObject == null) {
            return null;
        }
        List<GalleryTopicModel> list = new LinkedList<>();
        JSONArray topicArr = jsonObject.optJSONArray("topics");
        if (topicArr != null) {
            for (int i = 0, len = topicArr.length(); i < len; i++) {
                JSONObject topicObj = topicArr.optJSONObject(i);
                GalleryTopicModel topicModel = new GalleryTopicModel();
                topicModel.setUploadTitle(topicObj.optString("upload_title"));
                topicModel.setImage(topicObj.optString("image"));
                topicModel.setSubTitle(topicObj.optString("sub_title"));
                topicModel.setTopicId(topicObj.optLong("id"));
                topicModel.setTitle(topicObj.optString("title"));
                list.add(topicModel);
            }
        }
        return list;
    }
}
