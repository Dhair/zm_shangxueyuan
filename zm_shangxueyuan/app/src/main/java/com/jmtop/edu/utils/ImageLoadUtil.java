package com.jmtop.edu.utils;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.TotalSizeLimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.jmtop.edu.helper.StorageHelper;

import java.io.File;

/**
 * Created by chenzhiyong on 15/6/8.
 */
public class ImageLoadUtil {

    public static void initImageLoader(Context context){
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
}
