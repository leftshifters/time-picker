package vxt.abmulani.uielement;

import vxt.abmulani.customtimepicker.R;
import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CustomTimePicker {
	private String TAG = "CustomTimePicker";
	private boolean autoSwitchToMinutesScreen = true;
	private Dialog _DIALOG;
	private OnTimePickerChangeListener listener = null;
	private Context mContext;
	private int radialWidth;
	private Vibrator mVibrator;
	private boolean mVibrate = true;
	private long mLastVibrate = 0;
	private RadialTextsView radialHoursTexts, radialMinutesText;
	private FrameLayout hoursLayout, minutesLayout;
	private int _HOURS = 12, _MINUTES = 0;
	private boolean _AM_PM = true;
	private TextView hoursTextView, minutesTextView, ampmTextView;
	private Animation enterAnim, exitAnim;
	private HoursPicker hourPicker;
	private MinutesPicker minutesPicker;
	private Button amButton, pmButton, submitButton;
	private int screenDensityHeight, screenDensityWidth;
	private Activity mActivity;
	private RelativeLayout mainLayout;
	private CustomValues customValues;
	/** @param Activity
	 * <p>
	 * Current Activiy should be passed to get a object of the CustomTimePicker
	 * <p>
	 * @see setOnTimePickerChangeListener*/
	public CustomTimePicker(Activity activity) {
		this(activity, 12, 00, true, null);
	}

	/** @param Activity
	 * @param OnTimePickerChangeListener
	 * <p>
	 * Current Activiy should be passed to get a object of the CustomTimePicker.
	 * <p>
	 * OnTimePickerChangeListener will provide override methods to obtain updated values of the timepicker dialog
	 * <p>
	 * @see setInitialTime
	 * */
	public CustomTimePicker(Activity activity,
			OnTimePickerChangeListener listener) {
		this(activity, 12, 00, true, listener);
	}

	/** @param Activity
	 * @param hours
	 * @param minutes
	 * @param isAm
	 * @param OnTimePickerChangeListener
	 * <p>
	 * Current Activity should be passed to get a object of the CustomTimePicker.
	 * <p>
	 * OnTimePickerChangeListener will provide override methods to obtain updated values of the timepicker dialog
	 * <p>
	 * The time picker dialog will be initialized with the specified <b>hours,minutes and isAm</b> values
	 * */
	
	public CustomTimePicker(Activity activity, int hours, int minutes,
			boolean isAm, OnTimePickerChangeListener listener) {
		setOnTimePickerChangeListener(listener);
		this.mActivity = activity;
		this.mContext = activity;
		customValues=new CustomValues();
		setInitialTime(hours, minutes, isAm);
	}

	private void InitializeDialogView() {
		_DIALOG = new Dialog(mContext);
		_DIALOG.setContentView(R.layout.dialog_layout);
		_DIALOG.getWindow().addFlags(Window.FEATURE_NO_TITLE);
		_DIALOG.getWindow().setBackgroundDrawable(new ColorDrawable(0));

		mVibrator = (Vibrator) mContext
				.getSystemService(Service.VIBRATOR_SERVICE);
		screenDensityWidth = getWindowWidth();
		screenDensityHeight = getWindowHeight();
		radialWidth = Math.min(screenDensityHeight, screenDensityWidth);
		radialWidth = (int) (screenDensityWidth * 0.6);

		hoursTextView = (TextView) _DIALOG.findViewById(R.id.date_hours);
		minutesTextView = (TextView) _DIALOG.findViewById(R.id.date_minutes);
		ampmTextView = (TextView) _DIALOG.findViewById(R.id.date_ap_pm);
		ampmTextView.setTextColor(customValues.getDefaultColor());
		hoursTextView.setOnClickListener(onHourClicklistener);
		minutesTextView.setOnClickListener(onMinutesClicklistener);
		ampmTextView.setOnClickListener(onSmapAmPmClicklistener);

		enterAnim = AnimationUtils.loadAnimation(mContext, R.anim.bottom_up);
		exitAnim = AnimationUtils.loadAnimation(mContext, R.anim.bottom_down);

		InitHoursLayout();
		InitMinutesLayout();

		mainLayout = (RelativeLayout) _DIALOG.findViewById(R.id.mian_layout);
		mainLayout.setBackgroundColor(customValues.getTHemeColor());
		amButton = (Button) _DIALOG.findViewById(R.id.button_am);
		android.widget.RelativeLayout.LayoutParams param = (android.widget.RelativeLayout.LayoutParams) amButton
				.getLayoutParams();
		param.height = (int) (screenDensityWidth * 0.125);
		param.width = (int) (screenDensityWidth * 0.125);
		amButton.setLayoutParams(param);
		pmButton = (Button) _DIALOG.findViewById(R.id.button_pm);
		param = (android.widget.RelativeLayout.LayoutParams) pmButton
				.getLayoutParams();
		param.height = (int) (screenDensityWidth * 0.125);
		param.width = (int) (screenDensityWidth * 0.125);
		pmButton.setLayoutParams(param);

		amButton.setOnClickListener(onAmClicklistener);
		pmButton.setOnClickListener(onPmClicklistener);
		set_HOURS(_HOURS);
		set_MINUTES(_MINUTES);
		set_AM_PM(_AM_PM);
		submitButton = (Button) _DIALOG.findViewById(R.id.submit_button);
		submitButton.setOnClickListener(onsubmitClicklistener);
		switchToHoursLayout();

	}

	private OnClickListener onHourClicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switchToHoursLayout();
		}
	};

	private OnClickListener onMinutesClicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switchToMinutesLayout();
		}
	};

	private OnClickListener onSmapAmPmClicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			swapAmPm();
		}
	};

	private OnClickListener onAmClicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			set_AM_PM(true);
		}
	};

	private OnClickListener onPmClicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			set_AM_PM(false);
		}
	};

	private OnClickListener onsubmitClicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (listener != null)
				listener.onSubmitClicked(get_HOURS(), get_MINUTES(),
						get_AM_PM());
		}
	};

	private void switchToMinutesLayout() {
		tryVibrate(15);
		if (minutesLayout.getVisibility() == View.GONE) {
			minutesTextView.setTextColor(customValues.getMinutesPointerColor());

			minutesTextView.setSelected(true);
			hoursTextView.setSelected(false);
			minutesLayout.startAnimation(enterAnim);
			minutesLayout.setVisibility(View.VISIBLE);
			hoursTextView.setTextColor(customValues.getDefaultColor());
			hoursLayout.startAnimation(exitAnim);
			hoursLayout.setVisibility(View.GONE);
			minutesPicker.setVisible(true);
			hourPicker.setVisible(false);
		}
	}

	private void switchToHoursLayout() {
		mVibrator.vibrate(15);
		if (hoursLayout.getVisibility() == View.GONE) {
			minutesTextView.setTextColor(customValues.getDefaultColor());
			minutesTextView.setSelected(false);
			hoursTextView.setSelected(true);
			minutesLayout.startAnimation(exitAnim);
			minutesLayout.setVisibility(View.GONE);
			hoursTextView.setTextColor(customValues.getHoursPointerColor());
			hoursLayout.startAnimation(enterAnim);
			hoursLayout.setVisibility(View.VISIBLE);
			minutesPicker.setVisible(false);
			hourPicker.setVisible(true);
		}
	}

	private void InitMinutesLayout() {
		minutesLayout = (FrameLayout) _DIALOG.findViewById(R.id.minutes_layout);
		radialMinutesText = new RadialTextsView(mContext);
		radialMinutesText.setLayoutParams(new FrameLayout.LayoutParams(
				radialWidth, radialWidth, Gravity.CENTER));
		Resources res = mContext.getResources();
		int[] minutes = { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55 };
		String[] minutesTexts = new String[12];
		for (int i = 0; i < 12; i++) {
			minutesTexts[i] = String.format("%02d", minutes[i]);
		}
		radialMinutesText.initialize(res, minutesTexts, null, false, true,
				customValues.getMinutesWheelTextColor());
		radialMinutesText.invalidate();
		minutesPicker = new MinutesPicker(mContext, radialWidth,customValues);
		minutesPicker.setLayoutParams(new FrameLayout.LayoutParams(radialWidth,
				radialWidth, Gravity.CENTER));
		minutesPicker.setOnSeekBarChangeListener(oncircleListener);
		minutesPicker.setBackgroundColor(color.holo_blue_bright);
		minutesLayout.addView(minutesPicker);
		minutesLayout.addView(radialMinutesText);
	}

	private void InitHoursLayout() {
		hoursLayout = (FrameLayout) _DIALOG.findViewById(R.id.hours_layout);
		radialHoursTexts = new RadialTextsView(mContext);
		radialHoursTexts.setLayoutParams(new FrameLayout.LayoutParams(
				radialWidth, radialWidth, Gravity.CENTER));

		Resources res = mContext.getResources();
		int[] hours = { 12, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };
		String[] hoursTexts = new String[12];
		for (int i = 0; i < 12; i++) {
			hoursTexts[i] = String.format("%d", hours[i]);
		}
		radialHoursTexts.initialize(res, hoursTexts, null, false, true,
				customValues.getHoursWheelTextColor());
		radialHoursTexts.invalidate();
		hourPicker = new HoursPicker(mContext, radialWidth,customValues);
		hourPicker.setLayoutParams(new FrameLayout.LayoutParams(radialWidth,
				radialWidth, Gravity.CENTER));
		hourPicker.setOnSeekBarChangeListener(oncircleListener);
		hoursLayout.addView(hourPicker);
		hoursLayout.addView(radialHoursTexts);
	}

	private void tryVibrate() {
		if (mVibrate && mVibrator != null) {
			long now = SystemClock.uptimeMillis();
			if (now - mLastVibrate >= 125) {
				mVibrator.vibrate(5);
				mLastVibrate = now;
			}
		}
	}

	private void tryVibrate(int val) {
		if (mVibrate && mVibrator != null) {
			mVibrator.vibrate(val);
		}
	}

	private int getWindowWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		(mActivity).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	private int getWindowHeight() {
		DisplayMetrics metrics = new DisplayMetrics();
		(mActivity).getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}

	private OnCircleSeekBarChangeListener oncircleListener = new OnCircleSeekBarChangeListener() {
		@Override
		public void onProgressChanged(Object seekBar, int progress,
				boolean fromHour) {
			tryVibrate();
			if (fromHour) {
				set_HOURS(progress);
			} else {
				set_MINUTES(progress);
			}
			if (listener != null)
				listener.onProgressChanged(get_HOURS(), get_MINUTES(),
						get_AM_PM());
		}

		@Override
		public void onScrollRelease(HoursPicker seekBar, int progress,
				boolean fromUser) {
			if (autoSwitchToMinutesScreen)
				switchToMinutesLayout();
		}
	};

	private int get_HOURS() {
		return _HOURS;
	}

	private void set_HOURS(int _HOURS) {
		if (_HOURS <= 0) {
			_HOURS = 12;
		}
		if (_HOURS >= 13) {
			_HOURS = 1;
		}
		hoursTextView.setText(getPadding(_HOURS));
		this._HOURS = _HOURS;
	}

	private int get_MINUTES() {
		return _MINUTES;
	}

	private void set_MINUTES(int _MINUTES) {
		if (_MINUTES < 0) {
			_MINUTES = 0;
		}
		if (_MINUTES >= 60) {
			_MINUTES = 59;
		}
		minutesTextView.setText(getPadding(_MINUTES));
		this._MINUTES = _MINUTES;
	}

	private CharSequence getPadding(int _VALUE) {
		if (_VALUE < 10) {
			return "0" + _VALUE;
		}
		return _VALUE + "";
	}

	private boolean get_AM_PM() {
		return _AM_PM;
	}

	private void set_AM_PM(boolean _AM_PM) {
		tryVibrate(20);
		if (_AM_PM) {
			ampmTextView.setText("AM");
			amButton.setSelected(true);
			amButton.setTextColor(customValues.getDefaultColor());
			pmButton.setTextColor(customValues.getTHemeColor());
			pmButton.setSelected(false);
			this._AM_PM = true;
		} else {
			ampmTextView.setText("PM");
			amButton.setSelected(false);
			pmButton.setTextColor(customValues.getDefaultColor());
			amButton.setTextColor(customValues.getTHemeColor());
			pmButton.setSelected(true);
			this._AM_PM = false;
		}
		if (listener != null)
			listener.onProgressChanged(get_HOURS(), get_MINUTES(), get_AM_PM());
	}

	private void swapAmPm() {
		if (_AM_PM) {
			set_AM_PM(false);
		} else {
			set_AM_PM(true);
		}
	}

	/** 
	 * @param hours - specify the initial hour value [1-12]
	 * <p>
	 * @param minutes - specify the initial minute value [0-59]
	 * <p>
	 * @param isAm - specify if initial time is AM [boolean]*/
	public void setInitialTime(int hours, int minutes, boolean isAm) {
		if (hours > 0 && hours < 13) {
			customValues.setStartHoursValue(hours);
			this._HOURS = hours;
		} else {
			Log.e(TAG, "Invalid value for hours in setInitialTime()");
			Log.e(TAG, "Expected Value is 0-11");
		}
		if (minutes >= 0 && minutes < 60) {
			customValues.setStartMinuteValue(minutes);
			this._MINUTES = minutes;
		} else {
			Log.e(TAG, "Invalid value for minutes in setInitialTime()");
			Log.e(TAG, "Expected Value is 0-56");
		}
		this._AM_PM = isAm;
	}

	/**
	 * @param OnTimePickerChangeListener
	 *            <p>
	 *            Holds methods like <b>onProgressChanged</b> and
	 *            <b>onSubmitClicked</b>
	 * */
	public void setOnTimePickerChangeListener(
			OnTimePickerChangeListener listener) {
		if (listener != null) {
			this.listener = listener;
		}
	}

	/**
	 * @return TimePickerDialog
	 *         <p>
	 *         Initialize the TimePicker Dialog using the specified or default
	 *         Colors and Styles
	 *         <p>
	 *         Returns a Dialog which should be loaded in a dialog object.
	 *         Further use of this Object is as similar to a normal dialog
	 *         object
	 */
	public Dialog create() {
		InitializeDialogView();
		return _DIALOG;
	}

	/**
	 * @param DoVibrate
	 *            <p>
	 *            If <b>TRUE</b> the device vibrates every time user interacts
	 *            with the TimePicker
	 */
	public void setVibration(boolean doVibrate) {
		mVibrate = doVibrate;
	}

	/**
	 * @param WheelTextColor
	 *            <p>
	 *            Sets the color of the Text inside the Hours Picker
	 */
	public void setHoursWheelTextColor(int hoursWheelTextColor) {
		customValues.setHoursWheelTextColor(hoursWheelTextColor);
	}

	/**
	 * @param WheelTextColor
	 *            <p>
	 *            Sets the color of the Text inside the Minutes Picker
	 */
	public void setMinutesWheelTextColor(int minutesWheelTextColor) {
		customValues.setMinutesWheelTextColor(minutesWheelTextColor);
	}

	/**
	 * @param PointerColor
	 *            <p>
	 *            Sets the color of circular pointer of the Minutes Picker
	 */
	public void setMinutesPointerColor(int minutesPointerColor) {
		customValues.setMinutesPointerColor(minutesPointerColor);
	}

	/**
	 * @param PointerColor
	 *            <p>
	 *            Sets the color of circular pointer of the Hours Picker
	 */
	public void setHoursPointerColor(int hoursPointerColor) {
		customValues.setHoursPointerColor(hoursPointerColor);
	}

	/**
	 * @param InnerCircleColor
	 *            <p>
	 *            Sets the color of circular dial of the Minutes Picker
	 */
	public void setMinutesInnerCircleColor(int minutesInnerCircle) {
		customValues.setMinutesInnerCircle(minutesInnerCircle);
	}

	/**
	 * @param InnerCircleColor
	 *            <p>
	 *            Sets the color of circular dial of the Hours Picker
	 */
	public void setHoursInnerCircleColor(int hoursInnerCircle) {
		customValues.setHoursInnerCircle(hoursInnerCircle);
	}

	/**
	 * @param ThemeColor
	 *            <p>
	 *            The background of the Dialog is set as the ThemeColor
	 * */
	public void setBackgroundColor(int tHemeColor) {
		customValues.setTHemeColor(tHemeColor);
	}

	/**
	 * @param TextColor
	 *            <p>
	 *            The Color of the Text [Appearing on the Top of the dialog box]
	 *            Which holds the Selected Time Values
	 */
	public void setTextColor(int tExtColor) {
		customValues.setDefaultColor(tExtColor);
	}

	/**
	 * @param autoSwitchToMinutesScreen
	 *            <p>
	 *            If True the Minutes Screen will automatically appear after
	 *            hours value is selected
	 */
	public void setAutoSwitchToMinutesScreen(boolean autoSwitchToMinutesScreen) {
		this.autoSwitchToMinutesScreen = autoSwitchToMinutesScreen;
	}

	/**
	 * @param Drawable
	 *            - the background drawable for Am Pm buttons
	 *            <p>
	 *            Customize the background of the Am Pm button by providing your
	 *            own themed background.
	 *            <p>
	 *            Make sure your drawable selector holds different images for
	 *            <b> IsSelected</b> and <b>Default</b>
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void setAmPmBackgroungDrawable(Drawable background) {
		if (Build.VERSION.SDK_INT >= 16) {
			amButton.setBackground(background);
			pmButton.setBackground(background);
		} else {
			amButton.setBackgroundDrawable(background);
			pmButton.setBackgroundDrawable(background);
		}
	}

	/**
	 * @param Drawable
	 *            - For the Submit Button on the TimePicker
	 *            <p>
	 *            Dialog Customize the Button of the TimePicker Dialog by
	 *            providing the Drawable matching your application theme
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public void setSubmitButtonBackgroungDrawable(Drawable background) {
		if (Build.VERSION.SDK_INT >= 16) {
			submitButton.setBackground(background);
		} else {
			submitButton.setBackgroundDrawable(background);
		}
	}

}
