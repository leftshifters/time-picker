package com.example.democustomtimepicker;

import vxt.abmulani.uielement.CustomTimePicker;
import vxt.abmulani.uielement.OnTimePickerChangeListener;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	Dialog mydialog;
	TextView hoursTxt, min, am;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		hoursTxt = (TextView) findViewById(R.id.hours);
		min = (TextView) findViewById(R.id.minutes);
		am = (TextView) findViewById(R.id.ampm);
		
		findViewById(R.id.button_default).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DefaultTimePicker();
			}
		});

		findViewById(R.id.button_custom).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CustomizedTimePicker();
			}
		});
		
		
	}

	/**
	 * Will return and show a simple default time picker dialog
	 */
	public void DefaultTimePicker() {
		CustomTimePicker mypicker = new CustomTimePicker(this);
		mypicker.setOnTimePickerChangeListener(new OnTimePickerChangeListener() {

			@Override
			public void onProgressChanged(int hours, int minutes, boolean isAm) {
				am.setText(isAm ? "am" : "pm");
				min.setText(minutes + "");
				hoursTxt.setText(hours + "");
			}

			@Override
			public void onSubmitClicked(int hours, int minutes, boolean isAm) {
				mydialog.dismiss();
			}
		});

		// Gets the object of the required time picker dialog.
		mydialog = mypicker.create();
		mydialog.show();
	}

	/**
	 * Custom time picker; You can customize every single color of the time
	 * picker dialog
	 */
	public void CustomizedTimePicker() {
		CustomTimePicker mypicker = new CustomTimePicker(this);

		// set if the minutes picker should appear automatically after hours is
		// selected.
		// DEFAULT VALUE = TRUE
		mypicker.setAutoSwitchToMinutesScreen(false);

		// DEFAULT COLOR = Color.WHITE
		mypicker.setHoursInnerCircleColor(Color.parseColor("#E67451"));

		// DEFAULT COLOR= Color.RED
		mypicker.setHoursPointerColor(Color.LTGRAY);

		// DEFAULT COLOR= Color.BLACK
		mypicker.setHoursWheelTextColor(Color.YELLOW);

		// DEFAULT TIME = 12:00:AM
		mypicker.setInitialTime(10, 10, false);

		// DEFAULT COLOR = Color.WHITE
		mypicker.setMinutesInnerCircleColor(Color.parseColor("#15317E"));

		// DEFAULT COLOR= Color.GREEN
		mypicker.setMinutesPointerColor(Color.parseColor("#1569C7"));

		// DEFAULT COLOR= Color.BLACK
		mypicker.setMinutesWheelTextColor(Color.parseColor("#FFF8C6"));

		// DEFAUL COLOR= Color.LTGRAY;
		mypicker.setBackgroundColor(Color.parseColor("#483C32"));

		// DEFAULT VALUE= TRUE;
		mypicker.setVibration(false);

		// DEFAULT NONE
		/* mypicker.setSubmitButtonBackgroungDrawable(YOUR_DRAWABLE); */

		// The drawable selector should consist of two backgrounds
		// [i.e.IsSelected and Default]
		/* mypicker.setAmPmBackgroungDrawable(YOUR_DRAWABLE); */

		// DEFAULT COLOR = Color.Black;
		mypicker.setTextColor(Color.BLUE);

		mypicker.setOnTimePickerChangeListener(new OnTimePickerChangeListener() {

			@Override
			public void onProgressChanged(int hours, int minutes, boolean isAm) {
				am.setText(isAm ? "am" : "pm");
				min.setText(minutes + "");
				hoursTxt.setText(hours + "");
			}

			@Override
			public void onSubmitClicked(int hours, int minutes, boolean isAm) {
				mydialog.dismiss();
			}
		});

		// Gets the object of the required time picker dialog.
		mydialog = mypicker.create();
		mydialog.show();
	}

}
