package com.zm.shangxueyuan.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.zm.shangxueyuan.constant.VideoDBConstant;

import java.io.Serializable;

@Table(name = VideoDBConstant.T_STATUS)
public class VideoStatusModel extends Model implements Serializable {

	private static final long serialVersionUID = -3800222410011585901L;
	@Column(name = VideoDBConstant.VIDEO_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
	private long videoId; // 视频ID，唯一标识
	@Column(name = VideoDBConstant.FAV_STATUS)
	private int favStatus;// 1:收藏过0:未收藏过

	@Column(name = VideoDBConstant.FAV_DATE)
	private long favDate;// 点击收藏时间

	@Column(name = VideoDBConstant.DOWNLOAD_STATUS)
	private long downloadStatus;// －2:正在下载 －1:下载完成 0:正常

	@Column(name = VideoDBConstant.DOWNLOAD_TYPE)
	private int downloadType;// 1:流畅2:高清3:超清4:音频

	@Column(name = VideoDBConstant.DOWNLOAD_DATE)
	private long downloadDate;// 下载时间

	@Column(name = VideoDBConstant.PLAY_TYPE)
	private int playType;// 1:流畅2:高清3:超清4:音频

	@Column(name = VideoDBConstant.PLAY_DATE)
	private long playDate;// 点击播放时间

	@Column(name = VideoDBConstant.DOWN_ID)
	private long downId;// 下载id

	public long getVideoId() {
		return videoId;
	}

	public void setVideoId(long videoId) {
		this.videoId = videoId;
	}

	public int getFavStatus() {
		return favStatus;
	}

	public void setFavStatus(int favStatus) {
		this.favStatus = favStatus;
	}

	public long getFavDate() {
		return favDate;
	}

	public void setFavDate(long favDate) {
		this.favDate = favDate;
	}

	public long getDownloadStatus() {
		return downloadStatus;
	}

	public void setDownloadStatus(long downloadStatus) {
		this.downloadStatus = downloadStatus;
	}

	public int getDownloadType() {
		return downloadType;
	}

	public void setDownloadType(int downloadType) {
		this.downloadType = downloadType;
	}

	public long getDownloadDate() {
		return downloadDate;
	}

	public void setDownloadDate(long downloadDate) {
		this.downloadDate = downloadDate;
	}

	public int getPlayType() {
		return playType;
	}

	public void setPlayType(int playType) {
		this.playType = playType;
	}

	public long getPlayDate() {
		return playDate;
	}

	public void setPlayDate(long playDate) {
		this.playDate = playDate;
	}

	public long getDownId() {
		return downId;
	}

	public void setDownId(long downId) {
		this.downId = downId;
	}

	@Override
	public String toString() {
		return "VideoStatusModel [videoId=" + videoId + ", favStatus=" + favStatus + ", favDate=" + favDate + ", downloadStatus=" + downloadStatus
				+ ", downloadType=" + downloadType + ", downloadDate=" + downloadDate + ", playType=" + playType + ", playDate=" + playDate + "]";
	}

}
