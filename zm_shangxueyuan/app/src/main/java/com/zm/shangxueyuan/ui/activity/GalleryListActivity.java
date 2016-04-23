package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.GridView;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSPlainTextAKSKCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.constant.CommonConstant;
import com.zm.shangxueyuan.model.GalleryModel;
import com.zm.shangxueyuan.model.GalleryTopicModel;
import com.zm.shangxueyuan.ui.adapter.GalleryListAdapter;
import com.zm.shangxueyuan.ui.listener.OnItemClickListener;
import com.zm.shangxueyuan.ui.widget.LoadingEmptyView;
import com.zm.utils.PhoneUtil;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;

/**
 * Creator: dengshengjin on 16/4/20 08:48
 * Email: deng.shengjin@zuimeia.com
 * <p/>
 * 图片列表
 */
public class GalleryListActivity extends AbsLoadingEmptyActivity {
    private GalleryTopicModel mModel;
    private static final String MODEL = "model";
    private OSS mOss;
    private Executor mExecutor = Executors.newCachedThreadPool();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    @Bind(R.id.gallery_grid)
    GridView mGridView;
    private GalleryListAdapter mAdapter;
    private int mWidth;

    public static Intent getIntent(Context context, GalleryTopicModel model) {
        Intent intent = new Intent(context, GalleryListActivity.class);
        intent.putExtra(MODEL, model);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void initData() {
        mWidth = PhoneUtil.getDisplayWidth(getContext());
        mWidth = (int) (mWidth / 4.0f);
        mAdapter = new GalleryListAdapter(getApplicationContext(), mWidth);
        Object object = getIntent().getSerializableExtra(MODEL);
        if (object != null) {
            mModel = (GalleryTopicModel) object;
        }
        OSSCredentialProvider credentialProvider = new OSSPlainTextAKSKCredentialProvider(CommonConstant.accessKeyId, CommonConstant.accessKeySecret);

        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求书，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSSLog.enableLog();
        mOss = new OSSClient(getApplicationContext(), CommonConstant.endpoint, credentialProvider, conf);
    }

    @Override
    protected void initWidgets() {
        setActionTitle(mModel.getTitle());
        mGridView.setAdapter(mAdapter);
    }

    @Override
    protected void initWidgetsActions() {
        mAdapter.setOnItemClickListener(new OnItemClickListener<LinkedList<GalleryModel>>() {
            @Override
            public void onItemClick(View v, LinkedList<GalleryModel> galleryList, int position) {
                startActivity(GalleryPreviewActivity.getIntent(getApplicationContext(), position, galleryList));
            }
        });
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                syncListObjects();
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_gallery;
    }

    public void syncListObjects() {
        showLoading();
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                String prefix = String.format("gallery/topic/%s/", mModel.getUploadTitle());
                ListObjectsRequest listObjects = new ListObjectsRequest(CommonConstant.bucketName, prefix, null, null, null);
                OSSAsyncTask task = mOss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
                    @Override
                    public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                        if (result == null || result.getObjectSummaries() == null || result.getObjectSummaries().isEmpty()) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    showEmpty();
                                }
                            });
                            return;
                        }
                        final List<GalleryModel> galleryModels = GalleryModel.parseGalleryList(getApplicationContext(), result, mModel.getUploadTitle());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                hideLoading();
                                mAdapter.setGalleryList(galleryModels);
                                mAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                        // 请求异常
                        if (clientExcepion != null) {
                            // 本地异常如网络异常等
                            clientExcepion.printStackTrace();
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                showLoadFail(new LoadingEmptyView.LoadViewCallback() {
                                    @Override
                                    public void callback() {
                                        syncListObjects();
                                    }
                                });
                            }
                        });
                    }
                });
                task.waitUntilFinished();
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.clear();
        }
    }
}
