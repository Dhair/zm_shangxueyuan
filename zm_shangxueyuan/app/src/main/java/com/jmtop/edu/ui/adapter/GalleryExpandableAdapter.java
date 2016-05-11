package com.jmtop.edu.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.jmtop.edu.R;
import com.jmtop.edu.helper.StorageHelper;
import com.jmtop.edu.model.GalleryCategoryModel;
import com.jmtop.edu.model.GalleryTopicModel;
import com.jmtop.edu.ui.listener.OnItemClickListener;
import com.jmtop.edu.utils.CommonUtils;
import com.jmtop.edu.utils.ImageLoadUtil;
import com.jmtop.edu.utils.ResUtil;
import com.zm.utils.PhoneUtil;

import java.util.LinkedList;
import java.util.List;

/**
 * Creator: dengshengjin on 16/4/17 13:54
 * Email: deng.shengjin@zuimeia.com
 */
public class GalleryExpandableAdapter extends BaseExpandableListAdapter {
    private List<GalleryCategoryModel> mCategoryList;
    private List<GalleryTopicModel> mTopicList;
    private Context mContext;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private int mDisplayWidth;

    public GalleryExpandableAdapter(Context context) {
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


    static class ChildViewHolder {
        View view[] = new View[2];
        ImageView picture[] = new ImageView[2];
        TextView title[] = new TextView[2];
        TextView subTitle[] = new TextView[2];
    }

    static class GroupViewHolder {
        TextView titleText;
        LinearLayout moreBox;
    }

    public void clear() {
        mImageLoader.stop();
        mImageLoader.clearMemoryCache();
        System.gc();
        System.runFinalization();
    }

    @Override
    public int getGroupCount() {
        if (mCategoryList == null) {
            return 0;
        }
        return mCategoryList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mCategoryList == null || mCategoryList.size() < groupPosition) {
            return 0;
        }
        GalleryCategoryModel galleryCategoryModel = mCategoryList.get(groupPosition);
        List<GalleryTopicModel> galleryTopicList = findTopics(galleryCategoryModel.getCategoryId());
        if (galleryTopicList == null) {
            return 0;
        }
        int size = galleryTopicList.size();
        return (int) Math.ceil(size / 2.0f);
    }

    @Override
    public GalleryCategoryModel getGroup(int groupPosition) {
        return mCategoryList.get(groupPosition);
    }

    @Override
    public GalleryTopicModel getChild(int groupPosition, int childPosition) {
        GalleryCategoryModel galleryCategoryModel = mCategoryList.get(groupPosition);
        return findTopics(galleryCategoryModel.getCategoryId()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder holder;
        if (convertView == null) {
            holder = new GroupViewHolder();
            convertView = View.inflate(mContext, R.layout.fragment_home_gallery_group, null);

            holder.titleText = (TextView) convertView.findViewById(R.id.title);
            holder.moreBox = (LinearLayout) convertView.findViewById(R.id.more_box);
            convertView.setTag(holder);
        } else {
            holder = (GroupViewHolder) convertView.getTag();
        }
        final GalleryCategoryModel categoryModel = mCategoryList.get(groupPosition);
        holder.titleText.setText(categoryModel.getTitle());
        holder.moreBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGalleryCategoryOnItemClickListener != null) {
                    mGalleryCategoryOnItemClickListener.onItemClick(v, categoryModel, groupPosition);
                }
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildViewHolder holder;
        if (convertView == null) {
            holder = new ChildViewHolder();
            convertView = View.inflate(mContext, R.layout.fragment_home_gallery_child, null);

            int layoutW = (int) Math.ceil(mDisplayWidth / 2.0f);
            for (int i = 0; i < 2; i++) {
                holder.view[i] = convertView.findViewById(ResUtil.getInstance(mContext).viewId("view" + i));
                holder.picture[i] = (ImageView) holder.view[i].findViewById(R.id.image);
                holder.title[i] = (TextView) holder.view[i].findViewById(R.id.title);
                holder.subTitle[i] = (TextView) holder.view[i].findViewById(R.id.sub_title);
                ViewGroup.LayoutParams layoutParamsLeft = holder.view[i].getLayoutParams();
                layoutParamsLeft.width = layoutW;
            }

            convertView.setTag(holder);
        } else {
            holder = (ChildViewHolder) convertView.getTag();
        }
        GalleryCategoryModel categoryModel = mCategoryList.get(groupPosition);
        List<GalleryTopicModel> galleryTopicList = findTopics(categoryModel.getCategoryId());
        int start = childPosition * 2;
        int end = start + 2;
        for (int i = start; i < end; i++) {
            final int pos = i % 2;
            if (i >= galleryTopicList.size()) {
                holder.view[pos].setVisibility(View.INVISIBLE);
                holder.view[pos].setOnClickListener(null);
            } else {
                if (holder.view[pos].getVisibility() != View.VISIBLE) {
                    holder.view[pos].setVisibility(View.VISIBLE);
                }

                final GalleryTopicModel videoModel = galleryTopicList.get(i);
                holder.title[pos].setText(videoModel.getTitle());
                holder.subTitle[pos].setText(videoModel.getSubTitle());
                mImageLoader.displayImage(StorageHelper.getImageUrl(videoModel.getImage()), holder.picture[pos], mOptions);

                holder.view[pos].setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (mGalleryTopicModelOnItemClickListener != null) {
                            mGalleryTopicModelOnItemClickListener.onItemClick(v, videoModel, 0);
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

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void setCategoryList(List<GalleryCategoryModel> categoryList) {
        mCategoryList = categoryList;
    }

    public void setTopicList(List<GalleryTopicModel> topicList) {
        mTopicList = topicList;
    }

    public List<GalleryTopicModel> findTopics(long categoryId) {
        if (mTopicList == null) {
            return null;
        }
        List<GalleryTopicModel> topicList = new LinkedList<>();
        for (int i = 0, size = mTopicList.size(); i < size; i++) {
            GalleryTopicModel galleryTopicModel = mTopicList.get(i);
            if (galleryTopicModel.getCategoryId() == categoryId) {
                topicList.add(galleryTopicModel);
            }
        }
        return topicList;
    }

    public int getSelection(int groupPosition) {
        int totalPosition = 0;
        for (int i = 0; i < groupPosition; i++) {
            totalPosition += getChildrenCount(i) + 1;
        }
        return totalPosition;
    }

    private OnItemClickListener<GalleryCategoryModel> mGalleryCategoryOnItemClickListener;
    private OnItemClickListener<GalleryTopicModel> mGalleryTopicModelOnItemClickListener;

    public void setGalleryCategoryOnItemClickListener(OnItemClickListener<GalleryCategoryModel> galleryCategoryOnItemClickListener) {
        mGalleryCategoryOnItemClickListener = galleryCategoryOnItemClickListener;
    }

    public void setGalleryTopicModelOnItemClickListener(OnItemClickListener<GalleryTopicModel> galleryTopicModelOnItemClickListener) {
        mGalleryTopicModelOnItemClickListener = galleryTopicModelOnItemClickListener;
    }
}
