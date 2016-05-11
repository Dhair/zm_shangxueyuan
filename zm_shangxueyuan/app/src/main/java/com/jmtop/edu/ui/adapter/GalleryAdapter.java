package com.jmtop.edu.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.jmtop.edu.R;
import com.jmtop.edu.helper.StorageHelper;
import com.jmtop.edu.model.GalleryTopicModel;
import com.jmtop.edu.ui.listener.OnItemClickListener;
import com.jmtop.edu.utils.CommonUtils;
import com.jmtop.edu.utils.ImageLoadUtil;
import com.jmtop.edu.utils.ResUtil;
import com.zm.utils.PhoneUtil;

import java.util.List;

/**
 * Creator: dengshengjin on 16/4/17 13:54
 * Email: deng.shengjin@zuimeia.com
 */
public class GalleryAdapter extends BaseAdapter {
    private List<GalleryTopicModel> mGalleryList;
    private Context mContext;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private int mDisplayWidth;


    public GalleryAdapter(Context context) {
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
        if (mGalleryList == null) {
            return 0;
        }
        int size = mGalleryList.size();
        return (int) Math.ceil(size / 2.0f);
    }

    @Override
    public Object getItem(int position) {
        return mGalleryList.get(position);
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

            int layoutW = (int) Math.ceil(mDisplayWidth / 2.0f);

            for (int i = 0; i < 2; i++) {
                holder.view[i] = convertView.findViewById(ResUtil.getInstance(mContext).viewId("view" + i));
                holder.picture[i] = (ImageView) holder.view[i].findViewById(R.id.image);
                holder.title[i] = (TextView) holder.view[i].findViewById(R.id.title);
                holder.subTitle[i] = (TextView) holder.view[i].findViewById(R.id.sub_title);
                holder.flag[i] = (TextView) holder.view[i].findViewById(R.id.flag_text);
                holder.delete[i] = (ImageView) holder.view[i].findViewById(R.id.delete_image);
                holder.downloadType[i] = (TextView) holder.view[i].findViewById(R.id.download_type_text);
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
            if (i >= mGalleryList.size()) {
                holder.view[pos].setVisibility(View.INVISIBLE);
                holder.view[pos].setOnClickListener(null);
            } else {
                if (holder.view[pos].getVisibility() != View.VISIBLE) {
                    holder.view[pos].setVisibility(View.VISIBLE);
                }

                final GalleryTopicModel galleryTopicModel = mGalleryList.get(i);
                holder.title[pos].setText(galleryTopicModel.getTitle());
                holder.subTitle[pos].setText(galleryTopicModel.getSubTitle());
                mImageLoader.displayImage(StorageHelper.getImageUrl(galleryTopicModel.getImage()), holder.picture[pos], mOptions);

                holder.view[pos].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(v, galleryTopicModel, pos);
                        }

                    }
                });
            }
        }

        return convertView;
    }

    public void setGalleryList(List<GalleryTopicModel> galleryList) {
        mGalleryList = galleryList;
    }

    static class ViewHolder {
        View view[] = new View[2];
        ImageView picture[] = new ImageView[2];
        TextView title[] = new TextView[2];
        TextView subTitle[] = new TextView[2];
        TextView flag[] = new TextView[2];
        ImageView delete[] = new ImageView[2];
        TextView downloadType[] = new TextView[2];
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

}
