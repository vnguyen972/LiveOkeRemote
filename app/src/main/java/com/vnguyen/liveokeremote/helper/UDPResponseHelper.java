package com.vnguyen.liveokeremote.helper;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Base64;
import android.widget.ListView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.vnguyen.liveokeremote.ChatAdapter;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.LiveOkeRemoteBroadcastMsg;
import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.data.User;

import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import static com.vnguyen.liveokeremote.service.UDPListenerService.BROADCAST_PORT;
import static com.vnguyen.liveokeremote.service.UDPListenerService.UDP_BROADCAST;

public class UDPResponseHelper {
    private MainActivity context;

    public UDPResponseHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void processResponseIntent(Intent intent) {
        String senderMSG;
        if (intent.getAction().equalsIgnoreCase(UDP_BROADCAST)) {
            String senderIP = intent.getStringExtra("senderIP");
            int senderPORT = intent.getIntExtra("senderPORT", 0);
            senderMSG = intent.getStringExtra("message");
            LogHelper.v( "Received msg: " + senderMSG);
            processMessage(senderIP, senderMSG);
        } else if (intent.getAction().equalsIgnoreCase(NotificationHelper.LIVEOKE_NOTIFICATION_PLAY) ||
                intent.getAction().equalsIgnoreCase(NotificationHelper.LIVEOKE_NOTIFICATION_PAUSE) ||
                intent.getAction().equalsIgnoreCase(NotificationHelper.LIVEOKE_NOTIFICATION_NEXT) ||
                intent.getAction().equalsIgnoreCase(NotificationHelper.LIVEOKE_NOTIFICATION_MIC_OFF) ||
                intent.getAction().equalsIgnoreCase(NotificationHelper.LIVEOKE_NOTIFICATION_MIC_ON)) {
            LogHelper.v( "Received intent: " + intent);
            dumpIntent(intent);
            senderMSG = intent.getStringExtra("command");
            LogHelper.v( "Received message: " + senderMSG);
            processNotification(senderMSG);
        }
    }

