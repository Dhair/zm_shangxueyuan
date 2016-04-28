package com.zm.shangxueyuan.model;

import android.content.Context;

import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.alibaba.sdk.android.oss.model.OSSObjectSummary;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.helper.OssHelper;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Creator: dengshengjin on 16/4/22 22:25
 * Email: deng.shengjin@zuimeia.com
 */
public class GalleryModel extends BaseModel {
    private String titleUpload;
    private String imageListUrl;
    private String imageRealUrl;
    private String imageDetailUrl;

    public String getTitleUpload() {
        return titleUpload;
    }

    public void setTitleUpload(String titleUpload) {
        this.titleUpload = titleUpload;
    }

    public String getImageListUrl() {
        return imageListUrl;
    }

    public void setImageListUrl(String imageListUrl) {
        this.imageListUrl = imageListUrl;
    }

    public String getImageRealUrl() {
        return imageRealUrl;
    }

    public void setImageRealUrl(String imageRealUrl) {
        this.imageRealUrl = imageRealUrl;
    }

    public String getImageDetailUrl() {
        return imageDetailUrl;
    }

    public void setImageDetailUrl(String imageDetailUrl) {
        this.imageDetailUrl = imageDetailUrl;
    }

    public static List<GalleryModel> parseGalleryList(Context context, ListObjectsResult result, String titleUpload) {
        if (result == null || result.getObjectSummaries() == null || result.getObjectSummaries().isEmpty()) {
            return null;
        }
        List<GalleryModel> galleryList = new LinkedList<>();
        List<OSSObjectSummary> ossObjectSummaries = result.getObjectSummaries();
        for (int i = 0; i < ossObjectSummaries.size(); i++) {
            if (ossObjectSummaries.get(i).getSize() <= 0) {
                continue;
            }
            String baseUrl = CommonConstant.endpoint + File.separator + ossObjectSummaries.get(i).getKey();
            GalleryModel galleryModel = new GalleryModel();
            galleryModel.setTitleUpload(titleUpload);
            galleryModel.setImageListUrl(OssHelper.getImageListUrl(context, baseUrl));
            galleryModel.setImageDetailUrl(OssHelper.getImageDetailUrl(context, baseUrl));
            galleryModel.setImageRealUrl(OssHelper.getImageRealUrl(context, baseUrl));
            galleryList.add(galleryModel);
        }
        return galleryList;
    }
}
