package com.vnguyen.mytestapplication;

import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

public class TextDrawableHelper {
    private static TextDrawableHelper helper;

    public static TextDrawableHelper getInstance() {
        if (helper == null) {
            helper = new TextDrawableHelper();
        }
        return helper;
    }

    public void buildDrawable(ImageView view, String value, String shape) {
        ColorGenerator generator = ColorGenerator.DEFAULT;
        int color = generator.getColor(value);
        TextDrawable.IBuilder builder;
        if (shape != null && shape.equalsIgnoreCase("round")) {
            builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig().round();
        } else if (shape != null && shape.equalsIgnoreCase("rect")) {
            builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig().rect();
        } else {
            builder = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig().roundRect(5);
        }
        TextDrawable drawable = builder.build(value, color);
        view.setImageDrawable(drawable);
    }
}
