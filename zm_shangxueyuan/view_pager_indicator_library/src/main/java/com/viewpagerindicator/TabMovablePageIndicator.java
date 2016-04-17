/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
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
package com.viewpagerindicator;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
@SuppressLint("NewApi")
public class TabMovablePageIndicator extends HorizontalScrollView implements PageIndicator {
	/** Title text used when no title is provided by the adapter. */
	private static final CharSequence EMPTY_TITLE = "";

	/**
	 * Interface for a callback when the selected tab has been reselected.
	 */
	public interface OnTabReselectedListener {
		/**
		 * Callback when the selected tab has been reselected.
		 *
		 * @param position Position of the current center item.
		 */
		void onTabReselected(int position);
	}

	private Runnable mTabSelector;

	private final OnClickListener mTabClickListener = new OnClickListener() {
		public void onClick(View view) {
			TabView tabView = (TabView) view;
			final int oldSelected = mViewPager.getCurrentItem();
			final int newSelected = tabView.getIndex();
			mViewPager.setCurrentItem(newSelected);
			if (oldSelected == newSelected && mTabReselectedListener != null) {
				mTabReselectedListener.onTabReselected(newSelected);
			}
		}
	};

	private final IcsLinearLayout mTabLayout;
	private final LinearLayout mMainBox;
	private final ImageView mIndicator;

	private ViewPager mViewPager;
	private OnPageChangeListener mListener;

	private int mMaxTabWidth, mScreenWidth, mPaddingEachEdge, mExtendWidth;
	private int mSelectedTabIndex;

	private OnTabReselectedListener mTabReselectedListener;
	private boolean isTabAllDisplay;

	public TabMovablePageIndicator(Context context) {
		this(context, null);
		mScreenWidth = getScreenDisplayMetrics().widthPixels;
	}

	public TabMovablePageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScreenWidth = getScreenDisplayMetrics().widthPixels;
		setHorizontalScrollBarEnabled(false);

