package com.zm.shangxueyuan.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.listener.OnItemClickListener;
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
    private DisplayMetrics mDisplayMetrics;

    private boolean mNeedShowTopicFlag;
    private boolean mNeedShowDelete;
    private boolean mNeedShowDownloadType;

    public VideoAdapter(Context context) {
        this(context, true, false);
    }

    public VideoAdapter(Context context, boolean needShowTopicFlag) {
        this(context, needShowTopicFlag, false);
    }

    public VideoAdapter(Context context, boolean needShowTopicFlag, boolean needShowDownloadType) {
        mContext = context;
        mNeedShowTopicFlag = needShowTopicFlag;
        mNeedShowDownloadType = needShowDownloadType;
        init(mContext);
    }

    private void init(Context context) {
        mDisplayMetrics = context.getResources().getDisplayMetrics();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.fragment_home_video_item, null);

            int layoutW = (mItemWidth > 0 ? mItemWidth : (int) Math.ceil((mDisplayWidth) / 2.0f));
            for (int i = 0; i < 2; i++) {
                holder.view[i] = convertView.findViewById(ResUtil.getInstance(mContext).viewId("view" + i));
                holder.topLines[i] = holder.view[i].findViewById(R.id.top_line);
                holder.picture[i] = (ImageView) holder.view[i].findViewById(R.id.image);
                holder.title[i] = (TextView) holder.view[i].findViewById(R.id.title);
                holder.subTitle[i] = (TextView) holder.view[i].findViewById(R.id.sub_title);
                holder.flag[i] = (TextView) holder.view[i].findViewById(R.id.flag_text);
                holder.delete[i] = (ImageView) holder.view[i].findViewById(R.id.delete_image);
                holder.downloadType[i] = (TextView) holder.view[i].findViewById(R.id.download_type_text);
                holder.vipTexts[i] = (TextView) holder.view[i].findViewById(R.id.vip_text);
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
                holder.topLines[pos].setVisibility(View.GONE);
            } else {
                if (holder.view[pos].getVisibility() != View.VISIBLE) {
                    holder.view[pos].setVisibility(View.VISIBLE);
                }
                if (position == 0) {
                    holder.topLines[pos].setVisibility(View.VISIBLE);
                } else {
                    holder.topLines[pos].setVisibility(View.GONE);
                }
                final VideoModel videoModel = mVideoList.get(i);
                holder.title[pos].setText(videoModel.getTitle());
                holder.subTitle[pos].setText(videoModel.getSubTitle());
                holder.flag[pos].setVisibility(View.GONE);
                if (mNeedShowTopicFlag && VideoModel.isTopicVideo(videoModel)) {
                    holder.flag[pos].setVisibility(View.VISIBLE);
                } else {
                    holder.vipTexts[pos].setVisibility(View.GONE);
                    if (videoModel.isLoginValid() && !mNeedShowDelete) {
                        holder.vipTexts[pos].setVisibility(View.VISIBLE);
                    }
                }

                holder.delete[pos].setVisibility(View.GONE);
                if (mNeedShowDelete) {
                    holder.delete[pos].setVisibility(View.VISIBLE);
                }
                holder.downloadType[pos].setVisibility(View.GONE);
                if (mNeedShowDownloadType) {
                    if (videoModel.getDownloadType() == CommonConstant.SD_MODE) {
                        holder.downloadType[pos].setText(mContext.getString(R.string.video_sd_mode));
                    } else if (videoModel.getDownloadType() == CommonConstant.HD_MODE) {
                        holder.downloadType[pos].setText(mContext.getString(R.string.video_hd_mode));
                    } else if (videoModel.getDownloadType() == CommonConstant.UD_MODE) {
                        holder.downloadType[pos].setText(mContext.getString(R.string.video_ud_mode));
                    }
                    holder.downloadType[pos].setVisibility(View.VISIBLE);
                }
                mImageLoader.displayImage(StorageHelper.getImageUrl(videoModel.getImage()), holder.picture[pos], mOptions);

                holder.view[pos].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(v, videoModel, pos);
                        }

                    }
                });
            }
            holder.view[pos].getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.view[pos].getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    } else {
                        holder.view[pos].getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) holder.view[pos].getLayoutParams();
                    layoutParams.height = holder.view[pos].getHeight();
                    holder.view[pos].requestLayout();
                }
            });

        }

        return convertView;
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
        ImageView delete[] = new ImageView[2];
        TextView downloadType[] = new TextView[2];
        TextView vipTexts[] = new TextView[2];
        View topLines[] = new View[2];
    }

    public void clear() {
        mImageLoader.stop();
        mImageLoader.clearMemoryCache();
        System.gc();
        System.runFinalization();
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setNeedShowDelete(boolean needShowDelete) {
        mNeedShowDelete = needShowDelete;
        notifyDataSetChanged();
    }

    public boolean isNeedShowDelete() {
        return mNeedShowDelete;
    }

    public void removeVideoModel(VideoModel videoModel) {
        mVideoList.remove(videoModel);
        notifyDataSetChanged();
    }

    private int mItemWidth;

    public void setItemWidth(int itemWidth) {
        mItemWidth = itemWidth;
    }
}
