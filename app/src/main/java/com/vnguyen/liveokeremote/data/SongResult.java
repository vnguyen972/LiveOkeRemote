package com.vnguyen.liveokeremote.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.helper.SongHelper;

public class SongResult {
    public String Id;
    public String Title;
    public String Artist;
    public String Avatar;
    public String UrlJunDownload;
    public String LyricsUrl;
    public String UrlSource;
    public String SiteId;
    public String HostName;
    public Bitmap avatarBitmap;
    public String lyrics;

    public void downloadAvatars(final MainActivity context, final Drawable placeHolderDrawable) {
        Ion.with(context)
                .load("http://example.com/image.png")
                .withBitmap()
                .placeholder(placeHolderDrawable)
                .error(placeHolderDrawable)
                .asBitmap()
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (result != null) {
                            avatarBitmap = result;
                        } else {
                            avatarBitmap = context.drawableHelper.drawableToBitmap(placeHolderDrawable);
                        }
                    }
                });
    }

    public void downloadLyric() {
        lyrics = SongHelper.getLyric(LyricsUrl);
    }
}
