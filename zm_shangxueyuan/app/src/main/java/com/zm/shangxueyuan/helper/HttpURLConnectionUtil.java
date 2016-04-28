package com.zm.shangxueyuan.helper;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.zm.shangxueyuan.utils.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

/**
 * @author deng.shengjin
 * @version create_time:2014-6-18_下午10:28:22
 * @Description HttpURLConnectionUtil
 */
public class HttpURLConnectionUtil {
    private final static String TAG = "HttpURLConnectionUtil";

    public static synchronized boolean isCompletedFile(int netContentLen, File destFile) {
        try {
            int destFileLen = IOUtil.getFileSize(destFile);
            if (netContentLen > 0 && destFileLen > 0 && destFileLen >= netContentLen) {
                return true;
            }
        } catch (Throwable t) {
        }
        try {
            destFile.delete();// 没下载成功则删除
        } catch (Throwable t) {
        }
        return false;
    }

    public static synchronized boolean isCompletedFile(Context context, String downloadUrl, File destFile) {
        HttpURLConnection connection = null;
        try {
            if (TextUtils.isEmpty(downloadUrl)) {
                return false;
            }
            URL myFileURL = new URL(downloadUrl);

            connection = getNewHttpURLConnection(myFileURL, context);
            connection.setRequestMethod("HEAD");// 解决每次网络请求两次的情况
            int fileLen = connection.getContentLength();
            int destFileLen = IOUtil.getFileSize(destFile);
            if (fileLen > 0 && destFileLen > 0 && destFileLen >= fileLen) {
                return true;
            }
        } catch (Throwable t) {
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        try {
            destFile.delete();// 没下载成功则删除
        } catch (Throwable t) {
        }
        return false;
    }

    public static synchronized boolean downloadFile(String url, File f, Context context) {
        URL myFileUrl = null;
        Log.i(TAG, "Image url is 1 " + url);
        try {
            myFileUrl = new URL(url);
        } catch (Exception e) {
            return false;
        }
        HttpURLConnection connection = null;
        InputStream is = null;
        FileOutputStream fo = null;
        int contentLen = 0;
        try {
            connection = getNewHttpURLConnection(myFileUrl, context);
            contentLen = connection.getContentLength();
            connection.connect();

            is = connection.getInputStream();
            if (f.createNewFile()) {
                fo = new FileOutputStream(f);
                byte[] buffer = new byte[256];
                int size;
                while ((size = is.read(buffer)) > 0) {
                    fo.write(buffer, 0, size);
                }
            }
            if (fo != null) {
                fo.flush();
                fo.close();
            }
            if (isCompletedFile(contentLen, f)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static synchronized HttpURLConnection getNewHttpURLConnection(URL url, Context context) {
        HttpURLConnection connection = null;
        Cursor mCursor = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                // 获取当前正在使用的APN接入点
                Uri uri = Uri.parse("content://telephony/carriers/preferapn");
                mCursor = context.getContentResolver().query(uri, null, null, null, null);
                if (mCursor != null && mCursor.moveToFirst()) {
                    // 游标移至第一条记录，当然也只有一条
                    String proxyStr = mCursor.getString(mCursor.getColumnIndex("proxy"));
                    if (proxyStr != null && proxyStr.trim().length() > 0) {
                        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyStr, 80));
                        connection = (HttpURLConnection) url.openConnection(proxy);
                    }
                }
            }
            return connection;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                return (HttpURLConnection) url.openConnection();
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
        } finally {
            if (mCursor != null) {
                mCursor.close();
            }
        }

    }

}
