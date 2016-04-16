package com.zm.shangxueyuan.constant;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-10_下午5:23:57
 * @Description 公共常量
 */
public interface CommonConstant {
    String REQUEST_RES_URL = "http://114.55.53.247/api/v1";// 请求图片URL(正式)
    String VIDEO_RES_URL = "http://zmstatic.zuimeia.com/video/%s/%s.mp4";
    String PIC_RES_URL = "http://zmstatic.zuimeia.com/%s";
    String APP_BASE_DIR_NAME = "zhongmai/shangxueyuan";
    String DATA = "data";

    int SD_MODE = 1;// 流畅
    int HD_MODE = 2;// 高清
    int UD_MODE = 3;// 超清
    int AUDIO_MODE = 4;// 音频

    int FAV_STATUS = 1;
    int UNFAV_STATUS = 0;

    int CLICKED_STATUS = 1;
    int UNCLICKED_STATUS = 0;

    int DOWN_FINISH = -1;
    int DOWN_ING = -2;
    int DOWN_NONE = 0;

    int TOPIC_TYPE = 1;


    boolean IS_WARN_2G_3G = true;
}
