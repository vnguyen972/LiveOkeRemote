package com.vnguyen.liveokeremote.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.NotificationActivity;
import com.vnguyen.liveokeremote.R;

public class NotificationHelper {
    private static int LIVEOKE_REMOTE_NOTIFICATION_ID = 0;

    private MainActivity context;

    public NotificationHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void addNotification() {
        //int notificationId = new Random().nextInt(); // just use a counter in some util class...
        PendingIntent dismissIntent = NotificationActivity.getDismissIntent(LIVEOKE_REMOTE_NOTIFICATION_ID, context);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setAutoCancel(false)
                        .setContentTitle("Notifications Example")
                        .setContentText("This is a test notification")
                        .addAction(R.drawable.abc_ic_clear_mtrl_alpha,"DISMISS",dismissIntent)
                        .addAction(R.drawable.abc_ic_menu_share_mtrl_alpha,"REPLY",contentIntent);

        //builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(LIVEOKE_REMOTE_NOTIFICATION_ID, builder.build());
    }

    // Remove notification
    public void removeNotification() {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(LIVEOKE_REMOTE_NOTIFICATION_ID);
    }


}
