package vxt.abmulani.uielement;

import android.graphics.Color;

public class CustomValues {

	private int MinutesPointerColor = Color.GREEN;
	private int HoursPointerColor = Color.RED;
	private int MinutesInnerCircle = Color.WHITE;
	private int HoursInnerCircle = Color.WHITE;
	private int HoursWheelTextColor=Color.BLACK;
	private int MinutesWheelTextColor=Color.BLACK;
	private int THemeColor = Color.LTGRAY;
	private int defaultColor=Color.BLACK;
	private int StartMinuteValue=0;
	private int StartHoursValue=12;
	
	int getHoursWheelTextColor() {
		return HoursWheelTextColor;
	}

	void setHoursWheelTextColor(int hoursWheelTextColor) {
		HoursWheelTextColor = hoursWheelTextColor;
	}

	int getMinutesWheelTextColor() {
		return MinutesWheelTextColor;
	}

	void setMinutesWheelTextColor(int minutesWheelTextColor) {
		MinutesWheelTextColor = minutesWheelTextColor;
	}

	
	
	int getStartMinuteValue() {
		return StartMinuteValue;
	}

	void setStartMinuteValue(int startMinuteValue) {
		StartMinuteValue = startMinuteValue;
	}

	int getStartHoursValue() {
		return StartHoursValue;
	}

	void setStartHoursValue(int startHoursValue) {
		StartHoursValue = startHoursValue;
	}

	int getMinutesPointerColor() {
		return MinutesPointerColor;
	}

	void setMinutesPointerColor(int minutesPointerColor) {
		MinutesPointerColor = minutesPointerColor;
	}

	int getHoursPointerColor() {
		return HoursPointerColor;
	}

	void setHoursPointerColor(int hoursPointerColor) {
		HoursPointerColor = hoursPointerColor;
	}

	int getMinutesInnerCircle() {
		return MinutesInnerCircle;
	}

	void setMinutesInnerCircle(int minutesInnerCircle) {
		MinutesInnerCircle = minutesInnerCircle;
	}

	int getHoursInnerCircle() {
		return HoursInnerCircle;
	}

	void setHoursInnerCircle(int hoursInnerCircle) {
		HoursInnerCircle = hoursInnerCircle;
	}

	int getTHemeColor() {
		return THemeColor;
	}

	void setTHemeColor(int tHemeColor) {
		THemeColor = tHemeColor;
	}

	int getDefaultColor() {
		return defaultColor;
	}

	void setDefaultColor(int dEfaultColor) {
		defaultColor = dEfaultColor;
	}

}
