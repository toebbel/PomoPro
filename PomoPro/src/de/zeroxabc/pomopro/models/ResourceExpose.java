package de.zeroxabc.pomopro.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

/**
 * This hack is used, to expose the Resources of the App in a static way.
 * @author Tobi
 *
 */
public class ResourceExpose {
	private static Resources r;
	private static final String PREFS_NAME = "PomoProSettings";


	/**
	 * do nothing
	 * @param r
	 */
	private ResourceExpose() {
		
	}
	
	/**
	 * Initializes the exposure of the resources
	 * @param c the resources to expose - can't be null
	 */
	public static void init(Context c) {
		if(c == null)
			throw new IllegalArgumentException("Resources can't be null for exposure");
		if(r == null) {
			r = c.getResources();
		}
	}
	
	/**
	 * Returns the Resources of the app
	 * @return the resources
	 */
	public static Resources getR() {
		if(r == null)
			throw new IllegalStateException("Resources have not been exposed yet");
		return r;
	}
	
}
