package com.zm.shangxueyuan.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.helper.StorageHelper;
import com.zm.shangxueyuan.model.GalleryCategoryModel;
import com.zm.shangxueyuan.model.GalleryTopicModel;
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
public class GalleryAdapter extends BaseExpandableListAdapter {
    private List<GalleryCategoryModel> mCategoryList;
    private List<GalleryTopicModel> mTopicList;
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
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
        GalleryCategoryModel categoryModel = mCategoryList.get(groupPosition);
        holder.titleText.setText(categoryModel.getTitle());
        holder.moreBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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


                    }
                });
            }
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
}
