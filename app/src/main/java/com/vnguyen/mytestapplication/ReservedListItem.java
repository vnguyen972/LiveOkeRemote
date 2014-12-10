package com.vnguyen.mytestapplication;

import android.graphics.drawable.Drawable;

public class ReservedListItem {
    private String requester;
    private String title;
    private Drawable icon;

    public ReservedListItem(String requester, String title, Drawable icon) {
        this.requester = requester;
        this.title = title;
        this.icon = icon;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
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
}
