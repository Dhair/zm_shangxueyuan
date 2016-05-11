package com.jmtop.edu.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.jmtop.edu.constant.CommonConstant;
import com.jmtop.edu.constant.VideoDBConstant;
import com.jmtop.edu.db.VideoDBUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 * @author deng.shengjin
 * @version create_time:2014-3-10_下午4:04:49
 * @Description 视频model
 */
@Table(name = VideoDBConstant.T_VIDEO)
public class VideoModel extends Model implements Serializable {

    private static final long serialVersionUID = 5199714764213505561L;
    @Column(name = VideoDBConstant.VIDEO_ID, unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    private long videoId;

    @Column(name = VideoDBConstant.TITLE)
    private String title;

    @Column(name = VideoDBConstant.TYPE)//video-视频  topic-专题
    private String type;

    @Column(name = VideoDBConstant.CLARITY)//视频清晰度，ld-标清 sd-超清 hd-超清 取最大值
    private String clarity;

    @Column(name = VideoDBConstant.TITLE_UPLOAD)
    private String titleUpload;

    @Column(name = VideoDBConstant.VALID)//是否删除。
    private boolean isValid;

    @Column(name = VideoDBConstant.IS_TOP)//是否在分类中置顶推荐。
    private boolean isTop;


    @Column(name = VideoDBConstant.CONTENT)
    private String content;

    @Column(name = VideoDBConstant.TID)//如果视频属于某专题，这里会有值，对应的就是专题ID。
    private long tid;

    @Column(name = VideoDBConstant.TORDER)//如果视频属于某专题，这里会有值，专题中的排列顺序，逆序排列。
    private int tOrder;

    @Column(name = VideoDBConstant.CID)//分类ID。
    private long cid;

    @Column(name = VideoDBConstant.CORDER)//如果视频属于某专题，这里会有值，专题中的排列顺序，逆序排列。
    private int cOrder;

    @Column(name = VideoDBConstant.SUB_TITLE)
    private String subTitle;

    @Column(name = VideoDBConstant.IMAGE)
    private String image;

    @Column(name = VideoDBConstant.IS_TOPIC_TOP)
    private boolean isTopicTop;

    @Column(name = VideoDBConstant.MODIFY_DATE)
    private long modifyDate;

    @Column(name = VideoDBConstant.CLICKED)//是否点击过
    private boolean clicked;

    @Column(name = VideoDBConstant.IS_LOGIN_VALID)//是否点击过
    private boolean isLoginValid;

    private int mDownloadType;

    public int getDownloadType() {
        return mDownloadType;
    }

    public boolean isLoginValid() {
        return isLoginValid;
    }

    public void setIsLoginValid(boolean isLoginValid) {
        this.isLoginValid = isLoginValid;
    }

    public void setDownloadType(int downloadType) {
        mDownloadType = downloadType;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClarity() {
        return clarity;
    }

    public void setClarity(String clarity) {
        this.clarity = clarity;
    }

    public String getTitleUpload() {
        return titleUpload;
    }

    public void setTitleUpload(String titleUpload) {
        this.titleUpload = titleUpload;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setIsTop(boolean isTop) {
        this.isTop = isTop;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int gettOrder() {
        return tOrder;
    }

    public void settOrder(int tOrder) {
        this.tOrder = tOrder;
    }

    public long getCid() {
        return cid;
    }

    public void setCid(long cid) {
        this.cid = cid;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isTopicTop() {
        return isTopicTop;
    }

    public void setIsTopicTop(boolean isTopicTop) {
        this.isTopicTop = isTopicTop;
    }

    public long getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(long modifyDate) {
        this.modifyDate = modifyDate;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }

    public int getcOrder() {
        return cOrder;
    }

    public void setcOrder(int cOrder) {
        this.cOrder = cOrder;
    }

    public VideoStatusModel convert() {
        VideoStatusModel statusModel = new VideoStatusModel();
        statusModel.setVideoId(videoId);
        statusModel.setFavStatus(CommonConstant.UN_FAV_STATUS);
        return statusModel;
    }


    public static boolean parseVideos(JSONObject jsonObject) {
        if (jsonObject == null) {
            return false;
        }
        JSONArray videoArr = jsonObject.optJSONArray("videos");
        if (videoArr == null || videoArr.length() == 0) {
            return false;
        }
        List<VideoModel> videoList = new LinkedList<>();
        for (int i = 0, len = videoArr.length(); i < len; i++) {
            JSONObject videoObj = videoArr.optJSONObject(i);
            VideoModel videoModel = new VideoModel();
            videoModel.setModifyDate(videoObj.optLong("modify_date"));
            videoModel.setTitle(videoObj.optString("title"));
            videoModel.setIsTopicTop(videoObj.optBoolean("is_topic_top"));
            videoModel.setImage(videoObj.optString("image"));
            videoModel.setCid(videoObj.optLong("cid"));
            videoModel.setcOrder(videoObj.optInt("corder"));
            videoModel.setSubTitle(videoObj.optString("sub_title"));
            videoModel.setContent(videoObj.optString("content"));
            videoModel.setIsTop(videoObj.optBoolean("is_top"));
            videoModel.setIsValid(videoObj.optBoolean("is_valid"));
            videoModel.setTitleUpload(videoObj.optString("title_upload"));
            videoModel.setClarity(videoObj.optString("clarity"));
            videoModel.setType(videoObj.optString("type"));
            videoModel.setVideoId(videoObj.optLong("id"));
            videoModel.setTid(videoObj.optInt("tid"));
            videoModel.settOrder(videoObj.optInt("torder"));
            videoModel.setIsLoginValid(videoObj.optBoolean("is_login_valid"));
            videoList.add(videoModel);
        }
        if (!videoList.isEmpty()) {
            VideoDBUtil.save(videoList);
            return true;
        }
        return false;
    }

    public static boolean isTopicVideo(VideoModel videoModel) {
        return videoModel.getType().equals("topic");
    }

    public static boolean isTopicVideo(String videoType) {
        return "topic".equals(videoType);
    }

    public static boolean isTopVideo(VideoModel videoModel) {
        return videoModel.isTop();
    }

    @Override
    public String toString() {
        return "VideoModel{" +
                "videoId=" + videoId +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", clarity='" + clarity + '\'' +
                ", titleUpload='" + titleUpload + '\'' +
                ", isValid=" + isValid +
                ", isTop=" + isTop +
                ", content='" + content + '\'' +
                ", tid=" + tid +
                ", tOrder=" + tOrder +
                ", cid=" + cid +
                ", cOrder=" + cOrder +
                ", subTitle='" + subTitle + '\'' +
                ", image='" + image + '\'' +
                ", isTopicTop=" + isTopicTop +
                ", modifyDate=" + modifyDate +
                ", clicked=" + clicked +
                ", mDownloadType=" + mDownloadType +
                '}';
    }
}
