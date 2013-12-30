package vxt.abmulani.uielement;

public interface OnTimePickerChangeListener {
	/**
	 * @return hours
	 * @return minutes
	 * @return isAm = true if AM, false if PM
	 */
	public abstract void onProgressChanged(int hours, int minutes, boolean isAm);
	
	public abstract void onSubmitClicked(int hours, int minutes, boolean isAm);

}