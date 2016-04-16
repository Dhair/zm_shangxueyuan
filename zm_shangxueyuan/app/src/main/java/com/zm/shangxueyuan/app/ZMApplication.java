package com.zm.shangxueyuan.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orhanobut.logger.Logger;
import com.zm.shangxueyuan.helper.StorageHelper;

import java.io.File;

/**
 * Creator: dengshengjin on 16/4/16 11:15
 * Email: deng.shengjin@zuimeia.com
 */
public class ZMApplication extends Application {
    private final static String TAG = ZMApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.init(TAG);
        ActiveAndroid.initialize(this);
        initStrictMode();
        initImageLoader(getApplicationContext());
    }

    @SuppressWarnings("unused")
    private void initStrictMode() {
        if (StorageHelper.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().penaltyDeath()
                    .build());
        }
    }

    private void initImageLoader(Context context) {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisc(true).build();

        File cacheDir = new File(StorageHelper.getImgDir(context));
        ImageLoaderConfiguration config;
        if (StorageHelper.DEVELOPER_MODE) {
            config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).defaultDisplayImageOptions(options)
                    .memoryCacheSizePercentage(25).denyCacheImageMultipleSizesInMemory().memoryCache(new WeakMemoryCache())
                    .discCacheFileNameGenerator(new Md5FileNameGenerator()).discCache(new TotalSizeLimitedDiscCache(cacheDir, 50 * 1024 * 1024))
                    .tasksProcessingOrder(QueueProcessingType.LIFO).threadPoolSize(3).writeDebugLogs().build();
        } else {
            config = new ImageLoaderConfiguration.Builder(context).threadPriority(Thread.NORM_PRIORITY - 2).defaultDisplayImageOptions(options)
                    .denyCacheImageMultipleSizesInMemory().memoryCache(new WeakMemoryCache())
                    .discCache(new TotalSizeLimitedDiscCache(cacheDir, 50 * 1024 * 1024)).tasksProcessingOrder(QueueProcessingType.LIFO).threadPoolSize(3)
                    .build();
        }

        ImageLoader.getInstance().init(config);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }
}
