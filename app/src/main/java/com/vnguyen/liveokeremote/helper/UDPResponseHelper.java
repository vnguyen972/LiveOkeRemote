package com.vnguyen.liveokeremote.helper;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.LiveOkeRemoteBroadcastMsg;

public class UDPResponseHelper {
    private MainActivity context;

    public UDPResponseHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void processResponseIntent(Intent intent) {
        String senderIP = intent.getStringExtra("senderIP");
        int senderPORT = intent.getIntExtra("senderPORT",0);
        String senderMSG = intent.getStringExtra("message");
        Log.v(LiveOkeRemoteApplication.TAG, "Received from: " + senderIP + ":" + senderPORT);
        Log.v(LiveOkeRemoteApplication.TAG,"Received msg: " + senderMSG);
        if (senderMSG.startsWith("{")) {
            // Message is a JSON message
            LiveOkeRemoteBroadcastMsg msg = (new Gson()).fromJson(senderMSG, LiveOkeRemoteBroadcastMsg.class);
            // if the message coming from this app
            if (!msg.from.equalsIgnoreCase(context.getResources().getString(R.string.app_name))) {
                // is it really from another client with different IP?
                if (senderIP.equals(UDPBroadcastHelper.getMyIP(context))) {
                    try {
                        if (msg.greeting.equalsIgnoreCase("Hi")) {
                            SnackbarManager.show(Snackbar.with(context)
                                    .type(SnackbarType.MULTI_LINE)
                                    .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                    .textColor(Color.WHITE)
                                    .color(context.getResources().getColor(R.color.indigo_500))
                                    .text(msg.name + " is online!"));
                        } else if (msg.greeting.equalsIgnoreCase("Bye")) {
                            SnackbarManager.show(Snackbar.with(context)
                                    .type(SnackbarType.MULTI_LINE)
                                    .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                    .textColor(Color.WHITE)
                                    .color(context.getResources().getColor(R.color.indigo_500))
                                    .text(msg.name + " is offline!"));
                        }
                    } catch (Exception e) {
                        Log.e(context.app.TAG, e.getMessage(), e);
                    }
                }
            }
        } else {
            if (context.liveOkeUDPClient != null) {
                context.liveOkeUDPClient.liveOkeSocketInfo.ipAddress = senderIP;
                context.liveOkeUDPClient.liveOkeSocketInfo.port = senderPORT+"";
                if (senderMSG.startsWith("MasterCode:")) {
                    context.liveOkeUDPClient.liveOkeSocketInfo.masterCode = senderMSG.substring(11, senderMSG.length());
                }
            }
        }
    }
}
