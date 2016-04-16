package com.zm.shangxueyuan.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.utils.MyImageLoader;

import java.util.List;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-15_下午6:17:43
 * @Description 带删除adpter
 */
public class ContentDeleteItemAdapter extends ContentItemAdapter {
    private boolean toEditStatus = false;

    public ContentDeleteItemAdapter(Context context, List<VideoModel> videoList, ImageLoader mImageLoader, long navId) {
        super(context, videoList, mImageLoader, navId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoModel model = videoList.get(position);
        DeleteViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_content_delete_item, null);
            holder = new DeleteViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (DeleteViewHolder) convertView.getTag();
        }
        if (holder == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.view_content_delete_item, null);
            holder = new DeleteViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        }
        updateHolder(holder, model, position);
        if (!MyImageLoader.isMemoryCache(context, StorageHelper.getImageUrl(model.getImage()), holder.getImg(), mImageLoader)) {
            mImageLoader.displayImage(StorageHelper.getImageUrl(model.getImage()), holder.getImg(), mOptions);
        }
        return convertView;
    }

    protected void initHolder(DeleteViewHolder holder, View view) {
        super.initHolder(holder, view);
        holder.setDeleteImg((ImageView) view.findViewById(R.id.delete_img));
    }

    protected void updateHolder(DeleteViewHolder holder, VideoModel model, int position) {
        super.updateHolder(holder, model, position);
        if (isToEditStatus()) {
            holder.getDeleteImg().setVisibility(View.VISIBLE);
        } else {
            holder.getDeleteImg().setVisibility(View.GONE);
        }
    }

    @Override
    protected boolean isOpenNewView() {
        return false;
    }

    protected class DeleteViewHolder extends ViewHolder {
        private ImageView deleteImg;

        public ImageView getDeleteImg() {
            return deleteImg;
        }

        public void setDeleteImg(ImageView deleteImg) {
            this.deleteImg = deleteImg;
        }

    }

    public boolean isToEditStatus() {
        return toEditStatus;
    }

    public void setToEditStatus(boolean toEditStatus) {
        this.toEditStatus = toEditStatus;
    }

}
