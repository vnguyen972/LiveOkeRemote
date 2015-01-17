package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.RoundImgDrawable;
import com.vnguyen.liveokeremote.data.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

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

    public void remoteMyAvatar() {
        preferences.edit().remove("myAvatarURI").apply();
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
        Set<String> set = getPreferences().getStringSet("friends", new HashSet<String>());
        set.add(friend.name);
        getPreferences().edit().putStringSet("friends", set).apply();
        getPreferences().edit().putInt("total_friends", total+1).apply();
    }

    public void addSongDescDisplay(ArrayList<String> bys) {
        Set<String> set = new HashSet<>(bys.size());
        for (String by : bys) {
            set.add(by);
        }
        getPreferences().edit().putStringSet(context.getResources().getString(R.string.song_desc_display),set).apply();
    }

    public void removeFriend(User friend, int position) {
        Set<String> set = getPreferences().getStringSet("friends",new HashSet<String>());
        if (!set.isEmpty()) {
            for (Iterator<String> it = set.iterator(); it.hasNext(); ) {
                String name = it.next();
                if (name.equalsIgnoreCase(friend.name)) {
                    it.remove();
                    break;
                }
            }
            getPreferences().edit().putStringSet("friends", set).apply();
            getPreferences().edit().remove(friend.name + "_avatar").apply();
            int total = getPreferences().getInt("total_friends", 0);
            total--;
            getPreferences().edit().putInt("total_friends", total).apply();
        }
    }

    public ArrayList<User> retrieveFriendsList() {
        ArrayList<User> list = new ArrayList<>();
        int total = preferences.getInt("total_friends",0);
        Set<String> set = preferences.getStringSet("friends", new HashSet<String>());
        Log.v(context.app.TAG, "TOTAL IN FRIEND LIST = " + set.size());
        for (Iterator<String> it = set.iterator();it.hasNext();) {
            String name = it.next();
            Log.v(context.app.TAG,"userInfo = " + name);
            if (name != null && !name.equals("")) {
                User u = new User(name);
                list.add(u);
            }
        }
        Log.v(context.app.TAG,"TOTAL IN F.LIST = " + list.size());
        return list;
    }

    public ArrayList<User> retrieveFriends() {
        ArrayList<User> list = new ArrayList<>();
        int total = preferences.getInt("total_friends",0);
        Set<String> set = preferences.getStringSet("friends", new HashSet<String>());
        Log.v(context.app.TAG, "TOTAL FRIENDS = " + set.size());
        for (Iterator<String> it = set.iterator();it.hasNext();) {
            String name = it.next();
            Log.v(context.app.TAG,"userInfo = " + name);
            if (!name.equals("")) {
                User u = new User(name);
                Bitmap _bm;
                Bitmap bm;
                String avatarURI = PreferencesHelper.getInstance(context).getPreference(u.name+"_avatar");
                Log.v(context.app.TAG, "Avatar from Pref. URI: " + avatarURI);
                if (avatarURI != null && !avatarURI.equals("")) {
                    u.avatarURI = avatarURI;
                    Uri imgURI = Uri.parse(avatarURI);
                    _bm = context.uriToBitmap(imgURI);
                    FaceCropper mFaceCropper = new FaceCropper();
                    if (!_bm.isRecycled()) {
                        bm = mFaceCropper.getCroppedImage(_bm);
                        if (bm.getWidth() > 120 || bm.getHeight() > 120) {
                            bm = Bitmap.createScaledBitmap(bm, 120, 120, false);
                        }
                        u.avatar =  new RoundImgDrawable(bm);
                    } else {
                        Log.v(context.app.TAG, "bitmap is recycling..");
                    }
                } else {
                    //_bm = context.drawableHelper.drawableToBitmap(context.getResources().getDrawable(R.drawable.default_profile));
                    //_bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profile);
                    u.avatar = context.drawableHelper.buildDrawable(u.name.charAt(0)+"","round");
                }
                list.add(u);
            }
        }
        Log.v(context.app.TAG,"TOTAL IN LIST = " + list.size());
        return list;
    }
}
