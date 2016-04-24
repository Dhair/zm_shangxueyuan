package com.zm.shangxueyuan.db;

import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;
import com.sdk.download.providers.DownloadManager;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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

    public static VideoModel queryVideo(long videoId) {
        return new Select().from(VideoModel.class).where("videoId = ?", videoId).orderBy("RANDOM()").executeSingle();
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

    public static List<VideoModel> queryTopicVideos(long tId) {
        return new Select().from(VideoModel.class).where("tid=? and valid=1", tId).orderBy("corder DESC,videoId DESC").limit("25").execute();
    }

    public static List<VideoModel> queryVideoWithNav(long cId) {
        return new Select().from(VideoModel.class).where("cid = ? and valid=1", cId).orderBy("corder DESC").execute();
    }

    public static VideoStatusModel queryVideoStatus(long videoId) {
        return new Select().from(VideoStatusModel.class).where("videoId = ?", videoId).orderBy("RANDOM()").executeSingle();
    }

    public static VideoStatusModel queryVideoStatus(VideoModel videoModel) {
        VideoStatusModel statusModel = queryVideoStatus(videoModel.getVideoId());
        if (statusModel == null) {
            statusModel = videoModel.convert();
        }
        return statusModel;
    }

    public static void playHistoryDelete(Set<Long> videoIds) {
        ActiveAndroid.beginTransaction();
        try {
            for (Iterator<Long> it = videoIds.iterator(); it.hasNext(); ) {
                long videoId = it.next();
                VideoStatusModel videoStatusModel = getStatus(videoId);
                if (videoStatusModel != null) {
                    videoStatusModel.setPlayDate(0l);
                    videoStatusModel.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static void modifyPlay(VideoModel videoModel, int playType) {
        VideoStatusModel mStatusModel = queryVideoStatus(videoModel);
        if (mStatusModel != null) {
            mStatusModel.setPlayType(playType);
        }
        mStatusModel.save();
    }

    public static void modifyPlayed(VideoModel videoModel) {
        VideoStatusModel mStatusModel = queryVideoStatus(videoModel);
        if (mStatusModel != null) {
            mStatusModel.setPlayDate(Calendar.getInstance().getTimeInMillis());
        }
        mStatusModel.save();
    }

    public static void modifyCollect(VideoModel videoModel, int favType) {
        VideoStatusModel mStatusModel = queryVideoStatus(videoModel);
        if (mStatusModel != null) {
            mStatusModel.setFavStatus(favType);
            mStatusModel.setFavDate(Calendar.getInstance().getTimeInMillis());
        }
        mStatusModel.save();
    }

    public static void modifyDownload(VideoModel videoModel, int downloadType) {

    }

    public static void collectDelete(Set<Long> videoIds) {
        ActiveAndroid.beginTransaction();
        try {
            for (Iterator<Long> it = videoIds.iterator(); it.hasNext(); ) {
                long videoId = it.next();
                VideoStatusModel videoStatusModel = VideoDBUtil.getStatus(videoId);
                if (videoStatusModel != null) {
                    videoStatusModel.setFavStatus(CommonConstant.UN_FAV_STATUS);
                    videoStatusModel.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static void downloadDelete(Context context, Set<Long> videoIds) {
        DownloadManager mDownloadManager = new DownloadManager(context, context.getContentResolver(), context.getPackageName());
        for (Iterator<Long> it = videoIds.iterator(); it.hasNext(); ) {
            long videoId = it.next();
            mDownloadManager.remove(videoId);
        }
    }

    public static List<VideoModel> queryVideosByKeyword(String keyWord) {
        return new Select().from(VideoModel.class).where("valid=1 and (title like '%" + keyWord + "%' or content like '%" + keyWord + "%' or sub_title like '%" + keyWord + "%')")
                .orderBy("videoId DESC").limit("25").execute();
    }

}
