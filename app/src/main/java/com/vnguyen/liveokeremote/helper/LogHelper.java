package com.vnguyen.liveokeremote.helper;

import android.util.Log;

import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;

public class LogHelper {

    public static void v(String str) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG,Log.VERBOSE)) {
            Log.v(LiveOkeRemoteApplication.TAG, str);
        }
    }

    public static void v(String str, Throwable t) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG, Log.VERBOSE)) {
            Log.v(LiveOkeRemoteApplication.TAG, str, t);
        }
    }

    public static void d(String str) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG,Log.DEBUG)) {
            Log.d(LiveOkeRemoteApplication.TAG, str);
        }
    }
    public static void d(String str, Throwable t) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG, Log.DEBUG)) {
            Log.d(LiveOkeRemoteApplication.TAG, str, t);
        }
    }

    public static void i(String str) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG,Log.INFO)) {
            Log.i(LiveOkeRemoteApplication.TAG, str);
        }
    }
    public static void i(String str, Throwable t) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG, Log.INFO)) {
            Log.i(LiveOkeRemoteApplication.TAG, str, t);
        }
    }

    public static void w(String str) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG,Log.WARN)) {
            Log.w(LiveOkeRemoteApplication.TAG, str);
        }
    }
    public static void w(String str, Throwable t) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG, Log.WARN)) {
            Log.w(LiveOkeRemoteApplication.TAG, str, t);
        }
    }

    public static void e(String str) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG,Log.ERROR)) {
            Log.e(LiveOkeRemoteApplication.TAG, str);
        }
    }
    public static void e(String str, Throwable t) {
        if (Log.isLoggable(LiveOkeRemoteApplication.TAG, Log.ERROR)) {
            Log.e(LiveOkeRemoteApplication.TAG, str, t);
        }
    }


}
