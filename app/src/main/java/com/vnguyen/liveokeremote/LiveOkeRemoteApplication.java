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
                ReservedListItem item = new ReservedListItem("Requester " + 1, "Title " + i, null,Math.round(1000 + Math.random() * (9999 - 1000)));
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
            song.setTitle("Ai Dua Em Ve");
            song.setSinger("Dam Vinh Hung");
            songs.add(song);
        }
        return songs;
    }
}
