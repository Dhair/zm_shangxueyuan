package com.zm.shangxueyuan.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.utils.MyImageLoader;

import java.util.List;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-15_下午8:53:22
 * @Description 下载
 */
public class ContentDownloadAdapter extends ContentDeleteItemAdapter {

    public ContentDownloadAdapter(Context context, List<VideoModel> videoList, ImageLoader mImageLoader, long navId) {
        super(context, videoList, mImageLoader, navId);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoModel model = videoList.get(position);

        DownloadViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(getResourceId(), null);
            holder = new DownloadViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (DownloadViewHolder) convertView.getTag();
        }
        if (holder == null) {
            convertView = LayoutInflater.from(context).inflate(getResourceId(), null);
            holder = new DownloadViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        }
        updateHolder(holder, model, position);
        if (!MyImageLoader.isMemoryCache(context, StorageHelper.getImageUrl(model.getImage()), holder.getImg(), mImageLoader)) {
            mImageLoader.displayImage(StorageHelper.getImageUrl(model.getImage()), holder.getImg(), mOptions);
        }
        return convertView;
    }

    @Override
    protected int getResourceId() {
        return R.layout.view_content_type_item;
    }

    private void initHolder(DownloadViewHolder holder, View view) {
        super.initHolder(holder, view);
        holder.setTypeText((TextView) view.findViewById(R.id.video_type_text));
    }

    private void updateHolder(DownloadViewHolder holder, VideoModel model, int position) {
        super.updateHolder(holder, model, position);
        holder.getTypeText().setVisibility(View.GONE);
        if (model.getDownloadType() == CommonConstant.SD_MODE) {
            holder.getTypeText().setText(context.getString(R.string.video_sd_mode));
            holder.getTypeText().setVisibility(View.VISIBLE);
        } else if (model.getDownloadType() == CommonConstant.HD_MODE) {
            holder.getTypeText().setText(context.getString(R.string.video_hd_mode));
            holder.getTypeText().setVisibility(View.VISIBLE);
        } else if (model.getDownloadType() == CommonConstant.UD_MODE) {
            holder.getTypeText().setText(context.getString(R.string.video_ud_mode));
            holder.getTypeText().setVisibility(View.VISIBLE);
        }
    }

    final class DownloadViewHolder extends DeleteViewHolder {
        private TextView typeText;

        public TextView getTypeText() {
            return typeText;
        }

        public void setTypeText(TextView typeText) {
            this.typeText = typeText;
        }

    }
}
