package com.zm.shangxueyuan.db;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.zm.shangxueyuan.model.GalleryCategoryModel;
import com.zm.shangxueyuan.model.GalleryTopicModel;

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
