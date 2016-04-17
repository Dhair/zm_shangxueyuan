package com.zm.shangxueyuan.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.utils.CommonUtils;
import com.zm.shangxueyuan.utils.ImageLoadUtil;
import com.zm.shangxueyuan.utils.ResUtil;
import com.zm.utils.PhoneUtil;

import java.util.List;

/**
 * Creator: dengshengjin on 16/4/17 13:54
 * Email: deng.shengjin@zuimeia.com
 */
public class VideoAdapter extends BaseAdapter {
    private List<VideoModel> mVideoList;
    private Context mContext;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private int mDisplayWidth;

    public VideoAdapter(Context context) {
        mContext = context;
        init(mContext);
    }

    private void init(Context context) {
        mDisplayWidth = PhoneUtil.getDisplayWidth(context);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoadUtil.initImageLoader(context);
        }
        mImageLoader = ImageLoader.getInstance();
        mOptions = CommonUtils.getDisplayImageOptionsBuilder(R.drawable.common_default).build();
    }

    @Override
    public int getCount() {
        if (mVideoList == null) {
            return 0;
        }
        int size = mVideoList.size();
        return (int) Math.ceil(size / 2.0f);
    }

    @Override
    public Object getItem(int position) {
        return mVideoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.fragment_home_video_item, null);

            int layoutW = (int) Math.ceil(mDisplayWidth / 2.0f);

            for (int i = 0; i < 2; i++) {
                holder.view[i] = convertView.findViewById(ResUtil.getInstance(mContext).viewId("view" + i));
                holder.picture[i] = (ImageView) holder.view[i].findViewById(R.id.image);
                holder.title[i] = (TextView) holder.view[i].findViewById(R.id.title);
                holder.subTitle[i] = (TextView) holder.view[i].findViewById(R.id.sub_title);
                holder.flag[i] = (TextView) holder.view[i].findViewById(R.id.flag_text);
                ViewGroup.LayoutParams layoutParamsLeft = holder.view[i].getLayoutParams();
                layoutParamsLeft.width = layoutW;
            }

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int start = position * 2;
        int end = start + 2;

        for (int i = start; i < end; i++) {
            final int pos = i % 2;
            if (i >= mVideoList.size()) {
                holder.view[pos].setVisibility(View.INVISIBLE);
                holder.view[pos].setOnClickListener(null);
            } else {
                if (holder.view[pos].getVisibility() != View.VISIBLE) {
                    holder.view[pos].setVisibility(View.VISIBLE);
                }

                final VideoModel videoModel = mVideoList.get(i);
                holder.title[pos].setText(videoModel.getTitle());
                holder.subTitle[pos].setText(videoModel.getSubTitle());
                holder.flag[pos].setVisibility(View.GONE);
                if (VideoModel.isTopicVideo(videoModel)) {
                    holder.flag[pos].setVisibility(View.VISIBLE);
                }
                mImageLoader.displayImage(StorageHelper.getImageUrl(videoModel.getImage()), holder.picture[pos], mOptions);

                holder.view[pos].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        int[] location = new int[2];
                        v.getLocationInWindow(location);


                    }
                });
            }
        }

        return convertView;
    }

    public List<VideoModel> getVideoList() {
        return mVideoList;
    }

    public void setVideoList(List<VideoModel> videoList) {
        mVideoList = videoList;
    }

    static class ViewHolder {
        View view[] = new View[2];
        ImageView picture[] = new ImageView[2];
        TextView title[] = new TextView[2];
        TextView subTitle[] = new TextView[2];
        TextView flag[] = new TextView[2];
    }

    public void clear() {
        mImageLoader.stop();
        mImageLoader.clearMemoryCache();
        System.gc();
        System.runFinalization();
    }
}
