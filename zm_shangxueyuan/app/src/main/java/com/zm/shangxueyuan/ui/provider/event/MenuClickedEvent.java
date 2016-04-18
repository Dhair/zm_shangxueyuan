package com.zm.shangxueyuan.ui.provider.event;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-11_下午6:32:55
 * @Description 点击菜单栏item的事件
 */
public class MenuClickedEvent {
    public static final int VIDEO_CLICK = 1;
    public static final int GALLERY_CLICK = 2;
    private int position;
    private int mType;

    public MenuClickedEvent(int type, int position) {
        super();
        mType = type;
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public int getType() {
        return mType;
    }
}
