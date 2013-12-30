/*
 * Copyright 2012 Lars Werkman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vxt.abmulani.uielement;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;

class MinutesPicker extends View {
	private boolean visible=false;
	private static final String STATE_PARENT = "parent";
	private static final String STATE_ANGLE = "angle";

	private OnCircleSeekBarChangeListener mOnCircleSeekBarChangeListener;

	private Paint mPointerHaloPaint;

	private int mPointerRadius;

	private RectF mColorWheelRectangle = new RectF();

	private boolean mUserIsMovingPointer = false;

	private float mTranslationOffset;

	private float mColorWheelRadius;
	private float mAngle;
	private int textInt = 0;
	private int conversion = 0;
	private int max = 100;
	private SweepGradient s;
	private int pointer_halo_color, init_position;
	private boolean block_end = false;
	private float lastX;
	private int last_radians = 0;
	private boolean block_start = false;

	private int arc_finish_radians = 360;
	private int start_arc = 270;

	private float[] pointerPosition;
	private RectF mColorCenterHaloRectangle = new RectF();
	private int end_wheel;

	private Paint mWheelColor;

	public MinutesPicker(Context context,int radialWidth,CustomValues customValues) {
		super(context);
		mPointerRadius = (int) (radialWidth*0.06);
		max =60;
		init_position = customValues.getStartMinuteValue();
		start_arc = 0;
		end_wheel = 360;
		last_radians = end_wheel;
		if (init_position < start_arc)
			init_position = calculateTextFromStartAngle(start_arc);
		
		pointer_halo_color = customValues.getMinutesPointerColor();
		init(customValues);
		updateLayoutSize(radialWidth, radialWidth);
		
	}


	private void init(CustomValues customValues) {
		mWheelColor = new Paint(Paint.ANTI_ALIAS_FLAG);
		mWheelColor.setShader(s);
		mWheelColor.setColor(customValues.getMinutesInnerCircle());
		mWheelColor.setStyle(Paint.Style.FILL_AND_STROKE);
		
		mPointerHaloPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPointerHaloPaint.setColor(pointer_halo_color);
		mPointerHaloPaint.setStrokeWidth(mPointerRadius);
		mPointerHaloPaint.setAlpha(150);

		arc_finish_radians = (int) calculateAngleFromText(init_position) - 90;

		if (arc_finish_radians > end_wheel)
			arc_finish_radians = end_wheel;
		mAngle = calculateAngleFromRadians(arc_finish_radians > end_wheel ? end_wheel
				: arc_finish_radians);
		textInt = (calculateTextFromAngle(arc_finish_radians));

		invalidate();
	}


	@Override
	protected void onDraw(Canvas canvas) {

		canvas.translate(mTranslationOffset, mTranslationOffset);
		canvas.drawCircle(mColorWheelRectangle.centerX(), mColorWheelRectangle.centerX(),(float)(mColorWheelRadius*1.15), mWheelColor);

		canvas.drawCircle(pointerPosition[0], pointerPosition[1],
				(float)(mPointerRadius), mPointerHaloPaint);

	}

	private void updateLayoutSize(int width,int height){
		int min = Math.min(width, height);
		setMeasuredDimension(min, min);

		mTranslationOffset = min * 0.5f;
		mColorWheelRadius = (float) ((mTranslationOffset - mPointerRadius)*0.95);

		mColorWheelRectangle.set(-mColorWheelRadius, -mColorWheelRadius,
				mColorWheelRadius, mColorWheelRadius);

		mColorCenterHaloRectangle.set(-mColorWheelRadius / 2,
				-mColorWheelRadius / 2, mColorWheelRadius / 2,
				mColorWheelRadius / 2);

		pointerPosition = calculatePointerPosition(mAngle);
	}
	
	private int calculateTextFromAngle(float angle) {
		float m = angle - start_arc;

		float f = (float) ((end_wheel - start_arc) / m);

		return (int) (max / f);
	}

	private int calculateTextFromStartAngle(float angle) {
		float m = angle;

		float f = (float) ((end_wheel - start_arc) / m);

		return (int) (max / f);
	}

	private double calculateAngleFromText(int position) {
		if (position == 0 || position >= max)
			return (float) 90;

		double f = (double) max / (double) position;

		double f_r = 360 / f;

		double ang = f_r + 90;

		return ang;

	}

	private int calculateRadiansFromAngle(float angle) {
		float unit = (float) (angle / (2 * Math.PI));
		if (unit < 0) {
			unit += 1;
		}
		int radians = (int) ((unit * 360) - ((360 / 4) * 3));
		if (radians < 0)
			radians += 360;
		return radians;
	}

	private float calculateAngleFromRadians(int radians) {
		return (float) (((radians + 270) * (2 * Math.PI)) / 360);
	}
	public int getValue() {
		return conversion;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(!isVisible()){
			return false;
		}
		float x = event.getX() - mTranslationOffset;
		float y = event.getY() - mTranslationOffset;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mAngle = (float) java.lang.Math.atan2(y, x);

			block_end = false;
			block_start = false;
			mUserIsMovingPointer = true;

			arc_finish_radians = calculateRadiansFromAngle(mAngle);

			if (arc_finish_radians > end_wheel) {
				arc_finish_radians = end_wheel;
				block_end = true;
			}

			if (!block_end && !block_start) {
				textInt = calculateTextFromAngle(arc_finish_radians);
				pointerPosition = calculatePointerPosition(mAngle);
				invalidate();
				if (mOnCircleSeekBarChangeListener != null)
					mOnCircleSeekBarChangeListener.onProgressChanged(this,
							textInt, false);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (mUserIsMovingPointer) {
				mAngle = (float) java.lang.Math.atan2(y, x);

				int radians = calculateRadiansFromAngle(mAngle);

				if (last_radians > radians && radians < (360 / 6) && x > lastX
						&& last_radians > (360 / 6)) {

					if (!block_end && !block_start)
						block_end = true;
				} else if (last_radians >= start_arc
						&& last_radians <= (360 / 4) && radians <= (360 - 1)
						&& radians >= ((360 / 4) * 3) && x < lastX) {
					if (!block_start && !block_end)
						block_start = true;

				} else if (radians >= end_wheel && !block_start
						&& last_radians < radians) {
					block_end = true;
				} else if (radians < end_wheel && block_end
						&& last_radians > end_wheel) {
					block_end = false;
				} else if (radians < start_arc && last_radians > radians
						&& !block_end) {
					block_start = true;
				} else if (block_start && last_radians < radians
						&& radians > start_arc && radians < end_wheel) {
					block_start = false;
				}

				if (block_end) {
					block_end = false;
					arc_finish_radians = start_arc + 1;
					textInt = calculateTextFromAngle(arc_finish_radians);
					mAngle = calculateAngleFromRadians(arc_finish_radians);
					pointerPosition = calculatePointerPosition(mAngle);
				} else if (block_start) {
					block_start = false;
					arc_finish_radians = end_wheel - 1;
					textInt = calculateTextFromAngle(arc_finish_radians);
					mAngle = calculateAngleFromRadians(arc_finish_radians);
					pointerPosition = calculatePointerPosition(mAngle);
				} else {
					arc_finish_radians = calculateRadiansFromAngle(mAngle);
					textInt = calculateTextFromAngle(arc_finish_radians);
					pointerPosition = calculatePointerPosition(mAngle);
				}
				invalidate();
				if (mOnCircleSeekBarChangeListener != null)
					mOnCircleSeekBarChangeListener.onProgressChanged(this,
							textInt, false);

				last_radians = radians;

			}
			break;
		case MotionEvent.ACTION_UP:
			mUserIsMovingPointer = false;
			break;
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE && getParent() != null) {
			getParent().requestDisallowInterceptTouchEvent(true);
		}
		lastX = x;

		return true;
	}

	private float[] calculatePointerPosition(float angle) {
		float x = (float) ((mColorWheelRadius*0.95) * Math.cos(angle));
		float y = (float) ((mColorWheelRadius *0.95) * Math.sin(angle));

		return new float[] { x, y };
	}

	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();

		Bundle state = new Bundle();
		state.putParcelable(STATE_PARENT, superState);
		state.putFloat(STATE_ANGLE, mAngle);

		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle savedState = (Bundle) state;

		Parcelable superState = savedState.getParcelable(STATE_PARENT);
		super.onRestoreInstanceState(superState);

		mAngle = savedState.getFloat(STATE_ANGLE);
		arc_finish_radians = calculateRadiansFromAngle(mAngle);
		textInt = calculateTextFromAngle(arc_finish_radians);
		pointerPosition = calculatePointerPosition(mAngle);
	}

	public void setOnSeekBarChangeListener(OnCircleSeekBarChangeListener l) {
		mOnCircleSeekBarChangeListener = l;
	}


	public boolean isVisible() {
		return visible;
	}


	public void setVisible(boolean visible) {
		this.visible = visible;
	}

}
