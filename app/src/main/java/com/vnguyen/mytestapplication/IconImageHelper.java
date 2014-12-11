package com.vnguyen.mytestapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;

import com.thedazzler.droidicon.IconicFontDrawable;

public class IconImageHelper {

    private MainActivity context;

    public IconImageHelper(Context context) {
        this.context = (MainActivity) context;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setIconAsBackground(String iconName,int color, ImageView img) {
        IconicFontDrawable icon = new IconicFontDrawable(context);
        icon.setIcon(iconName);
        icon.setIconColor(context.getResources().getColor(color));
        img.setImageDrawable(null);
        int currentVersion = Build.VERSION.SDK_INT;
        if (currentVersion >= 16) {
            img.setBackground(icon);
        } else {
            img.setBackgroundDrawable(icon);
        }
    }
}
