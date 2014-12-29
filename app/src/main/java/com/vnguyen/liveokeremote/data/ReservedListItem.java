package com.vnguyen.liveokeremote.data;

import android.graphics.drawable.Drawable;

public class ReservedListItem {
    public String requester;
    public String title;
    public Drawable icon;
    public String number;

    public ReservedListItem(String requester, String title, Drawable icon, double num) {
        this.requester = requester;
        this.title = title;
        this.icon = icon;
        this.number = num + "";
    }
}
