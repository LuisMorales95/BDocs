package com.BeeDocs.utils;

import android.content.Context;
import android.content.SharedPreferences;

import static com.BeeDocs.BeeDocsApplication.SuperContext;

public class SharedPreference {
	
	public static void SETSharedPreferences(String Tag, String Value) {
		SharedPreferences preferences = SuperContext().getSharedPreferences("InfoPersonal", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(Tag, Value);
		editor.apply();
	}
	
	public static String GETSharedPreferences(String Tag, String Value) {
		SharedPreferences preferences = SuperContext().getSharedPreferences("InfoPersonal", Context.MODE_PRIVATE);
		return preferences.getString(Tag, Value);
	}
	
}
