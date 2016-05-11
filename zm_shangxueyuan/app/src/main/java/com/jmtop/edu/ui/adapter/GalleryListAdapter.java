package com.jmtop.edu.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.jmtop.edu.R;
import com.jmtop.edu.model.GalleryModel;
import com.jmtop.edu.ui.listener.OnItemClickListener;
import com.jmtop.edu.utils.CommonUtils;
import com.jmtop.edu.utils.ImageLoadUtil;

import java.util.LinkedList;
import java.util.List;

public class GalleryListAdapter extends BaseAdapter {
    private List<GalleryModel> mGalleryList;
    private Context context;
    private int mItemHeight;
    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;

    public GalleryListAdapter(Context context, int itemHeight) {
        super();
        this.context = context;
        mItemHeight = itemHeight;
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoadUtil.initImageLoader(context);
        }
        mImageLoader = ImageLoader.getInstance();
        mOptions = CommonUtils.getDisplayImageOptionsBuilder(R.drawable.list_default).build();
    }

    @Override
    public int getCount() {
        if (mGalleryList == null) {
            return 0;
        }
        return mGalleryList.size();
    }

    @Override
    public GalleryModel getItem(int position) {
        return mGalleryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GalleryModel model = mGalleryList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_gallery_item, null);
            holder = new ViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (holder == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_gallery_item, null);
            holder = new ViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        }
        updateHolder(holder, model, position);
        return convertView;
    }

    private void initHolder(ViewHolder holder, View view) {
        holder.mImageView = (ImageView) view.findViewById(R.id.image);
        holder.mViewGroup = (ViewGroup) view.findViewById(R.id.gallery_content);
    }

    public void clear() {
        mImageLoader.stop();
        mImageLoader.clearMemoryCache();
        System.gc();
        System.runFinalization();
    }

    private void updateHolder(ViewHolder holder, final GalleryModel model, final int position) {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) holder.mImageView.getLayoutParams();
        if (lp == null) {
            lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mItemHeight);
            holder.mImageView.setLayoutParams(lp);
        } else {
            lp.height = mItemHeight;
            holder.mImageView.requestLayout();
        }
        mImageLoader.displayImage(model.getImageListUrl(), holder.mImageView, mOptions);
        holder.mViewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(v, (LinkedList<GalleryModel>) mGalleryList, position);
                }
            }
        });
    }

    public void setGalleryList(List<GalleryModel> galleryList) {
        this.mGalleryList = galleryList;
    }

    final class ViewHolder {
        private ImageView mImageView;
        private ViewGroup mViewGroup;
    }

    private OnItemClickListener<LinkedList<GalleryModel>> mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener<LinkedList<GalleryModel>> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
}
