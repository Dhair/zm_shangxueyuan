package com.zm.shangxueyuan.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sdk.download.providers.DownloadManager;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.VideoDownloadModel;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.listener.OnItemClickListener;
import com.zm.shangxueyuan.utils.CommonUtils;
import com.zm.shangxueyuan.utils.ImageLoadUtil;
import com.zm.shangxueyuan.utils.ResUtil;
import com.zm.utils.PhoneUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Creator: dengshengjin on 16/4/17 13:54
 * Email: deng.shengjin@zuimeia.com
 */
public class VideoDownloadAdapter extends BaseAdapter {
    private List<VideoDownloadModel> mDownloadList;
    private Context mContext;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private int mDisplayWidth;

    private boolean mNeedShowTopicFlag;
    private boolean mNeedShowDelete;
    private boolean mNeedShowDownloadType;

    public VideoDownloadAdapter(Context context, boolean needShowTopicFlag, boolean needShowDownloadType) {
        mContext = context;
        mNeedShowTopicFlag = needShowTopicFlag;
        mNeedShowDownloadType = needShowDownloadType;
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
        if (mDownloadList == null) {
            return 0;
        }
        int size = mDownloadList.size();
        return (int) Math.ceil(size / 2.0f);
    }

    @Override
    public Object getItem(int position) {
        return mDownloadList.get(position);
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
            convertView = View.inflate(mContext, R.layout.fragment_download_video_item, null);

            int layoutW = (int) Math.ceil(mDisplayWidth / 2.0f);

            for (int i = 0; i < 2; i++) {
                holder.view[i] = convertView.findViewById(ResUtil.getInstance(mContext).viewId("view" + i));
                holder.picture[i] = (ImageView) holder.view[i].findViewById(R.id.image);
                holder.title[i] = (TextView) holder.view[i].findViewById(R.id.title);
                holder.subTitle[i] = (TextView) holder.view[i].findViewById(R.id.sub_title);
                holder.flag[i] = (TextView) holder.view[i].findViewById(R.id.flag_text);
                holder.delete[i] = (ImageView) holder.view[i].findViewById(R.id.delete_image);
                holder.downloadType[i] = (TextView) holder.view[i].findViewById(R.id.download_type_text);

                holder.downloadProgressBar[i] = (ProgressBar) holder.view[i].findViewById(R.id.download_progress_bar);
                holder.downloadBox[i] = (RelativeLayout) holder.view[i].findViewById(R.id.download_box);
                holder.downloadTips[i] = (TextView) holder.view[i].findViewById(R.id.download_tips_text);
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
            if (i >= mDownloadList.size()) {
                holder.view[pos].setVisibility(View.INVISIBLE);
                holder.view[pos].setOnClickListener(null);
            } else {
                if (holder.view[pos].getVisibility() != View.VISIBLE) {
                    holder.view[pos].setVisibility(View.VISIBLE);
                }

                final VideoDownloadModel videoModel = mDownloadList.get(i);
                holder.title[pos].setText(videoModel.mTitle);
                holder.subTitle[pos].setText(videoModel.mSubTitle);
                holder.flag[pos].setVisibility(View.GONE);
                if (mNeedShowTopicFlag && VideoModel.isTopicVideo(videoModel.mType)) {
                    holder.flag[pos].setVisibility(View.VISIBLE);
                }
                holder.delete[pos].setVisibility(View.GONE);
                if (mNeedShowDelete) {
                    holder.delete[pos].setVisibility(View.VISIBLE);
                }
                holder.downloadType[pos].setVisibility(View.GONE);
                if (mNeedShowDownloadType) {
                    if (videoModel.mDownloadType == CommonConstant.SD_MODE) {
                        holder.downloadType[pos].setText(mContext.getString(R.string.video_sd_mode));
                    } else if (videoModel.mDownloadType == CommonConstant.HD_MODE) {
                        holder.downloadType[pos].setText(mContext.getString(R.string.video_hd_mode));
                    } else if (videoModel.mDownloadType == CommonConstant.UD_MODE) {
                        holder.downloadType[pos].setText(mContext.getString(R.string.video_ud_mode));
                    }
                    holder.downloadType[pos].setVisibility(View.VISIBLE);
                }
                if (videoModel.mStatus == DownloadManager.STATUS_SUCCESSFUL) {
                    holder.downloadBox[pos].setVisibility(View.GONE);
                } else {
                    if (videoModel.mStatus == DownloadManager.STATUS_PENDING) {
                        holder.downloadBox[pos].setVisibility(View.VISIBLE);
                        holder.downloadTips[pos].setText(mContext.getString(R.string.video_download_status_pending));
                    } else if (videoModel.mStatus == DownloadManager.STATUS_PAUSED) {
                        holder.downloadBox[pos].setVisibility(View.VISIBLE);
                        holder.downloadTips[pos].setText(mContext.getString(R.string.video_download_status_paused));
                    } else if (videoModel.mStatus == DownloadManager.STATUS_RUNNING) {
                        holder.downloadBox[pos].setVisibility(View.VISIBLE);
                        holder.downloadProgressBar[pos].setProgress(videoModel.mProgress);
                        holder.downloadTips[pos].setText(videoModel.mProgressTips);
                    } else {
                        holder.downloadBox[pos].setVisibility(View.GONE);
                    }
                }
                mImageLoader.displayImage(StorageHelper.getImageUrl(videoModel.mImage), holder.picture[pos], mOptions);

                holder.view[pos].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(v, videoModel, pos);
                        }

                    }
                });
                holder.downloadBox[pos].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnDownloadItemClickListener != null) {
                            mOnDownloadItemClickListener.onItemClick(v, videoModel, pos);
                        }
                    }
                });
            }
        }

        return convertView;
    }

    static class ViewHolder {
        View view[] = new View[2];
        ImageView picture[] = new ImageView[2];
        TextView title[] = new TextView[2];
        TextView subTitle[] = new TextView[2];
        TextView flag[] = new TextView[2];
        ImageView delete[] = new ImageView[2];
        TextView downloadType[] = new TextView[2];

        RelativeLayout downloadBox[] = new RelativeLayout[2];
        ProgressBar downloadProgressBar[] = new ProgressBar[2];
        TextView downloadTips[] = new TextView[2];
    }

    public void clear() {
        mImageLoader.stop();
        mImageLoader.clearMemoryCache();
        System.gc();
        System.runFinalization();
    }

    private OnItemClickListener mOnItemClickListener, mOnDownloadItemClickListener;

    public void addModel(VideoDownloadModel downloadModel) {
        if (downloadModel == null) {
            return;
        }
        if (mDownloadList == null) {
            mDownloadList = new LinkedList<>();
            mDownloadList.add(downloadModel);
        } else {
            if (needAddToList(downloadModel)) {
                mDownloadList.add(downloadModel);
            }
        }
        notifyDataSetChanged();
    }

    private boolean needAddToList(VideoDownloadModel downloadModel) {
        boolean needAdd = true;
        for (int i = 0, size = mDownloadList.size(); i < size; i++) {
            VideoDownloadModel videoModel = mDownloadList.get(i);
            if (videoModel.equals(downloadModel)) {
                videoModel.update(downloadModel);
                needAdd = false;
                break;
            }
        }
        return needAdd;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setOnDownloadItemClickListener(OnItemClickListener onDownloadItemClickListener) {
        mOnDownloadItemClickListener = onDownloadItemClickListener;
    }

    public void setNeedShowDelete(boolean needShowDelete) {
        mNeedShowDelete = needShowDelete;
        notifyDataSetChanged();
    }

    public boolean isNeedShowDelete() {
        return mNeedShowDelete;
    }

    public void removeVideoModel(VideoDownloadModel downloadModel) {
        mDownloadList.remove(downloadModel);
        notifyDataSetChanged();
    }


}
