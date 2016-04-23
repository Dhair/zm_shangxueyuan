/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sdk.download.providers.downloads;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.download_sdk.download.provider.R;

import java.util.Collection;
import java.util.HashMap;


/**
 * This class handles the updating of the Notification Manager for the cases
 * where there is an ongoing download. Once the download is complete (be it
 * successful or unsuccessful) it is no longer the responsibility of this
 * component to show the download in the notification manager. 1、setContentTitle
 * 通知标题 2、setLargeIcon 大图标 3、setContentText 通知内容 4、setContentInfo 通知消息
 * 5、setSmallIcon 小图标 6、
 */
class DownloadNotification {

	Context mContext;
	HashMap<Long, NotificationItem> mNotifications;
	private SystemFacade mSystemFacade;

	static final String LOGTAG = "DownloadNotification";
	static final String WHERE_RUNNING = "(" + Downloads.COLUMN_STATUS + " >= '100') AND (" + Downloads.COLUMN_STATUS + " <= '199') AND ("
			+ Downloads.COLUMN_VISIBILITY + " IS NULL OR " + Downloads.COLUMN_VISIBILITY + " == '" + Downloads.VISIBILITY_VISIBLE + "' OR "
			+ Downloads.COLUMN_VISIBILITY + " == '" + Downloads.VISIBILITY_VISIBLE_NOTIFY_COMPLETED + "')";
	static final String WHERE_COMPLETED = Downloads.COLUMN_STATUS + " >= '200' AND " + Downloads.COLUMN_VISIBILITY + " == '"
			+ Downloads.VISIBILITY_VISIBLE_NOTIFY_COMPLETED + "'";

	/**
	 * This inner class is used to collate downloads that are owned by the same
	 * application. This is so that only one notification line item is used for
	 * all downloads of a given application.
	 */
	static class NotificationItem {
		long mId; // This first db _id for the download for the app
		long mTotalCurrent = 0;
		long mTotalTotal = 0;
		int mTitleCount = 0;
		String mPackageName; // App package name
		String mDescription;
		String[] mTitles = new String[2]; // download titles.
		String mPausedText = null;
		int mStatus;

		/*
		 * Add a second download to this notification item.
		 */
		void addItem(String title, long currentBytes, long totalBytes) {
			mTotalCurrent += currentBytes;
			if (totalBytes <= 0 || mTotalTotal == -1) {
				mTotalTotal = -1;
			} else {
				mTotalTotal += totalBytes;
			}
			if (mTitleCount < 2) {
				mTitles[mTitleCount] = title;
			}
			mTitleCount++;
		}
	}

	/**
	 * Constructor
	 * 
	 * @param ctx
	 *            The context to use to obtain access to the Notification
	 *            Service
	 */
	DownloadNotification(Context ctx, SystemFacade systemFacade) {
		mContext = ctx;
		mSystemFacade = systemFacade;
		mNotifications = new HashMap<Long, NotificationItem>();
	}

	/*
	 * Update the notification ui.
	 */
	public void updateNotification(Collection<DownloadInfo> downloads) {
		updateActiveNotification(downloads);
		updateCompletedNotification(downloads);
	}

