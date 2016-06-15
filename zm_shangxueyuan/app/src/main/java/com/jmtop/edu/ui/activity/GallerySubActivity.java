package com.jmtop.edu.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ListView;

import com.jmtop.edu.R;
import com.jmtop.edu.model.GalleryTopicModel;
import com.jmtop.edu.restful.ReqRestAdapter;
import com.jmtop.edu.restful.RestfulRequest;
import com.jmtop.edu.ui.adapter.GalleryAdapter;
import com.jmtop.edu.ui.listener.OnItemClickListener;
import com.jmtop.edu.ui.widget.LoadingEmptyView;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creator: dengshengjin on 16/4/21 22:34
 * Email: deng.shengjin@zuimeia.com
 * <p/>
 * 分类下的子分类
 */
public class GallerySubActivity extends AbsLoadingEmptyActivity {
    private GalleryTopicModel mModel;
    private static final String MODEL = "model";
    private ListView mListView;
    private Executor mExecutor = Executors.newCachedThreadPool();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private GalleryAdapter mAdapter;
    private RestfulRequest mRequest;

    public static Intent getIntent(Context context, GalleryTopicModel galleryTopicModel) {
        Intent intent = new Intent(context, GallerySubActivity.class);
        intent.putExtra(MODEL, galleryTopicModel);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void initData() {
        mRequest = ReqRestAdapter.getInstance(getContext()).create(RestfulRequest.class);
        mAdapter = new GalleryAdapter(getApplicationContext());
        Object object = getIntent().getSerializableExtra(MODEL);
        if (object != null) {
            mModel = (GalleryTopicModel) object;
        }
    }

    @Override
    protected void initWidgets() {
        setActionTitle(mModel.getTitle());
        mListView = (ListView) findViewById(R.id.list_view);
        mListView.setAdapter(mAdapter);
    }

    @Override
    protected void initWidgetsActions() {
        mAdapter.setOnItemClickListener(new OnItemClickListener<GalleryTopicModel>() {

            @Override
            public void onItemClick(View v, GalleryTopicModel galleryTopicModel, int position) {
                if (galleryTopicModel.isHasSubTopic()) {
                    startActivity(GallerySubActivity.getIntent(getApplicationContext(), galleryTopicModel));
                } else {
                    startActivity(GalleryListActivity.getIntent(getApplicationContext(), galleryTopicModel));
                }
            }
        });
        onLoadData();
    }

    private void onLoadData() {
        showLoading();
        mRequest.queryGallerySubTopics(mModel.getTopicId(), "", new Callback<JSONObject>() {
            @Override
            public void success(final JSONObject jsonObject, Response response) {
                mExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        final List<GalleryTopicModel> topicModels = GalleryTopicModel.parseGalleryTopics(jsonObject);
                        if (topicModels == null || topicModels.isEmpty()) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showEmpty();
                                }
                            });
                            return;
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                hideLoading();
                                mAdapter.setGalleryList(topicModels);
                                mAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                showLoadFail(new LoadingEmptyView.LoadViewCallback() {
                    @Override
                    public void callback() {
                        onLoadData();
                    }
                });
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gallery_more;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }
}
