package com.vnguyen.liveokeremote;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import java.net.URI;
import java.util.ArrayList;

import cat.lafosca.facecropper.FaceCropper;

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
        int i = 0;
        for (User aFriend : friends) {
            setStringPreference("friend_" + i, aFriend.name.trim());
            i++;
        }
    }

    public void addFriend(User friend) {
        int total = getPreferences().getInt("total_friends",0);
        int position = total;
        getPreferences().edit().putString("friend_"+position,friend.name.trim()).apply();
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
            String name = preferences.getString("friend_"+i,"");
            Log.v(context.app.TAG,"userInfo = " + name);
            if (!name.equals("")) {
                User u = new User(name);
                Bitmap _bm;
                Bitmap bm;
                String avatarURI = PreferencesHelper.getInstance(context).getPreference(u.name+"_avatar");
                Log.v(context.app.TAG, "Avatar from Pref. URI: " + avatarURI);
                if (avatarURI != null && !avatarURI.equals("")) {
                    Uri imgURI = Uri.parse(avatarURI);
                    _bm = context.uriToBitmap(imgURI);
                } else {
                    _bm = context.drawableHelper.drawableToBitmap(context.getResources().getDrawable(R.drawable.default_profile));
                }
                FaceCropper mFaceCropper = new FaceCropper();
                if (!_bm.isRecycled()) {
                    bm = mFaceCropper.getCroppedImage(_bm);
                    if (bm.getWidth() > 120 || bm.getHeight() > 120) {
                        bm = Bitmap.createScaledBitmap(bm, 120, 120, false);
                    }
                    u.avatar =  new RoundImgDrawable(bm);
                    list.add(u);
                }
            }
        }
        Log.v(context.app.TAG,"TOTAL IN LIST = " + list.size());
        return list;
    }
}
