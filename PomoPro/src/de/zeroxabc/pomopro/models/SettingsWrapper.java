package de.zeroxabc.pomopro.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * This class wraps read-access to the shared preferences.
 * @author Tobi
 *
 */
public class SettingsWrapper {

	SharedPreferences sharedPrefs;
	public SettingsWrapper(Context c) {
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(c);
	}
	
	/**
	 * Returns the duration of a pomodoro in ms.
	 * @return duration of a pomodor.
	 */
	public int getDurationPomo() {
		return Integer.parseInt(sharedPrefs.getString("timerPomodoro", "1500000"));
	}
	
	/**
	 * Returns the duration of a short break in ms.
	 * @return duration of a short break.
	 */
	public int getDurationShortBreak() {
		return Integer.parseInt(sharedPrefs.getString("timerShortB", "300000"));
	}
	
	/**
	 * Returns the duration of a long break in ms.
	 * @return duration of a long break.
	 */
	public int getDurationLongBreak() {
		return Integer.parseInt(sharedPrefs.getString("timerLongB", "900000"));
	}
	
	/**
	 * Returns true, if the device shall vibrate after each pomodoro and break
	 * @return true if vibration is enabled
	 */
	public boolean getVibrationSetting() {
		return sharedPrefs.getBoolean("enableVibrate", true);
	}
}
