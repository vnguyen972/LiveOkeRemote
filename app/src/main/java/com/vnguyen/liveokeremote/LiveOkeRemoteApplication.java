package com.vnguyen.liveokeremote;

import android.app.Application;

import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.data.User;
import com.vnguyen.liveokeremote.helper.DrawableHelper;
import com.vnguyen.liveokeremote.helper.PreferencesHelper;

import java.util.ArrayList;

public class LiveOkeRemoteApplication extends Application {

    public static String TAG = "-LiveOkeRemote-";

    private ArrayList<ReservedListItem> rsvpList;
    public String songInitialIconBy = "Singer";
    public boolean landscapeOriented = false;
    public ArrayList<String> displaySongDescFrom;

    public LiveOkeRemoteApplication() {
    }

    @Override
    public void onCreate() {
        rsvpList = new ArrayList<>();
        displaySongDescFrom = new ArrayList<>();
        displaySongDescFrom.add("singer");
        super.onCreate();
    }

    public ArrayList<ReservedListItem> generateTestRsvpList() {

        if (rsvpList == null || rsvpList.isEmpty()) {
            for (int i = 0; i < 50; i++) {
                User requester = new User(i+"_Requester");
                ReservedListItem item = new ReservedListItem(requester, "Title " + i, null,
                        (Math.round(1000 + Math.random() * (9999 - 1000)))+"");
                item.icon = (new DrawableHelper()).buildDrawable(item.requester.name.substring(0, 1), "round");
                rsvpList.add(item);
            }
        }
        //Toast.makeText(getApplicationContext(), "rsvpList.size() =  " + rsvpList.size(), Toast.LENGTH_LONG).show();
        return rsvpList;
    }

    public ArrayList<User> generateTestFriends() {
        ArrayList<User> friends = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            User friend = new User("Friend " + i);
            friends.add(friend);
        }
        return friends;
    }

    public ArrayList<Song> generateTestSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        for (int i = 0; i < 20;i++) {
            Song song = new Song();
            song.title = "Ai Dua Em Ve_" + i;
            song.singer = "Dam Vinh Hung";
            song.icon = (new DrawableHelper()).buildDrawable(song.singer.substring(0, 1), "round");
            songs.add(song);
        }
        return songs;
    }
}
