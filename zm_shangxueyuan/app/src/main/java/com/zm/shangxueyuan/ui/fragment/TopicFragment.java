package com.zm.shangxueyuan.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.activity.MineActivity;
import com.zm.shangxueyuan.ui.activity.VideoDetailActivity;
import com.zm.shangxueyuan.ui.adapter.ContentItemAdapter;
import com.zm.shangxueyuan.ui.provider.BusProvider;
import com.zm.shangxueyuan.ui.provider.event.EmptyDataEvent;

import java.util.List;

public class TopicFragment extends BaseFragment {
    protected GridView headerGridView;
    protected BaseAdapter adapter;
    protected Handler mHandler = new Handler();
    protected PopupWindow popupWindow;
    protected LayoutInflater inflater;
    protected ImageLoader imgImageLoader;
    protected List<VideoModel> videoList;
    private long topicId;
    public final static String TOPIC_ID = "topicId";

    public static TopicFragment newInstance() {
        TopicFragment fragment = new TopicFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        topicId = getActivity().getIntent().getLongExtra(TOPIC_ID, 0);
        imgImageLoader = ImageLoader.getInstance();
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = getNoDataView();
        popupWindow = new PopupWindow(layout, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(false);
        initObserver();
    }

    protected View getNoDataView() {
        View view = inflater.inflate(R.layout.fragment_content_nodata, null);
        TextView text = (TextView) view.findViewById(R.id.nodata_text);
        text.setText(getEmptyText());
        return view;
    }

    protected String getEmptyText() {
        return getString(R.string.nodata_str);
    }

    @Override
    protected void initData() {
        imgImageLoader = ImageLoader.getInstance();
        getData();
        if (observer != null) {
            adapter.registerDataSetObserver(observer);
        }
    }

    protected void getData() {
        adapter = new ContentItemAdapter(getApplicationContext(), null, imgImageLoader, 0);
        queryData();
    }

    protected void queryData() {
        videoList = VideoDBUtil.getTopicVideos(topicId);
    }

    @Override
    protected View initViews(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content_grid, container, false);
        headerGridView = (StickyGridHeadersGridView) view.findViewById(R.id.grid_view);
        headerGridView.setAdapter(adapter);
        PauseOnScrollListener listener = new PauseOnScrollListener(imgImageLoader, true, true);
        headerGridView.setOnScrollListener(listener);
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                ((ContentItemAdapter) adapter).setVideoList(videoList);
                adapter.notifyDataSetChanged();
            }
        }, 100);

        return view;
    }

    @Override
    protected void initWidgetActions() {

        headerGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                VideoModel videoModel = ((ContentItemAdapter) adapter).getItem(position);
                onMyItemClick(videoModel, position);
            }
        });

    }

    protected void onMyItemClick(VideoModel videoModel, int position) {
        if (VideoModel.isTopicVideo(videoModel)) {
            Intent intent = MineActivity.getIntent(getActivity(), MineActivity.MINE_TYPE_TOPIC, videoModel.getTitle());
            intent.putExtra(TopicFragment.TOPIC_ID, videoModel.getVideoId());
            startActivity(intent);
        } else {
            if (!videoModel.isClicked()) {
                videoModel.setClicked(true);
                videoModel.save();
                videoList.get(position).setClicked(true);
                ((ContentItemAdapter) adapter).setVideoList(videoList);
                adapter.notifyDataSetChanged();
            }
            startActivity(VideoDetailActivity.getIntent(getActivity(), videoModel.getVideoId()));
        }
    }

    private DataSetObserver observer;

    private void initObserver() {
        if (observer == null) {
            observer = new DataSetObserver() {

                @Override
                public void onChanged() {
                    super.onChanged();
                    if (adapter.isEmpty()) {

                        int[] xy = new int[2];
                        headerGridView.getLocationOnScreen(xy);
                        if (isResumed()) {
                            popupWindow.showAtLocation(headerGridView, Gravity.TOP | Gravity.LEFT, xy[0], xy[1]);// 设置在屏幕中的显示位置
                        }
                        BusProvider.getInstance().post(new EmptyDataEvent(true));
                    } else {
                        BusProvider.getInstance().post(new EmptyDataEvent(false));
                        popupWindow.dismiss();
                    }
                }

                @Override
                public void onInvalidated() {
                    super.onInvalidated();
                    onChanged();
                }
            };

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        popupWindow.dismiss();
        mHandler.removeMessages(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (observer != null) {
            try {
                adapter.unregisterDataSetObserver(observer);
            } catch (Exception e) {
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves so that we can provide the initial value.
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Always unregister when an object no longer should be on the bus.
        try {
            BusProvider.getInstance().unregister(this);
        } catch (Exception e) {
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.no_anim);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.no_anim);
    }

}