	private void updateActiveNotification(Collection<DownloadInfo> downloads) {
		// Collate the notifications
		mNotifications.clear();

		for (DownloadInfo download : downloads) {
			if (!isActiveAndVisible(download)) {
				continue;
			}

			String packageName = download.mPackage;
			long max = download.mTotalBytes;
			long progress = download.mCurrentBytes;
			long id = download.mId;
			String title = download.mTitle;
			if (title == null || title.length() == 0) {
				title = mContext.getResources().getString(R.string.zuimeia_sdk_download_download_unknown_title);
			}

			synchronized (mNotifications) {
				if (!mNotifications.containsKey(id)) {
					if (download.mStatus == Downloads.STATUS_RUNNING) {
						NotificationItem item = new NotificationItem();
						item.mId = id;
						item.mPackageName = packageName;
						item.mDescription = download.mDescription;
						item.mStatus = download.mStatus;
						item.addItem(title, progress, max);
						mNotifications.put(download.mId, item);
						if (download.mStatus == Downloads.STATUS_QUEUED_FOR_WIFI && item.mPausedText == null) {
							item.mPausedText = mContext.getResources().getString(R.string.zuimeia_sdk_download_notification_need_wifi_for_size);
						}
					} else {
						mSystemFacade.cancelNotification(id);
					}
				}
			}
		}

		// Add the notifications
		for (NotificationItem item : mNotifications.values()) {
			// Build the notification object
			final NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
			builder.setPriority(2);
			boolean hasPausedText = (item.mPausedText != null);
			int iconResource = android.R.drawable.stat_sys_download_done;
			if (hasPausedText) {
				iconResource = android.R.drawable.stat_sys_warning;
			}
			builder.setSmallIcon(iconResource);
			builder.setOngoing(true);

			boolean hasContentText = false;
			StringBuilder title = new StringBuilder(item.mTitles[0]);
			if (item.mTitleCount > 1) {
				title.append(mContext.getString(R.string.zuimeia_sdk_download_notification_filename_separator));
				title.append(item.mTitles[1]);
				if (item.mTitleCount > 2) {
					title.append(mContext.getString(R.string.zuimeia_sdk_download_notification_filename_extras,
							new Object[] { Integer.valueOf(item.mTitleCount - 2) }));
				}
			} else if (!TextUtils.isEmpty(item.mDescription)) {
				builder.setContentText(item.mDescription);
				hasContentText = true;
			}
			builder.setContentTitle(title);// 标题

			if (hasPausedText) {// 已经暂停
				builder.setContentText(item.mPausedText);
			} else {
				builder.setProgress((int) item.mTotalTotal, (int) item.mTotalCurrent, item.mTotalTotal == -1);
				if (Build.VERSION.SDK_INT >= 11) {
					if (hasContentText) {
						builder.setContentInfo(getDownloadingText(item.mTotalTotal, item.mTotalCurrent));
					}
				} else {// 适配android 3.0以下的
					builder.setContentText(String.format(mContext.getString(R.string.sdk_download_progress_percent),
							getDownloadingText(item.mTotalTotal, item.mTotalCurrent)));
				}
			}
			// 暂停(仅在下载中有效)
			// if (item.mStatus == Downloads.STATUS_PENDING || item.mStatus ==
			// Downloads.STATUS_RUNNING) {
			// Intent pauseIt = new
			// Intent(ZMDownloadReceiver.DOWNLOADRECEIVER_PAUSE);
			// pauseIt.setPackage(mContext.getPackageName());
			// Bundle bundle = new Bundle();
			// bundle.putLong(ZMDownloadReceiver.INTENT_DOWNLOAD_ID, item.mId);
			// bundle.putInt(ZMDownloadReceiver.INTENT_DOWNLOAD_STATUS,
			// item.mStatus);
			// pauseIt.putExtras(bundle);
			// builder.addAction(R.drawable.sdk_download_pause,
			// mContext.getString(R.string.zuimeia_sdk_download_notification_pause),
			// PendingIntent.getBroadcast(mContext, 0, pauseIt,
			// PendingIntent.FLAG_CANCEL_CURRENT));
			// }
			// 取消 
			if (item.mStatus != Downloads.STATUS_SUCCESS) {
				Intent cancelIt = new Intent(DownloadReceiver.DOWNLOAD_CANCEL);
				cancelIt.setPackage(mContext.getPackageName());
				Bundle bundle = new Bundle();
				bundle.putLong(DownloadReceiver.INTENT_DOWNLOAD_ID, item.mId);
				bundle.putInt(DownloadReceiver.INTENT_DOWNLOAD_STATUS, item.mStatus);
				cancelIt.putExtras(bundle);
				builder.addAction(R.drawable.sdk_download_download_cancel, mContext.getString(R.string.sdk_download_notification_cancel),
						PendingIntent.getBroadcast(mContext, 0, cancelIt, PendingIntent.FLAG_CANCEL_CURRENT));
			}

			Intent intent = new Intent(Constants.ACTION_LIST);
			intent.setClassName(mContext, DownloadReceiver.class.getName());
			intent.setData(ContentUris.withAppendedId(Downloads.getAllDownloadsContentURI(mContext), item.mId));
			intent.putExtra("multiple", item.mTitleCount > 1);

			builder.setContentIntent(PendingIntent.getBroadcast(mContext, 0, intent, 0));

			mSystemFacade.postNotification(item.mId, builder.build());
		}
	}

	private void updateCompletedNotification(Collection<DownloadInfo> downloads) {
		for (DownloadInfo download : downloads) {
			if (!isCompleteAndVisible(download)) {
				continue;
			}
			// Add the notifications
			Notification n = new Notification();
			n.icon = android.R.drawable.stat_sys_download_done;
			n.flags |= Notification.FLAG_AUTO_CANCEL;
			long id = download.mId;
			String title = download.mTitle;
			if (title == null || title.length() == 0) {
				title = mContext.getResources().getString(R.string.zuimeia_sdk_download_download_unknown_title);
			}
			Uri contentUri = ContentUris.withAppendedId(Downloads.getAllDownloadsContentURI(mContext), id);
			String caption;
			Intent intent;
			if (Downloads.isStatusError(download.mStatus)) {
				caption = mContext.getResources().getString(R.string.zuimeia_sdk_download_notification_download_failed);
				intent = new Intent(Constants.ACTION_LIST);
			} else {
				caption = mContext.getResources().getString(R.string.zuimeia_sdk_download_notification_download_complete);
				if (download.mDestination == Downloads.DESTINATION_EXTERNAL) {
					intent = new Intent(Constants.ACTION_OPEN);
				} else {
					intent = new Intent(Constants.ACTION_LIST);
				}
			}
			intent.setClassName(mContext.getPackageName(), DownloadReceiver.class.getName());
			intent.setData(contentUri);

			n.when = download.mLastMod;
			n.setLatestEventInfo(mContext, title, caption, PendingIntent.getBroadcast(mContext, 0, intent, 0));

			intent = new Intent(Constants.ACTION_HIDE);
			intent.setClassName(mContext.getPackageName(), DownloadReceiver.class.getName());
			intent.setData(contentUri);
			// n.deleteIntent = PendingIntent.getBroadcast(mContext, 0, intent,
			// 0); //删除notification，不做任何事情
			mSystemFacade.postNotification(download.mId, n);
		}
	}

	private boolean isActiveAndVisible(DownloadInfo download) {
		return 100 <= download.mStatus && download.mStatus < 200 && download.mVisibility != Downloads.VISIBILITY_HIDDEN;
	}

	private boolean isCompleteAndVisible(DownloadInfo download) {
		return download.mStatus >= 200 && download.mVisibility == Downloads.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
	}

	/*
	 * Helper function to build the downloading text.
	 */
	private String getDownloadingText(long totalBytes, long currentBytes) {
		if (totalBytes <= 0) {
			return "0%";
		}
		long progress = currentBytes * 100 / totalBytes;
		StringBuilder sb = new StringBuilder();
		sb.append(progress);
		sb.append('%');
		return sb.toString();
	}

}
