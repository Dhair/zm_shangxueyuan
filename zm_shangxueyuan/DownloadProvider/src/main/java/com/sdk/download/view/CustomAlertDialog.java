package com.sdk.download.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.download_sdk.download.provider.R;


/**
 * @author deng.shengjin
 * @version create_time:2014-11-20_下午3:18:06
 * @Description 自定义dialog
 */
public class CustomAlertDialog extends Dialog {
	private TextView titleText, detailText, negativeText, positiveText;

	public CustomAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
	}

	public CustomAlertDialog(Context context, int theme) {
		super(context, theme);
	}

	public CustomAlertDialog(Context context) {
		this(context, R.style.sdk_download_alert_dialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sdk_download_download_dialog_progress);
		titleText = (TextView) findViewById(R.id.download_dialog_title_text);
		detailText = (TextView) findViewById(R.id.download_dialog_detail_text);
		negativeText = (TextView) findViewById(R.id.download_dialog_negative_text);
		positiveText = (TextView) findViewById(R.id.download_dialog_positive_text);
	}

	public void showAlert(String titleTxt, String detailTxt, String negativeStr, String positiveStr, final OnMyAlertClickListener negativeListener,
			final OnMyAlertClickListener positiveListener) {
		super.show();
		if (titleText != null && !TextUtils.isEmpty(titleTxt)) {
			titleText.setText(titleTxt);
		}
		if (detailText != null && !TextUtils.isEmpty(detailTxt)) {
			detailText.setText(detailTxt);
		}
		if (negativeText != null && !TextUtils.isEmpty(negativeStr)) {
			negativeText.setText(negativeStr);
		}
		if (negativeText != null) {
			negativeText.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (negativeListener != null) {
						negativeListener.onClick(CustomAlertDialog.this);
					}
				}
			});
		}
		if (positiveText != null && !TextUtils.isEmpty(positiveStr)) {
			positiveText.setText(positiveStr);
		}
		if (positiveText != null) {
			positiveText.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (positiveListener != null) {
						positiveListener.onClick(CustomAlertDialog.this);
					}
				}
			});
		}
	}

	public static class AlertBuilder {
		private CustomAlertDialog myAlertDialog;
		private String titleTxt, detailTxt, negativeStr, positiveStr;
		private OnMyAlertClickListener negativeListener, positiveListener;
		private Context context;
		private OnCancelListener onCancelListener;
		private OnDismissListener onDismissListener;
		private OnKeyListener onKeyListener;

		public AlertBuilder(Context context) {
			super();
			this.context = context;
		}

		public AlertBuilder setTitle(String titleTxt) {
			this.titleTxt = titleTxt;
			return this;
		}

		public AlertBuilder setMessage(String detailTxt) {
			this.detailTxt = detailTxt;
			return this;
		}

		public AlertBuilder setNegativeButton(String text, final OnMyAlertClickListener listener) {
			this.negativeStr = text;
			this.negativeListener = listener;
			return this;
		}

		public AlertBuilder setPositiveButton(String text, final OnMyAlertClickListener listener) {
			this.positiveStr = text;
			this.positiveListener = listener;
			return this;
		}

		public AlertBuilder setOnCancelListener(OnCancelListener onCancelListener) {
			this.onCancelListener = onCancelListener;
			return this;
		}

		public AlertBuilder setOnDismissListener(OnDismissListener onDismissListener) {
			this.onDismissListener = onDismissListener;
			return this;
		}

		public AlertBuilder setOnKeyListener(OnKeyListener onKeyListener) {
			this.onKeyListener = onKeyListener;
			return this;
		}

		public AlertBuilder create() {
			if (myAlertDialog == null) {
				myAlertDialog = new CustomAlertDialog(context);
			}
			if (onCancelListener != null) {
				myAlertDialog.setOnCancelListener(onCancelListener);
			}
			if (onDismissListener != null) {
				myAlertDialog.setOnDismissListener(onDismissListener);
			}
			if (onKeyListener != null) {
				myAlertDialog.setOnKeyListener(onKeyListener);
			}
			return this;
		}

		public void show() {
			create();
			myAlertDialog.showAlert(titleTxt, detailTxt, negativeStr, positiveStr, negativeListener, positiveListener);
		}
	}

	public interface OnMyAlertClickListener {
		void onClick(Dialog dialog);
	}

}
