package com.zm.shangxueyuan.model;

import com.zm.shangxueyuan.db.VideoDBUtil;

/**
 * Creator: dengshengjin on 16/4/24 10:26
 * Email: deng.shengjin@zuimeia.com
 */
public class VideoDownloadModel extends BaseModel {
    private static final long serialVersionUID = -6780130076053468290L;
    public long mVideoId;
    public long mDownloadId;
    public String mFilePath;
    public int mStatus;
    public int mProgress;
    public String mProgressTips;
    public String mTitle;
    public String mImage;
    public String mSubTitle;
    public int mDownloadType;
    public String mType;

    public static VideoModel convert(long videoId) {
        return VideoDBUtil.queryVideo(videoId);
    }

    public boolean equals(VideoDownloadModel videoDownloadModel) {
        return (mVideoId == videoDownloadModel.mVideoId && mDownloadType == videoDownloadModel.mDownloadType);
    }

    public void update(VideoDownloadModel videoDownloadModel) {
        mStatus = videoDownloadModel.mStatus;
        mFilePath = videoDownloadModel.mFilePath;
        mProgress = videoDownloadModel.mProgress;
        mProgressTips = videoDownloadModel.mProgressTips;
    }

    @Override
    public String toString() {
        return "VideoDownloadModel{" +
                "mVideoId=" + mVideoId +
                ", mDownloadId=" + mDownloadId +
                ", mFilePath='" + mFilePath + '\'' +
                ", mStatus=" + mStatus +
                ", mProgress=" + mProgress +
                ", mTitle='" + mTitle + '\'' +
                ", mImage='" + mImage + '\'' +
                ", mSubTitle='" + mSubTitle + '\'' +
                ", mDownloadType=" + mDownloadType +
                ", mType='" + mType + '\'' +
                '}';
    }
}
