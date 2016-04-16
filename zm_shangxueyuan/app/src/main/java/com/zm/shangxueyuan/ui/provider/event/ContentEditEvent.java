package com.zm.shangxueyuan.ui.provider.event;

/**
* @author deng.shengjin
* @version create_time:2014-3-17_下午2:53:06
* @Description 删除数据
*/
public class ContentEditEvent {
	private boolean isEditStatus;

	public ContentEditEvent(boolean isEditStatus) {
		super();
		this.isEditStatus = isEditStatus;
	}

	public boolean isEditStatus() {
		return isEditStatus;
	}

	public void setEditStatus(boolean isEditStatus) {
		this.isEditStatus = isEditStatus;
	}

}
