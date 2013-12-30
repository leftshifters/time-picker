package vxt.abmulani.uielement;

/*
 * Copyright (C) 2013 The Android Open Source Project
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

import vxt.abmulani.customtimepicker.R;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;

class RadialTextsView extends View {
	private final static String TAG = "RadialTextsView";

	private final Paint mPaint = new Paint();

	private boolean mDrawValuesReady;
	private boolean mIsInitialized;

	private Typeface mTypefaceLight;
	private Typeface mTypefaceRegular;
	private String[] mTexts;
	private String[] mInnerTexts;
	private boolean mHasInnerCircle;
	private float mCircleRadiusMultiplier;
	private float mNumbersRadiusMultiplier;
	private float mInnerNumbersRadiusMultiplier;
	private float mTextSizeMultiplier;
	private float mInnerTextSizeMultiplier;
	private int mXCenter;
	private int mYCenter;
	private float mCircleRadius;
	private boolean mTextGridValuesDirty;
	private float mTextSize;
	private float mInnerTextSize;
	private float[] mTextGridHeights;
	private float[] mTextGridWidths;
	private float[] mInnerTextGridHeights;
	private float[] mInnerTextGridWidths;

	private float mAnimationRadiusMultiplier;
	private float mTransitionMidRadiusMultiplier;
	private float mTransitionEndRadiusMultiplier;
	ObjectAnimator mDisappearAnimator;
	ObjectAnimator mReappearAnimator;
	private InvalidateUpdateListener mInvalidateUpdateListener;

	public RadialTextsView(Context context) {
		super(context);
		mIsInitialized = false;
	}

	public void initialize(Resources res, String[] texts, String[] innerTexts,
			boolean is24HourMode, boolean disappearsOut,int themeColor) {
		if (mIsInitialized) {
			Log.e(TAG, "This RadialTextsView may only be initialized once.");
			return;
		}

		int numbersTextColor =themeColor;
		mPaint.setColor(numbersTextColor);
		String typefaceFamily = res.getString(R.string.radial_numbers_typeface);
		mTypefaceLight = Typeface.create(typefaceFamily, Typeface.NORMAL);
		String typefaceFamilyRegular = res.getString(R.string.sans_serif);
		mTypefaceRegular = Typeface.create(typefaceFamilyRegular,
				Typeface.NORMAL);
		mPaint.setAntiAlias(true);
		mPaint.setTextAlign(Align.CENTER);

		mTexts = texts;
		mInnerTexts = innerTexts;
		mHasInnerCircle = (innerTexts != null);

			mCircleRadiusMultiplier = Float.parseFloat(res
					.getString(R.string.circle_radius_multiplier_24HourMode));
		mTextGridHeights = new float[7];
		mTextGridWidths = new float[7];
		if (mHasInnerCircle) {
			mNumbersRadiusMultiplier = Float.parseFloat(res
					.getString(R.string.numbers_radius_multiplier_outer));
			mTextSizeMultiplier = Float.parseFloat(res
					.getString(R.string.text_size_multiplier_outer));
			mInnerNumbersRadiusMultiplier = Float.parseFloat(res
					.getString(R.string.numbers_radius_multiplier_inner));
			mInnerTextSizeMultiplier = Float.parseFloat(res
					.getString(R.string.text_size_multiplier_inner));

			mInnerTextGridHeights = new float[7];
			mInnerTextGridWidths = new float[7];
		} else {
			mNumbersRadiusMultiplier = Float.parseFloat(res
					.getString(R.string.numbers_radius_multiplier_normal));
			mTextSizeMultiplier = Float.parseFloat(res
					.getString(R.string.text_size_multiplier_normal));
		}

		mAnimationRadiusMultiplier = 1;
		mTransitionMidRadiusMultiplier = 1f + (0.05f * (disappearsOut ? -1 : 1));
		mTransitionEndRadiusMultiplier = 1f + (0.3f * (disappearsOut ? 1 : -1));
		mInvalidateUpdateListener = new InvalidateUpdateListener();

		mTextGridValuesDirty = true;
		mIsInitialized = true;
	}

	
	@Override
	public boolean hasOverlappingRendering() {
		return false;
	}

	
	public void setAnimationRadiusMultiplier(float animationRadiusMultiplier) {
		mAnimationRadiusMultiplier = animationRadiusMultiplier;
		mTextGridValuesDirty = true;
	}

	@Override
	public void onDraw(Canvas canvas) {
		int viewWidth = getWidth();
		if (viewWidth == 0 || !mIsInitialized) {
			return;
		}

		if (!mDrawValuesReady) {
			mXCenter = getWidth() / 2;
			mYCenter = getHeight() / 2;
			mCircleRadius = Math.min(mXCenter, mYCenter)
					* mCircleRadiusMultiplier;

			mTextSize = mCircleRadius * mTextSizeMultiplier;
			if (mHasInnerCircle) {
				mInnerTextSize = mCircleRadius * mInnerTextSizeMultiplier;
			}

			renderAnimations();

			mTextGridValuesDirty = true;
			mDrawValuesReady = true;
		}
		if (mTextGridValuesDirty) {
			float numbersRadius = mCircleRadius * mNumbersRadiusMultiplier
					* mAnimationRadiusMultiplier;
			calculateGridSizes(numbersRadius, mXCenter, mYCenter, mTextSize,
					mTextGridHeights, mTextGridWidths);
			if (mHasInnerCircle) {
				float innerNumbersRadius = mCircleRadius
						* mInnerNumbersRadiusMultiplier
						* mAnimationRadiusMultiplier;
				calculateGridSizes(innerNumbersRadius, mXCenter, mYCenter,
						mInnerTextSize, mInnerTextGridHeights,
						mInnerTextGridWidths);
			}
			mTextGridValuesDirty = false;
		}

		drawTexts(canvas, mTextSize, mTypefaceLight, mTexts, mTextGridWidths,
				mTextGridHeights);
		if (mHasInnerCircle) {
			drawTexts(canvas, mInnerTextSize, mTypefaceRegular, mInnerTexts,
					mInnerTextGridWidths, mInnerTextGridHeights);
		}
	}

	
	private void calculateGridSizes(float numbersRadius, float xCenter,
			float yCenter, float textSize, float[] textGridHeights,
			float[] textGridWidths) {
		
		float offset1 = numbersRadius;
		float offset2 = numbersRadius * ((float) Math.sqrt(3)) / 2f;
		float offset3 = numbersRadius / 2f;
		mPaint.setTextSize(textSize);
		yCenter -= (mPaint.descent() + mPaint.ascent()) / 2;

		textGridHeights[0] = yCenter - offset1;
		textGridWidths[0] = xCenter - offset1;
		textGridHeights[1] = yCenter - offset2;
		textGridWidths[1] = xCenter - offset2;
		textGridHeights[2] = yCenter - offset3;
		textGridWidths[2] = xCenter - offset3;
		textGridHeights[3] = yCenter;
		textGridWidths[3] = xCenter;
		textGridHeights[4] = yCenter + offset3;
		textGridWidths[4] = xCenter + offset3;
		textGridHeights[5] = yCenter + offset2;
		textGridWidths[5] = xCenter + offset2;
		textGridHeights[6] = yCenter + offset1;
		textGridWidths[6] = xCenter + offset1;
	}


	private void drawTexts(Canvas canvas, float textSize, Typeface typeface,
			String[] texts, float[] textGridWidths, float[] textGridHeights) {
		mPaint.setTextSize(textSize);
		mPaint.setTypeface(typeface);
		canvas.drawText(texts[0], textGridWidths[3], textGridHeights[0], mPaint);
		canvas.drawText(texts[1], textGridWidths[4], textGridHeights[1], mPaint);
		canvas.drawText(texts[2], textGridWidths[5], textGridHeights[2], mPaint);
		canvas.drawText(texts[3], textGridWidths[6], textGridHeights[3], mPaint);
		canvas.drawText(texts[4], textGridWidths[5], textGridHeights[4], mPaint);
		canvas.drawText(texts[5], textGridWidths[4], textGridHeights[5], mPaint);
		canvas.drawText(texts[6], textGridWidths[3], textGridHeights[6], mPaint);
		canvas.drawText(texts[7], textGridWidths[2], textGridHeights[5], mPaint);
		canvas.drawText(texts[8], textGridWidths[1], textGridHeights[4], mPaint);
		canvas.drawText(texts[9], textGridWidths[0], textGridHeights[3], mPaint);
		canvas.drawText(texts[10], textGridWidths[1], textGridHeights[2],
				mPaint);
		canvas.drawText(texts[11], textGridWidths[2], textGridHeights[1],
				mPaint);
	}

	
	private void renderAnimations() {
		Keyframe kf0, kf1, kf2, kf3;
		float midwayPoint = 0.2f;
		int duration = 500;

		// Set up animator for disappearing.
		kf0 = Keyframe.ofFloat(0f, 1);
		kf1 = Keyframe.ofFloat(midwayPoint, mTransitionMidRadiusMultiplier);
		kf2 = Keyframe.ofFloat(1f, mTransitionEndRadiusMultiplier);
		PropertyValuesHolder radiusDisappear = PropertyValuesHolder.ofKeyframe(
				"animationRadiusMultiplier", kf0, kf1, kf2);

		kf0 = Keyframe.ofFloat(0f, 1f);
		kf1 = Keyframe.ofFloat(1f, 0f);
		PropertyValuesHolder fadeOut = PropertyValuesHolder.ofKeyframe("alpha",
				kf0, kf1);

		mDisappearAnimator = ObjectAnimator.ofPropertyValuesHolder(this,
				radiusDisappear, fadeOut).setDuration(duration);
		mDisappearAnimator.addUpdateListener(mInvalidateUpdateListener);
		float delayMultiplier = 0.25f;
		float transitionDurationMultiplier = 1f;
		float totalDurationMultiplier = transitionDurationMultiplier
				+ delayMultiplier;
		int totalDuration = (int) (duration * totalDurationMultiplier);
		float delayPoint = (delayMultiplier * duration) / totalDuration;
		midwayPoint = 1 - (midwayPoint * (1 - delayPoint));

		kf0 = Keyframe.ofFloat(0f, mTransitionEndRadiusMultiplier);
		kf1 = Keyframe.ofFloat(delayPoint, mTransitionEndRadiusMultiplier);
		kf2 = Keyframe.ofFloat(midwayPoint, mTransitionMidRadiusMultiplier);
		kf3 = Keyframe.ofFloat(1f, 1);
		PropertyValuesHolder radiusReappear = PropertyValuesHolder.ofKeyframe(
				"animationRadiusMultiplier", kf0, kf1, kf2, kf3);

		kf0 = Keyframe.ofFloat(0f, 0f);
		kf1 = Keyframe.ofFloat(delayPoint, 0f);
		kf2 = Keyframe.ofFloat(1f, 1f);
		PropertyValuesHolder fadeIn = PropertyValuesHolder.ofKeyframe("alpha",
				kf0, kf1, kf2);

		mReappearAnimator = ObjectAnimator.ofPropertyValuesHolder(this,
				radiusReappear, fadeIn).setDuration(totalDuration);
		mReappearAnimator.addUpdateListener(mInvalidateUpdateListener);
	}

	public ObjectAnimator getDisappearAnimator() {
		if (!mIsInitialized || !mDrawValuesReady || mDisappearAnimator == null) {
			Log.e(TAG, "RadialTextView was not ready for animation.");
			return null;
		}

		return mDisappearAnimator;
	}

	public ObjectAnimator getReappearAnimator() {
		if (!mIsInitialized || !mDrawValuesReady || mReappearAnimator == null) {
			Log.e(TAG, "RadialTextView was not ready for animation.");
			return null;
		}

		return mReappearAnimator;
	}

	private class InvalidateUpdateListener implements
			ValueAnimator.AnimatorUpdateListener {
		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			RadialTextsView.this.invalidate();
		}
	}
}