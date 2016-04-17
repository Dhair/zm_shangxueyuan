package com.zm.shangxueyuan.utils;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.zm.shangxueyuan.R;

public class CommonUtils {
    public static DisplayImageOptions.Builder getDisplayImageOptionsBuilder() {
        return new DisplayImageOptions.Builder().resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888).displayer(new SimpleBitmapDisplayer());
    }
    public static DisplayImageOptions.Builder getDisplayImageOptionsBuilder(int defaultResId) {
        return new DisplayImageOptions.Builder().showImageForEmptyUri(defaultResId).showImageOnLoading(defaultResId)
                .resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888).displayer(new SimpleBitmapDisplayer());
    }

    public static DisplayImageOptions.Builder getDisplayImageOptionsBuilderDefault() {
        return getDisplayImageOptionsBuilder(R.drawable.list_default);
    }


}
