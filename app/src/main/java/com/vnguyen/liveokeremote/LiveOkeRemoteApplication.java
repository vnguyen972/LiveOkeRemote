package com.vnguyen.liveokeremote;

import android.app.Application;

import java.util.ArrayList;

public class LiveOkeRemoteApplication extends Application {

    public static String TAG = "-LiveOkeRemote-";

    private ArrayList<ReservedListItem> rsvpList;

    public LiveOkeRemoteApplication() {
    }


    @Override
    public void onCreate() {
        rsvpList = new ArrayList<>();
        super.onCreate();
    }

    public ArrayList<ReservedListItem> generateTestRsvpList() {
        if (rsvpList == null || rsvpList.isEmpty()) {
            for (int i = 0; i < 50; i++) {
                ReservedListItem item = new ReservedListItem(i+"_Requester", "Title " + i, null,Math.round(1000 + Math.random() * (9999 - 1000)));
                item.icon = (new DrawableHelper()).buildDrawable(item.requester.substring(0, 1), "round");
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
            song.title = "Ai Dua Em Ve";
            song.singer = "Dam Vinh Hung";
            song.icon = (new DrawableHelper()).buildDrawable(song.singer.substring(0, 1), "round");
            songs.add(song);
        }
        return songs;
    }
}
