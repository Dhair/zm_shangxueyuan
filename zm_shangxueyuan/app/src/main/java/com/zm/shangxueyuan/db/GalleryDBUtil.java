package com.zm.shangxueyuan.db;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.zm.shangxueyuan.constant.VideoDBConstant;
import com.zm.shangxueyuan.model.GalleryCategoryModel;
import com.zm.shangxueyuan.model.GalleryTopicModel;

import java.util.LinkedList;
import java.util.List;

/**
 * Creator: dengshengjin on 16/4/16 20:59
 * Email: deng.shengjin@zuimeia.com
 */
public class GalleryDBUtil {
    public static synchronized void saveCategory(List<GalleryCategoryModel> galleryCategoryList) {
        if (galleryCategoryList == null) {
            return;
        }
        ActiveAndroid.beginTransaction();
        try {
            for (GalleryCategoryModel galleryCategoryModel : galleryCategoryList) {
                galleryCategoryModel.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static synchronized List<GalleryCategoryModel> query() {
        List<GalleryCategoryModel> list = new Select().from(GalleryCategoryModel.class)
                .orderBy(VideoDBConstant.GALLERY_CATEGORY_ID + " ASC").execute();
        if (list == null) {
            list = new LinkedList<>();
        }
        return list;
    }

    public static synchronized List<GalleryTopicModel> queryTopics() {
        List<GalleryTopicModel> list = new Select().from(GalleryTopicModel.class)
                .orderBy(VideoDBConstant.GALLERY_TOPIC_ID + " DESC").execute();
        if (list == null) {
            list = new LinkedList<>();
        }
        return list;
    }

    public static synchronized void clearGalleryCategory() {
        try {
            new Delete().from(GalleryCategoryModel.class).execute();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static synchronized void saveTopic(List<GalleryTopicModel> galleryTopicList) {
        if (galleryTopicList == null) {
            return;
        }
        ActiveAndroid.beginTransaction();
        try {
            for (GalleryTopicModel galleryTopicModel : galleryTopicList) {
                galleryTopicModel.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }


    public static synchronized void clearGalleryTopics() {
        try {
            new Delete().from(GalleryTopicModel.class).execute();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
