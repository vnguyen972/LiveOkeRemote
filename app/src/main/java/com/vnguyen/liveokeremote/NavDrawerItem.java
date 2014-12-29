package com.vnguyen.liveokeremote;

import android.graphics.drawable.Drawable;

public class NavDrawerItem {
    public String title;
    public Drawable icon;
    public String count = "0";
    public boolean counterVisible = false;

    public NavDrawerItem(String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title, Drawable icon, boolean isCounterVisible, String count) {
        this.title = title;
        this.icon = icon;
        this.counterVisible = isCounterVisible;
        this.count = count;
    }

}
