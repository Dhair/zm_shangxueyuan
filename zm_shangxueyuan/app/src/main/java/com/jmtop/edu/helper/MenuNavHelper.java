package com.jmtop.edu.helper;

import com.jmtop.edu.db.GalleryDBUtil;
import com.jmtop.edu.db.NavDBUtil;
import com.jmtop.edu.model.GalleryCategoryModel;
import com.jmtop.edu.model.NavModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Creator: dengshengjin on 16/4/17 09:41
 * Email: deng.shengjin@zuimeia.com
 */
public class MenuNavHelper {
    private static MenuNavHelper mMenuNavHelper;
    private List<NavModel> mVideoNavModels;
    private List<GalleryCategoryModel> mGalleryNavModels;
    private Executor mExecutor;

    private MenuNavHelper() {
        mExecutor = Executors.newSingleThreadExecutor();
    }

    public static MenuNavHelper getInstance() {
        if (mMenuNavHelper == null) {
            synchronized (MenuNavHelper.class) {
                if (mMenuNavHelper == null) {
                    mMenuNavHelper = new MenuNavHelper();
                }
            }
        }
        return mMenuNavHelper;
    }

    public void queryNavData(final NavInitedCallback callback) {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mVideoNavModels = NavDBUtil.queryNav();
                mGalleryNavModels = GalleryDBUtil.query();
                if (callback != null) {
                    callback.callback();
                }
            }
        });
    }

    public boolean existGalleryNav() {
        return mGalleryNavModels != null && !mGalleryNavModels.isEmpty();
    }

    public List<GalleryCategoryModel> getGalleryNavModels() {
        return mGalleryNavModels;
    }

    public List<NavModel> getVideoNavModels() {
        return mVideoNavModels;
    }

    public interface NavInitedCallback {
        void callback();
    }
}
