package com.example.whatshistory.Util;

import android.content.Context;
import android.preference.PreferenceManager;

public class SharedPreference {
    private Context context;
    private String preferrence_name;
    public static String default_value = "";
    android.content.SharedPreferences pref;
    android.content.SharedPreferences.Editor settings;

    public SharedPreference(Context context) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        settings = pref.edit();

        this.context = context;
    }

    public boolean setString(String key, String value) {
        return settings.putString(key, value).commit();
    }

    public void setStringWithApply(String key, String value) {
        settings.putString(key, value).apply();
    }

    public boolean setBoolean(String key, Boolean value) {
        return settings.putBoolean(key, value).commit();
    }

    public boolean setString(String key, int value) {
        return settings.putInt(key, value).commit();
    }

    public String getPreference(String key) {
        return pref.getString(key, default_value);
    }

    public Boolean getPreferenceBoolean(String key) {
        return pref.getBoolean(key, false);
    }

    public String getPreference(String key, String default_value) {
        return pref.getString(key, default_value);
    }

    public boolean removeValue(String key) {
        return pref.edit().remove(key).commit();
    }

}
