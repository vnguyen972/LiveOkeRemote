package com.vnguyen.mytestapplication;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DrawableHelper {
    private static DrawableHelper helper;

    public static DrawableHelper getInstance() {
        if (helper == null) {
            helper = new DrawableHelper();
        }
        return helper;
    }

    public Drawable buildDrawable(String value, String shape) {
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
        return drawable;
    }

    public Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
