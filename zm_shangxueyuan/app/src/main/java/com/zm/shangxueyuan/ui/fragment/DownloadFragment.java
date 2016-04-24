package com.zm.shangxueyuan.ui.fragment;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;

import com.sdk.download.providers.DownloadManager;
import com.sdk.download.providers.downloads.Downloads;
import com.squareup.otto.Subscribe;
import com.zm.shangxueyuan.R;
import com.zm.shangxueyuan.db.VideoDBUtil;
import com.zm.shangxueyuan.helper.DownloadManagerHelper;
import com.zm.shangxueyuan.model.VideoDownloadModel;
import com.zm.shangxueyuan.model.VideoModel;
import com.zm.shangxueyuan.ui.provider.event.MineDataEditEvent;

import java.io.File;
import java.net.URLDecoder;

public final class DownloadFragment extends AbsDownloadFragment {

    public static DownloadFragment newInstance() {
        DownloadFragment fragment = new DownloadFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Subscribe
    public void menuClick(MineDataEditEvent event) {
        onEditEvent();
    }

    protected String getEmptyTxt() {
        return getString(R.string.download_nodata_str);
    }

    @Override
    protected void onStopFragmentEvent() {

    }

    @Override
    protected void initWidgetActions() {
        super.initWidgetActions();
        loadData();
    }

    private DownloadChangeObserver downloadObserver;

    @Override
    protected void registerDataSetObserver() {
        super.registerDataSetObserver();
        if (downloadObserver == null) {
            downloadObserver = new DownloadChangeObserver(null);
        }
        getActivity().getContentResolver().registerContentObserver(Downloads.getContentURI(getApplicationContext()), true, downloadObserver);
    }

    @Override
    protected void unregisterDataSetObserver() {
        super.unregisterDataSetObserver();
        getActivity().getContentResolver().unregisterContentObserver(downloadObserver);
    }

    final class DownloadChangeObserver extends ContentObserver {

        public DownloadChangeObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            loadData();
        }

    }

    private void loadData() {
        getExecutor().execute(new Runnable() {
            @Override
            public void run() {
                final DownloadManager.Query query = new DownloadManager.Query();
                Cursor cursor = null;
                try {
                    cursor = mDownloadManager.query(query);
                    while (cursor.moveToNext()) {
                        long downloadId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID));
                        String downloadUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_DOWNLOAD_URL));
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        long videoId = DownloadManagerHelper.getVideoIdByUrl(downloadUrl);
                        int videoType = DownloadManagerHelper.getVideoTypeByUrl(downloadUrl);
                        String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        localUri = URLDecoder.decode(localUri, "UTF-8");
                        localUri = DownloadManagerHelper.getFilePath(localUri);
                        long currentBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        long totalBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        switch (status) {
                            case DownloadManager.STATUS_PAUSED:
                            case DownloadManager.STATUS_PENDING:
                            case DownloadManager.STATUS_RUNNING:
                            case DownloadManager.STATUS_SUCCESSFUL:
                                VideoModel videoModel = VideoDBUtil.queryVideo(videoId);
                                VideoDownloadModel downloadModel = new VideoDownloadModel();
                                downloadModel.mVideoId = videoId;
                                downloadModel.mDownloadType = videoType;
                                downloadModel.mDownloadId = downloadId;
                                downloadModel.mFilePath = localUri;
                                downloadModel.mStatus = status;
                                final int progress = getProgressValue(totalBytes, currentBytes);
                                downloadModel.mProgress = progress;
                                String progressTips = Formatter.formatFileSize(getApplicationContext(), currentBytes) + "/" + Formatter.formatFileSize(getApplicationContext(), totalBytes);
                                downloadModel.mProgressTips = progressTips;
                                downloadModel.mTitle = videoModel.getTitle();
                                downloadModel.mSubTitle = videoModel.getSubTitle();
                                downloadModel.mImage = videoModel.getImage();
                                downloadModel.mType = videoModel.getType();
                                getVideoAdapter().addModel(downloadModel);

                                break;
                            case DownloadManager.STATUS_FAILED:
                                mDownloadManager.remove(downloadId);
                                new File(localUri).delete();
                                break;
                        }

                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
        });
    }

    private int getProgressValue(long totalBytes, long currentBytes) {
        if (totalBytes == -1) {
            return 0;
        }
        return (int) (currentBytes * 100 / totalBytes);
    }
}