    private void processNotification(String message) {
        switch (message) {
            case "play":case "pause":
                context.liveOkeUDPClient.sendMessage("play",
                        context.liveOkeUDPClient.liveOkeIPAddress,
                        context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
//                if (message.equalsIgnoreCase("play")) {
//                    context.notificationHelper.pause = false;
//                    context.notificationHelper.addNotification();
//                } else {
//                    context.notificationHelper.pause = true;
//                    context.notificationHelper.addNotification();
//                }
                break;
            case "next":
                context.liveOkeUDPClient.sendMessage("next",
                        context.liveOkeUDPClient.liveOkeIPAddress,
                        context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
                break;
            case "mic-on":case "mic-off":
                context.liveOkeUDPClient.sendMessage("toggleaudio",
                        context.liveOkeUDPClient.liveOkeIPAddress,
                        context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
//                if (message.equalsIgnoreCase("mic-on")) {
//                    context.notificationHelper.micOn = false;
//                    context.notificationHelper.addNotification();
//                } else {
//                    context.notificationHelper.micOn = true;
//                    context.notificationHelper.addNotification();
//                }
                break;
            default:
                break;
        }
    }

    //public void processMessage(String senderIP, String senderMSG) {
    public void processMessage(String senderIP, String senderMSG) {
            if (senderMSG.startsWith("{")) {
                // Message is a JSON message
                final LiveOkeRemoteBroadcastMsg msg = (new Gson()).fromJson(senderMSG, LiveOkeRemoteBroadcastMsg.class);
                msg.ipAddress = senderIP;
                LogHelper.v("msg.ip = " + msg.ipAddress);
                if (msg.greeting.equalsIgnoreCase("my.avatar")) {
                    // fromWhere will have the name that this avatar belongs
                    User u = context.friendsListHelper.findFriend(msg.fromWhere);
                    if (u != null) {
                        byte[] byteArray = Base64.decode(msg.name, Base64.DEFAULT);
                        Bitmap breceived = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                        u.avatar = new BitmapDrawable(breceived);
                    }
                } else if (msg.fromWhere.equalsIgnoreCase(context.getResources().getString(R.string.app_name))) {
                    // if the message coming from this app
                    // is it really from another client with different IP?
                    if (!context.liveOkeUDPClient.isMine(senderIP)) {
                        try {
                            if (msg.greeting.equalsIgnoreCase("Hi") || msg.greeting.equalsIgnoreCase("Hello") ||
                                    msg.greeting.equalsIgnoreCase("Pause") || msg.greeting.equalsIgnoreCase("Resume")) {
                                User u = context.friendsListHelper.findFriend(msg.name);
                                if (u == null || !u.ipAddress.equalsIgnoreCase(msg.ipAddress)) {
                                    u = new User(msg.name);
                                    u.ipAddress = senderIP;
                                    //u.avatar = context.drawableHelper.buildDrawable(u.name.charAt(0) + "", "round");
                                    u.avatar = PreferencesHelper.getInstance(context).findFriendAvatar(u.name);
                                    context.friendsList.add(u);
                                }
                                SnackbarManager.show(Snackbar.with(context)
                                        .type(SnackbarType.MULTI_LINE)
                                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                        .textColor(Color.WHITE)
                                        .color(context.getResources().getColor(R.color.indigo_500))
                                        .text(msg.name + " is online!"));
                                LogHelper.v("friends.list = " + context.friendsList.size());
                                if (msg.greeting.equalsIgnoreCase("Hi")) {
                                    // only say Hello when receive Hi
                                    LiveOkeRemoteBroadcastMsg bcMsg =
                                            new LiveOkeRemoteBroadcastMsg("Hello",
                                                    context.getResources().getString(R.string.app_name), context.me.name);
                                    if (context.liveOkeUDPClient != null) {
                                        context.liveOkeUDPClient.sendMessage((new Gson()).toJson(bcMsg), senderIP, BROADCAST_PORT);
//                                        byte[] imageBytes = context.drawableHelper.encodeAvatar(context.me);
//                                        bcMsg = new LiveOkeRemoteBroadcastMsg("my.avatar",context.me.name, Base64.encodeToString(imageBytes,Base64.DEFAULT));
//                                        context.liveOkeUDPClient.sendMessage((new Gson()).toJson(bcMsg),null, UDPListenerService.BROADCAST_PORT);
                                    }
                                }
                                final User usr = u;
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (context.viewFlipper.getDisplayedChild() != 0) {
                                            context.friendsListHelper.displayFriendsListPanel();
                                        }
                                        MaterialDialog dialog = context.chatHelper.chat(usr, false);
                                        ListView list = (ListView) dialog.getCustomView().findViewById(R.id.chat_message);
                                        if (msg.greeting.equalsIgnoreCase("Hi") || msg.greeting.equalsIgnoreCase("Pause") ||
                                                msg.greeting.equalsIgnoreCase("Resume")) {
                                            // update chat window
                                            ((ChatAdapter) list.getAdapter()).messages.add(msg);
                                            ((ChatAdapter) list.getAdapter()).notifyDataSetChanged();

                                        }
                                    }
                                });
                            } else if (msg.greeting.equalsIgnoreCase("Bye")) {
                                SnackbarManager.show(Snackbar.with(context)
                                        .type(SnackbarType.MULTI_LINE)
                                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                        .textColor(Color.WHITE)
                                        .color(context.getResources().getColor(R.color.indigo_500))
                                        .text(msg.name + " is offline!"));

                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        User u = context.friendsListHelper.findFriend(msg.name);
                                        if (u != null) {
                                            // update chat window
                                            MaterialDialog dialog = context.chatHelper.chat(u, false);
                                            ListView list = (ListView) dialog.getCustomView().findViewById(R.id.chat_message);
                                            ((ChatAdapter)list.getAdapter()).messages.add(msg);
                                            ((ChatAdapter)list.getAdapter()).notifyDataSetChanged();
                                        }
                                        context.friendsListHelper.adapter.removeFriendFromAdapter(msg.name);
                                    }
                                });
                            } else if (msg.greeting.equalsIgnoreCase("Chat")) {
                                if (!context.isInForegroundMode) {
                                    context.notificationHelper.chatNotification(msg.name,msg.message);
                                }
                                Vibrator v = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 500 milliseconds
                                v.vibrate(500);
                                MaterialDialog dialog = null;
                                User messenger = context.friendsListHelper.findFriend(msg.name);
                                if (messenger != null) {
                                    dialog = context.chatHelper.chat(messenger, false);
                                    ListView list = (ListView) dialog.getCustomView().findViewById(R.id.chat_message);
                                    ChatAdapter ca = (ChatAdapter) list.getAdapter();
                                    ca.messages.add(msg);
                                    ca.notifyDataSetChanged();
                                    if (dialog != null) {
                                        final MaterialDialog d = dialog;
                                        if (!d.isShowing()) {
                                            SnackbarManager.show(Snackbar.with(context)
                                                            .type(SnackbarType.MULTI_LINE)
                                                            .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                            .textColor(Color.WHITE)
                                                            .color(context.getResources().getColor(R.color.indigo_500))
                                                            .text(msg.name + " says: " + msg.message)
                                                            .actionLabel("Reply")
                                                            .actionListener(new ActionClickListener() {
                                                                @Override
                                                                public void onActionClicked(Snackbar snackbar) {
                                                                    LogHelper.v("Reply the message");
                                                                    d.show();
                                                                }
                                                            })
                                            );
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            LogHelper.e(e.getMessage(), e);
                        }
                    }
                }
            } else {
                // Process LiveOke Msg here
                if (context.liveOkeUDPClient.liveOkeIPAddress == null) {
                    context.liveOkeUDPClient.liveOkeIPAddress = senderIP;
                }
                if (senderMSG.equalsIgnoreCase("Pong")) {
                    context.liveOkeUDPClient.pingCount = 0;
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.toggleOn();
                        }
                    });
                } else if (senderMSG.startsWith("MasterCode:")) {
                    String code = senderMSG.substring(11, senderMSG.length());
                    LogHelper.d("Server Master Code = " + code);
                    if (!code.equalsIgnoreCase("")) {
                        context.serverMasterCode = senderMSG.substring(11, senderMSG.length());
//                        // test notification
//                        context.notificationHelper.pause = true;
//                        context.notificationHelper.addNotification();
                    }
                } else if (senderMSG.startsWith("Track:")) {
                    String nextTrack = senderMSG.substring(6, senderMSG.length());
                    if (nextTrack.equalsIgnoreCase("Karaoke")) {
                        // assuming current is music
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                context.floatingButtonsHelper.micOff();
                                context.notificationHelper.micOn = false;
                                context.notificationHelper.addNotification();
                            }
                        });
                    } else {
                        // current is Karaoke
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                context.floatingButtonsHelper.micOn();
                                context.notificationHelper.micOn = true;
                                context.notificationHelper.addNotification();
                            }
                        });
                    }
                } else if (senderMSG.startsWith("Pause:")) {
                    final String currentAudioTrack = senderMSG.substring(6, senderMSG.length());
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.nowPlayingHelper.pushTitle("Pause:<br><b>" + context.liveOkeUDPClient.currentSong + "</b>");
                            context.floatingButtonsHelper.togglePlayBtn();
                            context.notificationHelper.nowPlayingTitle = "PAUSE";
                            context.notificationHelper.nowPlayingStr = context.liveOkeUDPClient.currentSong;
                            context.notificationHelper.pause = true;
                            if (currentAudioTrack.equalsIgnoreCase("Karaoke")) {
                                context.floatingButtonsHelper.micOn();
                                context.notificationHelper.micOn = true;
                            } else {
                                context.notificationHelper.micOn = false;
                                context.floatingButtonsHelper.micOff();
                            }
                            context.notificationHelper.addNotification();
                        }
                    });
                } else if (senderMSG.startsWith("Quit")) {
                    // reset address in case when LiveOke is back on it has a new address
                    // then we can retrieve it
                    context.liveOkeUDPClient.liveOkeIPAddress = null;
                } else if (senderMSG.startsWith("Playing:")) {
                    context.liveOkeUDPClient.currentSong = senderMSG.substring(8, senderMSG.length());
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.nowPlayingHelper.setTitle("Current Song:<br><b>" + context.liveOkeUDPClient.currentSong + "</b>");
                            context.notificationHelper.nowPlayingTitle = "NOW PLAYING";
                            context.notificationHelper.nowPlayingStr = context.liveOkeUDPClient.currentSong;
                        }
                    });
                } else if (senderMSG.startsWith("Play:")) {
                    final String currentAudioTrack = senderMSG.substring(5, senderMSG.length());
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.nowPlayingHelper.setTitle("Now Playing:<br><b>" + context.liveOkeUDPClient.currentSong + "</b>");
                            context.notificationHelper.nowPlayingTitle = "NOW PLAYING";
                            context.notificationHelper.nowPlayingStr = context.liveOkeUDPClient.currentSong;
                            context.floatingButtonsHelper.togglePlayBtn();
                            context.notificationHelper.pause = false;
                            if (currentAudioTrack.equalsIgnoreCase("Karaoke")) {
                                context.floatingButtonsHelper.micOn();
                                context.notificationHelper.micOn = true;
                            } else {
                                context.floatingButtonsHelper.micOff();
                                context.notificationHelper.micOn = false;
                            }
                            context.notificationHelper.addNotification();
                        }
                    });
                } else if (senderMSG.startsWith("Reserve:")) {
                    if (context.liveOkeUDPClient.rsvpList != null) {
                        context.liveOkeUDPClient.rsvpList.clear();
                    }
                    String msgList = senderMSG.substring(8, senderMSG.length());
                    StringTokenizer stok = new StringTokenizer(msgList, " ");
                    while (stok.hasMoreTokens()) {
                        StringTokenizer reqTok = new StringTokenizer(stok.nextToken(), ".");
                        if (reqTok.hasMoreTokens()) {
                            final String songID = reqTok.nextToken();
                            String requester = reqTok.nextToken().replace("_", " ");
                            try {
                                context.db.open();
                                final Song song = context.db.findSongByID(songID);
                                if (song != null) {
                                    final User u = new User(requester);
                                    if (context.me.name.equalsIgnoreCase(requester)) {
                                        u.avatar = context.me.avatar;
                                    } else {
                                        if (context.friendsList != null) {
                                            for (User user : context.friendsList) {
                                                if (user.name.equals(u.name)) {
                                                    LogHelper.v( "Found requester on friendlist!");
                                                    if (user.avatarURI != null && !user.avatarURI.equals("")) {
                                                        u.avatar = user.avatar;
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (u.avatar == null) {
                                        u.avatar = (new DrawableHelper()).buildDrawable(u.name.substring(0, 1), "round");
                                    }
                                    final ReservedListItem rsvpItem = new ReservedListItem(u, song.title, song.icon, songID);
                                    context.liveOkeUDPClient.rsvpList.add(rsvpItem);
                                }
                            } catch (Exception ex) {
                                LogHelper.e(ex.getMessage(), ex);
                            } finally {
                                context.db.close();
                            }
                        }
                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.rsvpPanelHelper.refreshRsvpList(context.liveOkeUDPClient.rsvpList);
                            context.updateRsvpCounter(context.liveOkeUDPClient.rsvpList.size());
                            context.notificationHelper.totalRsvp = context.liveOkeUDPClient.rsvpList.size();
                            context.notificationHelper.addNotification();
                        }
                    });
                } else if (senderMSG.startsWith("CurrentVolume:")) {
                    String volume = senderMSG.substring(14, senderMSG.length());
                    PreferencesHelper.getInstance(context).setStringPreference("volume",volume);
                }
            }
    }
    public void dumpIntent(Intent i){

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            LogHelper.v("Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                LogHelper.v("[" + key + "=" + bundle.get(key)+"]");
            }
            LogHelper.v("Dumping Intent end");
        }
    }
}
