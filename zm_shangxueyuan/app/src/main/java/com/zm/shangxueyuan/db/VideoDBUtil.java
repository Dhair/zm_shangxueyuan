package com.zm.shangxueyuan.db;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;

import java.util.LinkedList;
import java.util.List;

public class VideoDBUtil {

    public static synchronized void save(List<VideoModel> videoList) {
        if (videoList == null) {
            return;
        }
        ActiveAndroid.beginTransaction();
        try {
            for (VideoModel videoModel : videoList) {
                videoModel.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static VideoStatusModel getStatus(long videoId) {
        return new Select().from(VideoStatusModel.class).where("videoId = ?", videoId).orderBy("RANDOM()").executeSingle();
    }

    private static VideoModel queryVideo(long videoId) {
        return new Select().from(VideoModel.class).where("videoId = ?", videoId).orderBy("RANDOM()").executeSingle();
    }

    public static List<VideoModel> queryDownloadedVideos() {
        List<VideoStatusModel> statusList = new Select().from(VideoStatusModel.class).where("downloadStatus = -1").orderBy("downloadDate desc").execute();
        List<VideoModel> videoList = new LinkedList<>();
        if (statusList != null) {
            for (VideoStatusModel statusModel : statusList) {
                VideoModel videoModel = queryVideo(statusModel.getVideoId());
                if (videoModel != null && videoModel.isValid()) {
                    videoModel.setDownloadType(statusModel.getDownloadType());
                    videoList.add(videoModel);
                }
            }
        }
        return videoList;
    }

    public static List<VideoModel> queryHistoryVideos() {
        List<VideoStatusModel> statusList = new Select().from(VideoStatusModel.class).where("playDate > 0").orderBy("playDate desc").limit("16").execute();
        List<VideoModel> videoList = new LinkedList<>();
        if (statusList != null) {
            for (int i = 0, size = statusList.size(); i < size; i++) {
                VideoModel videoModel = queryVideo(statusList.get(i).getVideoId());
                if (videoModel != null && videoModel.isValid()) {
                    videoList.add(videoModel);
                }
            }
        }
        return videoList;
    }

    public static List<VideoModel> queryCollectedVideos() {
        List<VideoStatusModel> statusList = new Select().from(VideoStatusModel.class).where("favStatus = 1").orderBy("favDate desc").execute();
        List<VideoModel> videoList = new LinkedList<>();
        if (statusList != null) {
            for (int i = 0, size = statusList.size(); i < size; i++) {
                VideoModel videoModel = queryVideo(statusList.get(i).getVideoId());
                if (videoModel != null && videoModel.isValid()) {
                    videoList.add(videoModel);
                }
            }
        }
        return videoList;
    }

    public static List<VideoModel> getTopicVideos(long tId) {
        return new Select().from(VideoModel.class).where("tId=? and type=0 ", tId).orderBy("tOrder ASC").limit("16").execute();
    }
}
