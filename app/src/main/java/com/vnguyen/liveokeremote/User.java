package com.vnguyen.liveokeremote;

import android.graphics.drawable.Drawable;

public class User {
    public String name;
    public String avatarURI;
    public Drawable avatar;

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
