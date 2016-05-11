package com.jmtop.edu.model;

import java.io.Serializable;

/**
* @author deng.shengjin
* @version create_time:2014-3-13_下午8:17:21
* @Description 父类
*/
public abstract class BaseModel implements Serializable {

	private static final long serialVersionUID = -6780130076053468257L;
	private boolean result = true;
	private String errorCode = null;

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

}