		mMainBox = new LinearLayout(context);
		mMainBox.setOrientation(LinearLayout.VERTICAL);
		mMainBox.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));

		mTabLayout = new IcsLinearLayout(context, R.attr.vpiTabPageIndicatorStyle);
		mMainBox.addView(mTabLayout, new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT, 1));

		mIndicator = new ImageView(context);
		LinearLayout.LayoutParams indicatorLp = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
		mMainBox.addView(mIndicator, indicatorLp);

		addView(mMainBox, new ViewGroup.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
	}

	public void setIndicatorColor(int color) {
		mIndicator.setBackgroundColor(color);
	}

	public void setIndicatorLayoutParams(int height, int extendWidth) {
		mExtendWidth = extendWidth;
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(WRAP_CONTENT, height);
		mIndicator.setLayoutParams(lp);
		mIndicator.setVisibility(View.INVISIBLE);
		requestLayout();
	}

	public void setTabAllDisplay(boolean isTabAllDisplay) {
		this.isTabAllDisplay = isTabAllDisplay;
	}

	public void setOnTabReselectedListener(OnTabReselectedListener listener) {
		mTabReselectedListener = listener;
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
		setFillViewport(lockedExpanded);

		final int childCount = mTabLayout.getChildCount();
		if (childCount > 1 && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
			if (childCount > 2) {
				mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
			} else {
				// exactly to tabs
				mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
			}
		} else {
			mMaxTabWidth = -1;
		}

		final int oldWidth = getMeasuredWidth();
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int newWidth = getMeasuredWidth();

		if (lockedExpanded && oldWidth != newWidth) {
			// Recenter the tab display if we're at a new (scrollable) size.
			setCurrentItem(mSelectedTabIndex);
		}
	}

	private void animateToTab(final int position) {
		final TabView tabView = (TabView) mTabLayout.getChildAt(position);
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
		mTabSelector = new Runnable() {
			public void run() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					animateScrollWithIndicator(tabView);
				} else {
					final int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;
					smoothScrollTo(scrollPos, 0);
					moveIndicator(tabView);
				}

				mTabSelector = null;
			}
		};
		post(mTabSelector);
	}

	private void animateScrollWithIndicator(TabView tabView) {
		int scrollPos = tabView.getLeft() - (getWidth() - tabView.getWidth()) / 2;

		AnimatorSet as = new AnimatorSet();
		as.setDuration(300);

		ArrayList<Animator> animations = new ArrayList<Animator>();

		// scroll
		ObjectAnimator animScrollX = ObjectAnimator.ofInt(TabMovablePageIndicator.this, "scrollX", scrollPos);
		animScrollX.setInterpolator(new AccelerateDecelerateInterpolator());
		animations.add(animScrollX);

		// indicator position
		int left = tabView.getLeft();
//		int width = tabView.getWidth();
		int textWidth = tabView.wordWidth;
//		int x = left + (width - textWidth) / 2 - mExtendWidth * 2;
//		x = x < 0 ? 0 : x;

		ObjectAnimator translateXAnim = ObjectAnimator.ofFloat(mIndicator, "translationX", left);
		translateXAnim.setInterpolator(new OvershootInterpolator(0.8f));
		animations.add(translateXAnim);

		// indicator width
		ValueAnimator animWidth = ValueAnimator.ofInt(mIndicator.getLayoutParams().width, textWidth + mExtendWidth * 2);
		animWidth.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIndicator.getLayoutParams();
				lp.width = (Integer) animation.getAnimatedValue();

				mIndicator.setLayoutParams(lp);
				requestLayout();
			}
		});
		animations.add(animWidth);

		as.playTogether(animations);
		as.start();
	}

	private void moveIndicator(TextView tabView) {
		int left = tabView.getLeft();
		int width = tabView.getWidth();
		int textWidth = getTextWidth(tabView.getText().toString(), tabView.getTextSize(), tabView.getTypeface());

		int x = left + (width - textWidth) / 2;

		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(textWidth, mIndicator.getLayoutParams().height);
		lp.leftMargin = x;
		mIndicator.setLayoutParams(lp);
	}

	public static int getTextWidth(String str, float fontSize, Typeface typeface) {
		if (str == null || str.equals("")) {
			return 0;
		}
		Paint pFont = new Paint();
		Rect rect = new Rect();
		pFont.setTextSize(fontSize);
		pFont.setTypeface(typeface);
		pFont.getTextBounds(str, 0, str.length(), rect);
		return rect.width();
	}

	protected DisplayMetrics getScreenDisplayMetrics() {
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm;
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (mTabSelector != null) {
			// Re-post the selector we saved
			post(mTabSelector);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mTabSelector != null) {
			removeCallbacks(mTabSelector);
		}
	}

	private TabView addTab(int index, CharSequence text, int iconResId) {
		final TabView tabView = new TabView(getContext());
		tabView.mIndex = index;
		tabView.setFocusable(true);
		tabView.setOnClickListener(mTabClickListener);
		tabView.setText(text);

		if (iconResId != 0) {
			tabView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0);
		}

		mTabLayout.addView(tabView, new LinearLayout.LayoutParams(0, MATCH_PARENT, 1));
		int width = getTextWidth(text.toString(), tabView.getTextSize(), tabView.getTypeface());
		tabView.wordWidth = width;

		return tabView;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if (mListener != null) {
			mListener.onPageScrollStateChanged(arg0);
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (mListener != null) {
			mListener.onPageScrolled(arg0, arg1, arg2);
		}
	}

	@Override
	public void onPageSelected(int arg0) {
		setCurrentItem(arg0);
		if (mListener != null) {
			mListener.onPageSelected(arg0);
		}
	}

	@Override
	public void setViewPager(ViewPager view) {
		if (mViewPager == view) {
			return;
		}
		if (mViewPager != null) {
			mViewPager.setOnPageChangeListener(null);
		}
		final PagerAdapter adapter = view.getAdapter();
		if (adapter == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}
		mViewPager = view;
		view.setOnPageChangeListener(this);
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		mTabLayout.removeAllViews();
		PagerAdapter adapter = mViewPager.getAdapter();
		IconPagerAdapter iconAdapter = null;
		if (adapter instanceof IconPagerAdapter) {
			iconAdapter = (IconPagerAdapter) adapter;
		}
		final int count = adapter.getCount();

		int wordsTotalWidth = 0;
		for (int i = 0; i < count; i++) {
			CharSequence title = adapter.getPageTitle(i);
			if (title == null) {
				title = EMPTY_TITLE;
			}
			int iconResId = 0;
			if (iconAdapter != null) {
				iconResId = iconAdapter.getIconResId(i);
			}
			TabView tabView = addTab(i, title, iconResId);
			wordsTotalWidth += tabView.wordWidth;
		}

		if (isTabAllDisplay) {
			int totalSpace = mScreenWidth - wordsTotalWidth;
			if (totalSpace < 0) {
				mPaddingEachEdge = 0;
			} else {
				mPaddingEachEdge = totalSpace / count / 2;
			}
		}

		if (mSelectedTabIndex > count) {
			mSelectedTabIndex = count - 1;
		}
		setCurrentItem(mSelectedTabIndex);
		requestLayout();
	}

	@Override
	public void setViewPager(ViewPager view, int initialPosition) {
		setViewPager(view);
		setCurrentItem(initialPosition);
	}

	@Override
	public void setCurrentItem(int item) {
		if (mViewPager == null) {
			throw new IllegalStateException("ViewPager has not been bound.");
		}
		mSelectedTabIndex = item;
		mViewPager.setCurrentItem(item);

		final int tabCount = mTabLayout.getChildCount();
		for (int i = 0; i < tabCount; i++) {
			final View child = mTabLayout.getChildAt(i);
			final boolean isSelected = (i == item);
			child.setSelected(isSelected);
			if (isSelected) {
				animateToTab(item);
			}
		}

	}

	@Override
	public void setOnPageChangeListener(OnPageChangeListener listener) {
		mListener = listener;
	}

	private class TabView extends TextView {
		private int mIndex;
		private int wordWidth;

		public TabView(Context context) {
			super(context, null, R.attr.vpiTabPageIndicatorStyle);
		}

		@Override
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);

			// Re-measure if we went beyond our maximum size.
			if (!isTabAllDisplay) {
				if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
					super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
				}
			} else {
				super.onMeasure(MeasureSpec.makeMeasureSpec(wordWidth + mPaddingEachEdge * 2, MeasureSpec.EXACTLY), heightMeasureSpec);
				if (mIndicator.getVisibility() == View.INVISIBLE && mIndex == 0) {
					LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mIndicator.getLayoutParams();
					lp.width = wordWidth + mExtendWidth * 2;
					int x = mPaddingEachEdge - mExtendWidth;
					lp.leftMargin = x;
					mIndicator.setLayoutParams(lp);
					mIndicator.setVisibility(View.VISIBLE);
					TabMovablePageIndicator.this.requestLayout();
				}
			}
		}

		public int getIndex() {
			return mIndex;
		}

	}
}
