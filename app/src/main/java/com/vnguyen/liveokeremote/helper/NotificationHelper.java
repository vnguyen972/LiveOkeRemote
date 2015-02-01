package com.vnguyen.liveokeremote.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.vnguyen.liveokeremote.DismissNotificationActivity;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationHelper {
    // Notification ids
    private static int LIVEOKE_REMOTE_NOTIFICATION_ID = 0;
    private static int LIVEOKE_REMOTE_CHAT_NOTIFICATION_ID = 1;

    private MainActivity context;

    // Intent action for app notifications
    public static String LIVEOKE_NOTIFICATION_PLAY = "Notification-Play";
    public static String LIVEOKE_NOTIFICATION_PAUSE = "Notification-Pause";
    public static String LIVEOKE_NOTIFICATION_NEXT = "Notification-Next";
    public static String LIVEOKE_NOTIFICATION_MIC_ON = "Notification-Mic-On";
    public static String LIVEOKE_NOTIFICATION_MIC_OFF = "Notification-Mic-Off";
    public static String LIVEOKE_NOTIFICATION_APP = "Notification-App";

    // flag to control icon switch logic
    public boolean pause = true;
    public boolean micOn = true;

    // default string to display on notification
    public String nowPlayingStr = "Happy Singing!";
    public String nowPlayingTitle;
    public int totalRsvp = 0;

    // hashmap to store user's chat message for chat notifications
    public HashMap<String, ArrayList<String>> chatMap;

    public NotificationHelper(Context context) {
        this.context = (MainActivity) context;
        nowPlayingTitle = this.context.getResources().getString(R.string.app_name);
        chatMap = new HashMap<>();
    }

    public void chatNotification(String name, String message) {
        ArrayList<String> msgList = new ArrayList<>();
        if (chatMap.containsKey(name)) {
            msgList = chatMap.get(name);
            msgList.add(message);
        } else {
            msgList.add(message);
            chatMap.put(name, msgList);
        }

        PendingIntent dismissIntent = DismissNotificationActivity.getDismissIntent(LIVEOKE_REMOTE_CHAT_NOTIFICATION_ID, context);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.putExtra("reply-chat","yes");
        notificationIntent.putExtra("chat-user",name);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setPriority(Notification.PRIORITY_MAX) // to be on top of the notification
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.microphone_icon)
                        .setAutoCancel(false)
                        .setContentTitle(name + " says")
                        .setContentText(message)
                        .setContentIntent(contentIntent)
                        .setColor(context.getResources().getColor(R.color.primary))
                        .setVibrate(null)
                        .addAction(R.drawable.ic_clear_white_18dp, "DISMISS", dismissIntent)
                        .addAction(R.drawable.ic_reply_white_18dp, "REPLY", contentIntent)
                ;

        // for big view when user swipe to expand the notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.setBigContentTitle(name + " says");
        inboxStyle.setSummaryText(context.getResources().getString(R.string.app_name) + " Chat");

        for (String msg: msgList) {
            inboxStyle.addLine(msg);
        }

        notificationBuilder.setStyle(inboxStyle);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(LIVEOKE_REMOTE_CHAT_NOTIFICATION_ID, notificationBuilder.build());
    }

    public void addNotification() {
        // click on notification will bring the app to the foreground
        Intent appIntent = new Intent(context, MainActivity.class);
        //appIntent.setAction(LIVEOKE_NOTIFICATION_APP);
        PendingIntent pendingAppIntent = PendingIntent.getActivity(context,0,appIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setDefaults(Notification.DEFAULT_LIGHTS)
                        .setSmallIcon(R.drawable.microphone_icon)
                        .setOngoing(true) // cannot swipe to remove, only to be removed by app
                        .setAutoCancel(false)
                        .setVibrate(null) // no vibration on this notification -> not annoying
                        .setContentIntent(pendingAppIntent)
                ;

        // custom view for this notification
        RemoteViews expandedView = new RemoteViews(context.getPackageName(),
                R.layout.now_playing_notification);

        expandedView.setImageViewResource(R.id.notification_icon,R.drawable.default_profile);
        expandedView.setTextViewText(R.id.notification_msg, nowPlayingStr);
        expandedView.setTextViewText(R.id.notification_title, nowPlayingTitle);
        if (totalRsvp > 0) {
            expandedView.setTextViewText(R.id.notification_rsvp, totalRsvp + "");
        }

        // set up on click
        // play button
        Intent intent = new Intent();
        if (pause) {
            expandedView.setImageViewResource(R.id.playIcon,android.R.drawable.ic_media_play);
            intent.setAction(LIVEOKE_NOTIFICATION_PLAY);
            intent.putExtra("command", "play");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            expandedView.setOnClickPendingIntent(R.id.playIcon, pendingIntent);
        } else {
            expandedView.setImageViewResource(R.id.playIcon,android.R.drawable.ic_media_pause);
            intent.setAction(LIVEOKE_NOTIFICATION_PAUSE);
            intent.putExtra("command", "pause");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT);
            expandedView.setOnClickPendingIntent(R.id.playIcon, pendingIntent);
        }

        // mic button
        Intent micIntent = new Intent();
        if (!micOn) {
            micIntent.setAction(LIVEOKE_NOTIFICATION_MIC_OFF);
            expandedView.setImageViewResource(R.id.micIcon, R.drawable.ic_mic_off_white_18dp);
            micIntent.putExtra("command","mic-off");
        } else {
            micIntent.setAction(LIVEOKE_NOTIFICATION_MIC_ON);
            expandedView.setImageViewResource(R.id.micIcon, R.drawable.ic_mic_white_18dp);
            micIntent.putExtra("command","mic-on");
        }
        PendingIntent micPendingIntent = PendingIntent.getBroadcast(context,0,micIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        expandedView.setOnClickPendingIntent(R.id.micIcon,micPendingIntent);

        // next button
        Intent nextIntent = new Intent(LIVEOKE_NOTIFICATION_NEXT);
        nextIntent.putExtra("command", "next");
        PendingIntent pendingNextIntent = PendingIntent.getBroadcast(context,0,nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        expandedView.setOnClickPendingIntent(R.id.nextIcon, pendingNextIntent);

        //expandedView.setOnClickPendingIntent(R.id.notification_msg,pendingAppIntent);

        notificationBuilder.setContent(expandedView);
        //builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(LIVEOKE_REMOTE_NOTIFICATION_ID, notificationBuilder.build());
    }

    // Remove notification
    public void removeNotification() {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(LIVEOKE_REMOTE_NOTIFICATION_ID);
    }

    public void removeChatNotification() {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(LIVEOKE_REMOTE_CHAT_NOTIFICATION_ID);
    }

}
