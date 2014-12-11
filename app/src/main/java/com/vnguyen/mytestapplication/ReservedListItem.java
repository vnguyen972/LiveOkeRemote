package com.vnguyen.mytestapplication;

import android.graphics.drawable.Drawable;

public class ReservedListItem {
    private String requester;
    private String title;
    private Drawable icon;
    private String number;

    public ReservedListItem(String requester, String title, Drawable icon, double num) {
        this.requester = requester;
        this.title = title;
        this.icon = icon;
        this.number = num + "";
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
