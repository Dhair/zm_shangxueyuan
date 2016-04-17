package com.zm.shangxueyuan.ui.provider.event;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-11_下午6:32:55
 * @Description 点击菜单栏item的事件
 */
public class MenuClickedEvent {
    private int position;

    public MenuClickedEvent(int position) {
        super();
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}
