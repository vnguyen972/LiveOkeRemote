package com.vnguyen.mytestapplication;

import android.graphics.drawable.Drawable;

public class NavDrawerItem {
    private String title;
    private Drawable icon;
    private String count = "0";
    private boolean isCounterVisible = false;

    public NavDrawerItem(String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title, Drawable icon, boolean isCounterVisible, String count) {
        this.title = title;
        this.icon = icon;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public boolean isCounterVisible() {
        return isCounterVisible;
    }

    public void setCounterVisible(boolean isCounterVisible) {
        this.isCounterVisible = isCounterVisible;
    }
}
