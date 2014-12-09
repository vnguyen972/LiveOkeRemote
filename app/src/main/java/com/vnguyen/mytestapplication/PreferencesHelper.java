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

    public void loadPreferences() {
        context.ipAddress = getPreferences().getString(context.getResources().getString(R.string.ip_adress),"");
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
}
