package com.vnguyen.liveokeremote.data;

import android.graphics.drawable.Drawable;

public class ReservedListItem {
    public User requester;
    public String title;
    public Drawable icon;
    public String number;

    public ReservedListItem(User requester, String title, Drawable icon, String num) {
        this.requester = requester;
        this.title = title;
        this.icon = icon;
        this.number = num;
    }
}
