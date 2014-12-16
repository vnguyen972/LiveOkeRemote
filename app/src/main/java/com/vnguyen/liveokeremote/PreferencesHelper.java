package com.vnguyen.liveokeremote;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.StringTokenizer;

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

    public void saveFriends(ArrayList<User> friends) {
        preferences.edit().putInt("total_friends", friends.size()).apply();
        for (int i = 0; i < friends.size();i++) {
            User aFriend = friends.get(i);
            setStringPreference("friend_"+i,aFriend.getName().trim());
        }
    }

    public void addFriend(User friend) {
        int total = getPreferences().getInt("total_friends",0);
        int position = total;
        getPreferences().edit().putString("friend_"+position,friend.getName().trim()).apply();
        getPreferences().edit().putInt("total_friends", total+1).apply();
    }

    public void removeFriend(User friend, int position) {
        getPreferences().edit().remove("friend_"+position).apply();
        int total = getPreferences().getInt("total_friends",0);
        total--;
        getPreferences().edit().putInt("total_friends",total).apply();
    }

    public ArrayList<User> retrieveFriends() {
        ArrayList<User> list = new ArrayList<>();
        int total = preferences.getInt("total_friends",0);
        Log.v(context.app.TAG, "TOTAL FRIENDS = " + total);
        for (int i = 0; i < total;i++) {
            String userInfo = preferences.getString("friend_"+i,"");
            Log.v(context.app.TAG,"userInfo = " + userInfo);
            if (!userInfo.equals("")) {
                User u = new User(userInfo);
                list.add(u);
            }
        }
        Log.v(context.app.TAG,"TOTAL IN LIST = " + list.size());
        return list;
    }
}
