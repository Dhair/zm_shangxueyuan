package com.zm.shangxueyuan.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.utils.MyImageLoader;

import java.util.List;

public class ContentItemAdapter extends BaseAdapter {
    protected List<VideoModel> videoList;
    protected Context context;
    protected DisplayImageOptions mOptions;
    protected ImageLoader mImageLoader;
    protected long navId;

    public ContentItemAdapter(Context context, List<VideoModel> videoList, ImageLoader mImageLoader, long navId) {
        this.videoList = videoList;
        this.context = context;
        mOptions = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.list_default).showImageOnLoading(R.drawable.list_default)
                .resetViewBeforeLoading(true).cacheInMemory(true).cacheOnDisc(true).imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.ARGB_8888).displayer(new SimpleBitmapDisplayer()).build();
        this.mImageLoader = mImageLoader;
        this.navId = navId;
    }

    @Override
    public int getCount() {
        if (videoList == null) {
            return 0;
        }
        return videoList.size();
    }

    @Override
    public VideoModel getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VideoModel model = videoList.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(getResourceId(), null);
            holder = new ViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (holder == null) {
            convertView = LayoutInflater.from(context).inflate(getResourceId(), null);
            holder = new ViewHolder();
            initHolder(holder, convertView);
            convertView.setTag(holder);
        }
        updateHolder(holder, model, position);

        if (!MyImageLoader.isMemoryCache(context, StorageHelper.getImageUrl(model.getImage()), holder.getImg(), mImageLoader)) {
            mImageLoader.displayImage(StorageHelper.getImageUrl(model.getImage()), holder.getImg(), mOptions);
        }

        return convertView;
    }

    protected void initHolder(ViewHolder holder, View view) {
        ImageView img = (ImageView) view.findViewById(R.id.content_img);
        TextView text = (TextView) view.findViewById(R.id.content_text);
        ImageView audioImg = (ImageView) view.findViewById(R.id.audio_mark_img);
        ImageView newVideoImg = (ImageView) view.findViewById(R.id.new_video_img);
        ImageView topicImg = (ImageView) view.findViewById(R.id.topic_img);
        holder.setImg(img);
        holder.setText(text);
        holder.setAudioImg(audioImg);
        holder.setNewVideoImg(newVideoImg);
        holder.setTopicImg(topicImg);
    }

    public void updateHolder(View view, VideoModel model) {
        ImageView img = (ImageView) view.findViewById(R.id.content_img);
        TextView text = (TextView) view.findViewById(R.id.content_text);
        ImageView audioImg = (ImageView) view.findViewById(R.id.audio_mark_img);
        ImageView newVideoImg = (ImageView) view.findViewById(R.id.new_video_img);
        ImageView topicImg = (ImageView) view.findViewById(R.id.topic_img);
        text.setText(model.getTitle());
        audioImg.setVisibility(View.GONE);
        newVideoImg.setVisibility(View.GONE);
        if (VideoModel.isTopicVideo(model)) {
            topicImg.setVisibility(View.VISIBLE);
        } else {
            topicImg.setVisibility(View.GONE);
        }
        if (!MyImageLoader.isMemoryCache(context, StorageHelper.getImageUrl(model.getImage()), img, mImageLoader)) {
            mImageLoader.displayImage(StorageHelper.getImageUrl(model.getImage()), img, mOptions);
        }
    }

    protected void updateHolder(ViewHolder holder, VideoModel model, int position) {
        holder.getText().setText(model.getTitle());
        holder.getAudioImg().setVisibility(View.GONE);
        holder.getTopicImg().setVisibility(View.GONE);
        if (model.isClicked()) {
            holder.getNewVideoImg().setVisibility(View.GONE);
        } else {
            holder.getNewVideoImg().setVisibility(View.VISIBLE);
        }

    }

    public List<VideoModel> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<VideoModel> videoList) {
        this.videoList = videoList;
    }

    protected int getResourceId() {
        return R.layout.view_content_item;
    }

    protected boolean isOpenNewView() {
        return true;
    }

    protected class ViewHolder {
        private ImageView img, audioImg, newVideoImg, topicImg;
        private TextView text;

        public ViewHolder() {
            super();
        }

        public ImageView getImg() {
            return img;
        }

        public void setImg(ImageView img) {
            this.img = img;
        }

        public TextView getText() {
            return text;
        }

        public void setText(TextView text) {
            this.text = text;
        }

        public ImageView getAudioImg() {
            return audioImg;
        }

        public void setAudioImg(ImageView audioImg) {
            this.audioImg = audioImg;
        }

        public ImageView getNewVideoImg() {
            return newVideoImg;
        }

        public void setNewVideoImg(ImageView newVideoImg) {
            this.newVideoImg = newVideoImg;
        }

        public ImageView getTopicImg() {
            return topicImg;
        }

        public void setTopicImg(ImageView topicImg) {
            this.topicImg = topicImg;
        }

    }
}
