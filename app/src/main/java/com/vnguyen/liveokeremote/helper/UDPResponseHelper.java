package com.vnguyen.liveokeremote.helper;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.LiveOkeRemoteBroadcastMsg;
import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.data.User;
import com.vnguyen.liveokeremote.service.UDPListenerService;

import java.util.StringTokenizer;

public class UDPResponseHelper {
    private MainActivity context;

    public UDPResponseHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void processResponseIntent(Intent intent) {
        final String senderIP = intent.getStringExtra("senderIP");
        final int senderPORT = intent.getIntExtra("senderPORT", 0);
        final String senderMSG = intent.getStringExtra("message");
        Log.v(LiveOkeRemoteApplication.TAG, "Received msg: " + senderMSG);
        processMessage(senderIP, senderMSG);
    }

    //public void processMessage(String senderIP, String senderMSG) {
    public void processMessage(String senderIP, String senderMSG) {
            if (senderMSG.startsWith("{")) {
                // Message is a JSON message
                final LiveOkeRemoteBroadcastMsg msg = (new Gson()).fromJson(senderMSG, LiveOkeRemoteBroadcastMsg.class);
                msg.ipAddress = senderIP;
                // if the message coming from this app
                if (msg.fromWhere.equalsIgnoreCase(context.getResources().getString(R.string.app_name))) {
                    // is it really from another client with different IP?
                    if (!context.liveOkeUDPClient.isMine(senderIP)) {
                        User u = new User(msg.name);
                        u.ipAddress = senderIP;
                        u.avatar = context.drawableHelper.buildDrawable(u.name.charAt(0)+"","round");
                        context.friendsList.add(u);
                        try {
                            if (msg.greeting.equalsIgnoreCase("Hi") || msg.greeting.equalsIgnoreCase("Hello")) {
                                SnackbarManager.show(Snackbar.with(context)
                                        .type(SnackbarType.MULTI_LINE)
                                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                        .textColor(Color.WHITE)
                                        .color(context.getResources().getColor(R.color.indigo_500))
                                        .text(msg.name + " is online!"));
                                Log.v(LiveOkeRemoteApplication.TAG,"friends.list = " + context.friendsList.size());
                                if (msg.greeting.equalsIgnoreCase("Hi")) {
                                    // only say Hello when receive Hi
                                    LiveOkeRemoteBroadcastMsg bcMsg =
                                            new LiveOkeRemoteBroadcastMsg("Hello",
                                                    context.getResources().getString(R.string.app_name), context.me.name);
                                    if (context.liveOkeUDPClient != null) {
                                        context.liveOkeUDPClient.sendMessage((new Gson()).toJson(bcMsg), senderIP, UDPListenerService.BROADCAST_PORT);
                                    }
                                }
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (context.viewFlipper.getDisplayedChild() != 0) {
                                            context.friendsListHelper.displayFriendsListPanel();
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
                                        context.friendsListHelper.adapter.removeFriendFromAdapter(msg.name);
                                    }
                                });
                            } else if (msg.greeting.equalsIgnoreCase("Chat")) {
                                SnackbarManager.show(Snackbar.with(context)
                                        .type(SnackbarType.MULTI_LINE)
                                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                        .textColor(Color.WHITE)
                                        .color(context.getResources().getColor(R.color.indigo_500))
                                        .text(msg.name + " says: " + msg.message)
                                        .actionLabel("Reply")
                                        .actionListener(new ActionClickListener() {
                                            @Override
                                            public void onActionClicked(Snackbar snackbar) {
                                                Log.v(LiveOkeRemoteApplication.TAG,"Reply the message");
                                                MaterialDialog dialog;
                                                User u = context.friendsListHelper.findFriend(msg.name);
                                                if (u != null) {
                                                    if (context.chatMap.containsKey(msg.name)) {
                                                        // already got chat going on
                                                        dialog = context.chatMap.get(msg.name);
                                                    } else {
                                                        // new chat
                                                        dialog = (new AlertDialogHelper(context)).popupChat(u);
                                                        context.chatMap.put(msg.name, dialog);
                                                    }
                                                    dialog.show();
                                                }
                                            }
                                        })
                                );
                            }
                        } catch (Exception e) {
                            Log.e(LiveOkeRemoteApplication.TAG, e.getMessage(), e);
                        }
                    }
                }
            } else {
                // Process LiveOke Msg here
                context.liveOkeUDPClient.liveOkeIPAddress = senderIP;
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
                    Log.d(LiveOkeRemoteApplication.TAG,"Server Master Code = " + code);
                    if (!code.equalsIgnoreCase("")) {
                        context.serverMasterCode = senderMSG.substring(11, senderMSG.length());
                    }
                } else if (senderMSG.startsWith("Track:")) {
                    String nextTrack = senderMSG.substring(6, senderMSG.length());
                    if (nextTrack.equalsIgnoreCase("Karaoke")) {
                        // assuming current is music
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                context.floatingButtonsHelper.micOff();
                            }
                        });
                    } else {
                        // current is Karaoke
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                context.floatingButtonsHelper.micOn();
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
                            if (currentAudioTrack.equalsIgnoreCase("Karaoke")) {
                                context.floatingButtonsHelper.micOn();
                            } else {
                                context.floatingButtonsHelper.micOff();
                            }
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
                        }
                    });
                } else if (senderMSG.startsWith("Play:")) {
                    final String currentAudioTrack = senderMSG.substring(5, senderMSG.length());
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            context.nowPlayingHelper.setTitle("Now Playing:<br><b>" + context.liveOkeUDPClient.currentSong + "</b>");
                            context.floatingButtonsHelper.togglePlayBtn();
                            if (currentAudioTrack.equalsIgnoreCase("Karaoke")) {
                                context.floatingButtonsHelper.micOn();
                            } else {
                                context.floatingButtonsHelper.micOff();
                            }
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
                                                    Log.v(LiveOkeRemoteApplication.TAG, "Found requester on friendlist!");
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
                                Log.e(LiveOkeRemoteApplication.TAG, ex.getMessage(), ex);
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
                        }
                    });
                }
            }
    }
}
