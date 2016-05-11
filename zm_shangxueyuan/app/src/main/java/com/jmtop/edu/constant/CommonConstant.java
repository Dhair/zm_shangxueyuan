package com.jmtop.edu.constant;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-10_下午5:23:57
 * @Description 公共常量
 */
public interface CommonConstant {
    String REQUEST_RES_URL = "http://114.55.53.247/api/v1";// 请求图片URL(正式)
    String LOGIN_RES_URL = "http://58.64.198.60:8081/dhsxyapi";// 请求图片URL(正式)
    String LOGIN_WEB_URl = "http://www.shzhibo.net/wx_edu/red/index.html?plg_nld=1&plg_uin=1&plg_auth=1&plg_nld=1&plg_usr=1&plg_vkey=1&plg_dev=1#user-login";
    int LOGIN_CLIENT_TYPE = 3;
    String VIDEO_RES_URL = "http://zmstatic.zuimeia.com/video/%s/%s.mp4";
    String PIC_RES_URL = "http://zmstatic.zuimeia.com/%s";
    String APP_BASE_DIR_NAME = "jmtop/jmtop_edu";

    int SD_MODE = 1;// 流畅
    int HD_MODE = 2;// 高清
    int UD_MODE = 3;// 超清
    int NONE_MODE = 0;

    int FAV_STATUS = 1;
    int UN_FAV_STATUS = 0;

    int DOWN_FINISH = -1;
    int DOWN_ING = -2;
    int DOWN_NONE = 0;


    boolean IS_WARN_2G_3G = true;


    String FINISH_ACTION_NAME = "finish_action_name";

    String accessKeyId = "UQsawImSq6EPrcTO";
    String accessKeySecret = "KzdfiUFluyykj71dtq5MI9S0v11xPO";
    String endpoint = "http://zmstatic.zuimeia.com";
    String bucketName = "boku";
}
