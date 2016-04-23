package com.zm.shangxueyuan.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.SettingDBUtil;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.model.GalleryTopicModel;
import com.zm.shangxueyuan.model.KeywordModel;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.restful.ReqRestAdapter;
import com.zm.shangxueyuan.restful.RestfulRequest;
import com.zm.shangxueyuan.ui.adapter.GalleryAdapter;
import com.zm.shangxueyuan.ui.adapter.SearchKeywordAdapter;
import com.zm.shangxueyuan.ui.adapter.VideoAdapter;
import com.zm.shangxueyuan.ui.listener.OnItemClickListener;
import com.zm.utils.KeyBoardUtil;
import com.zm.utils.PhoneUtil;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.Bind;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Creator: dengshengjin on 16/4/20 08:49
 * Email: deng.shengjin@zuimeia.com
 */
public class SearchActivity extends AbsLoadingEmptyActivity {
    @Bind(R.id.search_edit)
    EditText mKeywordText;

    @Bind(R.id.search_video)
    TextView mSearchVideoText;

    @Bind(R.id.search_gallery)
    TextView mSearchGalleryText;

    @Bind(R.id.search_tips_title)
    TextView mTipsTitleText;

    @Bind(R.id.list_view)
    ListView mListView;

    private List<KeywordModel> mKeywordList;

    private static final int SEARCH_VIDEO = 1 << 2;
    private static final int SEARCH_GALLERY = 1 << 3;

    private SearchKeywordAdapter mSearchKeywordAdapter;
    private VideoAdapter mVideoAdapter;
    private GalleryAdapter mGalleryAdapter;
    private Executor mExecutor = Executors.newCachedThreadPool();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private RestfulRequest mRequest;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void initData() {
        mSearchKeywordAdapter = new SearchKeywordAdapter(getApplicationContext());
        mVideoAdapter = new VideoAdapter(getApplicationContext(), true);
        int itemWidth = (int) (PhoneUtil.getDisplayWidth(getContext()) / 2.0f);
        mVideoAdapter.setItemWidth(itemWidth);
        mGalleryAdapter = new GalleryAdapter(getApplicationContext());
        mRequest = ReqRestAdapter.getInstance(getContext()).create(RestfulRequest.class);
    }

    @Override
    protected void initWidgets() {
        setActionTitle(R.string.search_title);
        setActionBackActions(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOpenSoftKeyboard()) {
                    KeyBoardUtil.hideSoftKeyboard(SearchActivity.this);
                }
            }
        });
        mListView.setAdapter(mSearchKeywordAdapter);
    }

    private boolean isOpenSoftKeyboard() {
        if (KeyBoardUtil.isOpenSoftKeyboard(mKeywordText, getApplicationContext())) {
            return true;
        }
        return false;
    }

    @Override
    protected void initWidgetsActions() {
        mSearchVideoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mKeywordText.getText().toString();
                if (TextUtils.isEmpty(keyword) || isLoading()) {
                    return;
                }
                onKeywordEvent(SEARCH_VIDEO, keyword);
            }
        });
        mSearchGalleryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mKeywordText.getText().toString();
                if (TextUtils.isEmpty(keyword) || isLoading()) {
                    return;
                }
                onKeywordEvent(SEARCH_GALLERY, keyword);
            }
        });
        mSearchKeywordAdapter.setOnItemClickListener(new OnItemClickListener<KeywordModel>() {
            @Override
            public void onItemClick(View v, KeywordModel keywordModel, int position) {
                String keyword = keywordModel.getWord();
                if (TextUtils.isEmpty(keyword) || isLoading()) {
                    return;
                }
                onKeywordEvent(SEARCH_VIDEO, keyword);
                mKeywordText.setText(keyword);
                mKeywordText.setSelection(keyword.length());
                KeyBoardUtil.hideSoftKeyboard(SearchActivity.this);
            }
        });
        mKeywordText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = mKeywordText.getText().toString().trim();
                if (TextUtils.isEmpty(text) && !isLoading()) {
                    mListView.setAdapter(mSearchKeywordAdapter);
                    mSearchKeywordAdapter.setKeywordList(mKeywordList);
                    mSearchKeywordAdapter.notifyDataSetChanged();
                    hideLoading();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mVideoAdapter.setOnItemClickListener(new OnItemClickListener<VideoModel>() {
            @Override
            public void onItemClick(View v, VideoModel videoModel, int position) {
                if (VideoModel.isTopicVideo(videoModel)) {
                    startActivity(VideoTopicActivity.getIntent(getApplicationContext(), videoModel.getVideoId(), videoModel.getTitle()));
                } else {
                    startActivity(VideoDetailActivity.getIntent(getApplicationContext(), videoModel));
                }
            }
        });
        mGalleryAdapter.setOnItemClickListener(new OnItemClickListener<GalleryTopicModel>() {
            @Override
            public void onItemClick(View v, GalleryTopicModel galleryTopicModel, int position) {
                startActivity(GalleryListActivity.getIntent(getApplicationContext(), galleryTopicModel));
            }

        });
        mKeywordText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String keyword = mKeywordText.getText().toString();
                    if (!TextUtils.isEmpty(keyword) && !isLoading()) {
                        onKeywordEvent(SEARCH_VIDEO, keyword);
                    }
                    return true;
                }
                return false;

            }
        });
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mKeywordList = KeywordModel.parseHotWords(SettingDBUtil.getInstance(getApplicationContext()).getConfigServer());
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isFinishing()) {
                            return;
                        }
                        mSearchKeywordAdapter.setKeywordList(mKeywordList);
                        mSearchKeywordAdapter.notifyDataSetChanged();
                    }
                }, 100);

            }
        });
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                KeyBoardUtil.showSoftKeyboard(mKeywordText, SearchActivity.this);
            }
        }, 300);
    }

    private void onKeywordEvent(int type, final String keyword) {
        if (type == SEARCH_VIDEO) {
            mTipsTitleText.setText(getString(R.string.search_video_result));
            showLoading();
            mExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    final List<VideoModel> videoList = VideoDBUtil.queryVideosByKeyword(keyword);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isFinishing()) {
                                return;
                            }
                            if (videoList == null || videoList.isEmpty()) {
                                showEmptyWidthWarn(getString(R.string.search_no_result));
                                return;
                            }
                            mListView.setAdapter(mVideoAdapter);
                            mVideoAdapter.setVideoList(videoList);
                            mVideoAdapter.notifyDataSetChanged();
                            hideLoading();
                        }
                    }, 300);

                }
            });
        } else {
            mTipsTitleText.setText(getString(R.string.search_gallery_result));
            showLoading();
            mRequest.search(keyword, new Callback<JSONObject>() {
                @Override
                public void success(final JSONObject jsonObject, Response response) {
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            final List<GalleryTopicModel> topicList = GalleryTopicModel.parseGalleryTopics(jsonObject);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (isFinishing()) {
                                        return;
                                    }
                                    if (topicList == null || topicList.isEmpty()) {
                                        showEmptyWidthWarn(getString(R.string.search_no_result));
                                        return;
                                    }
                                    mListView.setAdapter(mGalleryAdapter);
                                    mGalleryAdapter.setGalleryList(topicList);
                                    mGalleryAdapter.notifyDataSetChanged();
                                    hideLoading();
                                }
                            }, 300);
                        }
                    });
                }

                @Override
                public void failure(RetrofitError error) {
                    showEmptyWidthWarn(getString(R.string.search_no_result));
                }
            });

        }
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_search;
    }
}
