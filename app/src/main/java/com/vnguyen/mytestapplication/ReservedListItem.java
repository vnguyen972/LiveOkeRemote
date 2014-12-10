package com.vnguyen.mytestapplication;

import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

public class ReservedListItem {
    private String requester;
    private String title;
    private Drawable icon;

    public ReservedListItem(String requester, String title, Drawable icon) {
        this.requester = requester;
        this.title = title;
        if (icon != null) {
            this.icon = icon;
        } else {
            ColorGenerator generator = ColorGenerator.DEFAULT;
            int color = generator.getColor(title.charAt(0));
            TextDrawable iconDrawable = TextDrawable.builder().
                    beginConfig()
                    .withBorder(4) /* thickness in px */
                    .width(5)
                    .endConfig()
                    .buildRound(title.charAt(0)+"", color);
            this.icon = iconDrawable;
        }
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
