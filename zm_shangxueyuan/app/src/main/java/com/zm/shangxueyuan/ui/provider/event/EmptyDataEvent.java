package com.zm.shangxueyuan.ui.provider.event;

/**
* @author deng.shengjin
* @version create_time:2014-3-21_上午11:40:46
* @Description 空数据
*/
public class EmptyDataEvent {
	private boolean isEmpty;

	public EmptyDataEvent(boolean isEmpty) {
		super();
		this.isEmpty = isEmpty;
	}

	public boolean isEmpty() {
		return isEmpty;
	}

	public void setEmpty(boolean isEmpty) {
		this.isEmpty = isEmpty;
	}

}
