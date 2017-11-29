package com.dnion.app.android.injuriesapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceUtil {
	
	public static final String SHARED_NAME = "userinfo";
	
	public static String getSharedPreferenceValue(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, "");
	}

	public static String getSharedPreferenceValue(Context context, String key, String def) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getString(key, def);
	}
	
	public static void setSharedPreferenceValue(Context context, String key, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void removeSharedPreferenceValue(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public static boolean getSharedPreferenceBooleanValue(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, false);
	}
	
	public static boolean getSharedPreferenceBooleanValue(Context context, String key, boolean defval) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getBoolean(key, defval);
	}
	
	public static void setSharedPreferenceValue(Context context, String key, boolean value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}
	
	public static void setSharedPreferenceValue(Context context, String key, long value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
	
	public static long getSharedPreferenceLongValue(Context context, String key) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_NAME, Context.MODE_PRIVATE);
		return sharedPreferences.getLong(key, 0L);
	}
	
}
