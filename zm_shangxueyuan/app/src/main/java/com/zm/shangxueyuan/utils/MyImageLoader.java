package com.zm.shangxueyuan.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.zm.utils.PhoneUtil;

public class MyImageLoader {

	public static boolean isMemoryCache(Context context, String uri, ImageView imageView, ImageLoader imageLoader) {

		ImageViewAware imageAware = new ImageViewAware(imageView);
		ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageAware, getMaxImageSize(context));

		String memoryCacheKey = MemoryCacheUtils.generateKey(uri, targetSize);
		Bitmap bmp = imageLoader.getMemoryCache().get(memoryCacheKey);
		if (bmp != null && !bmp.isRecycled()) {
			return imageAware.setImageBitmap(bmp);
		}
		return false;

	}

	private static ImageSize getMaxImageSize(Context context) {
		return new ImageSize(PhoneUtil.getDisplayWidth(context), PhoneUtil.getDisplayHeight(context));
	}
}
