package com.vnguyen.mytestapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesHelper {
    private MainActivity context;
    private SharedPreferences preferences;

    private static PreferencesHelper prefHelper;

    private PreferencesHelper(Context context) {
        this.context = (MainActivity) context;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    public static PreferencesHelper getInstance(Context context) {
        if (prefHelper == null) {
            prefHelper = new PreferencesHelper(context);
        }
        return prefHelper;
    }

    public String getPreference(String key) {
        return getPreferences().getString(key,"");
    }

    public void setStringPreference(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
}
