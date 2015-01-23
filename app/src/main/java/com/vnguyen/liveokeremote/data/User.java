package com.vnguyen.liveokeremote.data;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

public class User {
    public String name;
    public String ipAddress;
    public String avatarURI;
    public Drawable avatar;
    public ArrayList<LiveOkeRemoteBroadcastMsg> chatMessages;

    public User(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object usr) {
        if (!(usr instanceof User)) {
            return false;
        }
        User that = (User) usr;

        return this.name.equalsIgnoreCase(that.name);
    }
}
