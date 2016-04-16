package com.zm.shangxueyuan.ui.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.activeandroid.ActiveAndroid;
import com.squareup.otto.Subscribe;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.model.VideoStatusModel;
import com.zm.shangxueyuan.ui.adapter.ContentDeleteItemAdapter;
import com.zm.shangxueyuan.ui.provider.event.ContentEditEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author deng.shengjin
 * @version create_time:2014-3-15_下午11:41:13
 * @Description 我的收藏
 */
public class FavorFragment extends TopicFragment {
    protected Set<Long> set = new HashSet<Long>();

    public static FavorFragment newInstance() {
        FavorFragment fragment = new FavorFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void getData() {
        adapter = new ContentDeleteItemAdapter(getApplicationContext(), null, imgImageLoader, 0);
        videoList = VideoDBUtil.queryCollectedVideos();
    }

    @Override
    protected String getEmptyText() {
        return getString(R.string.fav_nodata_str);
    }

    @Subscribe
    public void menuClick(ContentEditEvent event) {
        if (event.isEditStatus()) {// 处于编辑状态-->完成状态
            if (adapter instanceof ContentDeleteItemAdapter) {
                ((ContentDeleteItemAdapter) adapter).setToEditStatus(false);
                adapter.notifyDataSetChanged();
            }
        } else {// 处于完成状态-->编辑状态
            if (adapter instanceof ContentDeleteItemAdapter) {
                ((ContentDeleteItemAdapter) adapter).setToEditStatus(true);
                adapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void initWidgetActions() {
        headerGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {
                ContentDeleteItemAdapter deleteAdapter = ((ContentDeleteItemAdapter) adapter);
                final VideoModel videoModel = deleteAdapter.getItem(position);
                if (deleteAdapter.isToEditStatus()) {
                    new AlertDialog.Builder(getActivity()).setMessage(R.string.video_delete_request).setTitle(R.string.tips)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }

                            }).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            set.add(videoModel.getVideoId());
                            videoList.remove(position);
                            ((ContentDeleteItemAdapter) adapter).setVideoList(videoList);
                            adapter.notifyDataSetChanged();
                        }
                    }).create().show();
                } else {
                    onMyItemClick(videoModel, position);
                }
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        onStopEvent();
    }

    /**
     * 设置成未收藏
     */
    protected void onStopEvent() {
        ActiveAndroid.beginTransaction();
        try {
            for (Iterator<Long> it = set.iterator(); it.hasNext(); ) {
                long videoId = it.next();
                VideoStatusModel videoStatusModel = VideoDBUtil.getStatus(videoId);
                if (videoStatusModel != null) {
                    videoStatusModel.setFavStatus(CommonConstant.UNFAV_STATUS);
                    videoStatusModel.save();
                }
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

}