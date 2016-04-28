package com.zm.shangxueyuan.helper;

import android.content.Context;

import java.io.File;

/**
 * @author deng.shengjin
 * @version create_time:2014-6-18_下午11:36:41
 * @Description 文件下载
 */
public class FileTransferService {

    private Context context;

    public FileTransferService(Context context) {
        super();
        this.context = context;
    }

    public boolean downloadFile(String url, File targetFile) {
        return HttpURLConnectionUtil.downloadFile(url, targetFile, context);
    }

}
