package com.vnguyen.liveokeremote.helper;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.LiveOkeRemoteBroadcastMsg;
import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.data.User;
import com.vnguyen.liveokeremote.db.SongListDataSource;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UDPResponseHelper {
    private MainActivity context;
    private ArrayList<String> songRawDataList;
    private SongListDataSource db;

    public UDPResponseHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void processResponseIntent(Intent intent) {
        final String senderIP = intent.getStringExtra("senderIP");
        final int senderPORT = intent.getIntExtra("senderPORT", 0);
        final String senderMSG = intent.getStringExtra("message");
        Log.v(LiveOkeRemoteApplication.TAG, "Received from: " + senderIP + ":" + senderPORT);
        Log.v(LiveOkeRemoteApplication.TAG, "Received msg: " + senderMSG);
        // would this help?
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                   processMessage(senderIP, senderPORT, senderMSG);
                return null;
            }
        };
        task.execute((Void[])null);
    }

    public void processMessage(String senderIP, int senderPORT, String senderMSG) {
        if (senderMSG.startsWith("{")) {
            // Message is a JSON message
            final LiveOkeRemoteBroadcastMsg msg = (new Gson()).fromJson(senderMSG, LiveOkeRemoteBroadcastMsg.class);
            // if the message coming from this app
            if (msg.from.equalsIgnoreCase(context.getResources().getString(R.string.app_name))) {
                // is it really from another client with different IP?
                if (!senderIP.equals(context.liveOkeUDPClient.getMyIP())) {
                    try {
                        if (msg.greeting.equalsIgnoreCase("Hi")) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SnackbarManager.show(Snackbar.with(context)
                                            .type(SnackbarType.MULTI_LINE)
                                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                            .textColor(Color.WHITE)
                                            .color(context.getResources().getColor(R.color.indigo_500))
                                            .text(msg.name + " is online!"));
                                }
                            });
                        } else if (msg.greeting.equalsIgnoreCase("Bye")) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SnackbarManager.show(Snackbar.with(context)
                                            .type(SnackbarType.MULTI_LINE)
                                            .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                            .textColor(Color.WHITE)
                                            .color(context.getResources().getColor(R.color.indigo_500))
                                            .text(msg.name + " is offline!"));
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e(context.app.TAG, e.getMessage(), e);
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
            } else if (senderMSG.equalsIgnoreCase("MasterCode:")) {
                String code = senderMSG.substring(11, senderMSG.length());
                if (code != null && !code.equalsIgnoreCase("")) {
                    context.serverMasterCode = senderMSG.substring(11, senderMSG.length());
                }
            } else if (senderMSG.startsWith("Songlist:")) {
                String songData = senderMSG.substring(9, senderMSG.length());
                songRawDataList.add(songData);
            } else if (senderMSG.startsWith("totalsong:")) {
                context.totalSong = Integer.parseInt(senderMSG.substring(10, senderMSG.length()));
                songRawDataList = new ArrayList<>();
                context.liveOkeUDPClient.gotTotalSongResponse = true;
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
                        context.nowPlayingHelper.pushTitle("Pause:<br><b>" + context.liveOkeUDPClient.currentSong+"</b>");
                        context.floatingButtonsHelper.togglePlayBtn();
                        if (currentAudioTrack.equalsIgnoreCase("Karaoke")) {
                            context.floatingButtonsHelper.micOn();
                        } else {
                            context.floatingButtonsHelper.micOff();
                        }
                    }
                });
            } else if (senderMSG.equalsIgnoreCase("Quit")) {
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
                StringTokenizer stok = new StringTokenizer(msgList," ");
                while (stok.hasMoreTokens()) {
                    StringTokenizer reqTok = new StringTokenizer(stok.nextToken(),".");
                    if (reqTok.hasMoreTokens()) {
                        final String songID = reqTok.nextToken();
                        String requester = reqTok.nextToken().replace("_"," ");
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
                                                Log.v(context.app.TAG,"Found requester on friendlist!");
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
                            Log.e(context.app.TAG,ex.getMessage(),ex);
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
            } else if (senderMSG.startsWith("Finish")) {
                // done receiving songs list
                try {
                    if (context.liveOkeUDPClient.songs != null && !context.liveOkeUDPClient.songs.isEmpty()) {
                        context.liveOkeUDPClient.songs.clear();
                    }
                    ExecutorService executor = Executors.newFixedThreadPool(2);
                    int cpus = Runtime.getRuntime().availableProcessors();
                    int maxThreads = cpus * 2;
                    maxThreads = (maxThreads > 0 ? maxThreads : 1);
                    Log.d(context.app.TAG, "CPUs: " + cpus);
                    Log.d(context.app.TAG, "Max Thread: " + maxThreads);
                    Log.d(context.app.TAG,"Total Raw Songs: " + songRawDataList.size());
                    executor = new ThreadPoolExecutor(
                            cpus, // core thread pool size
                            maxThreads, // maximum thread pool size
                            40, // time to wait before resizing pool
                            TimeUnit.SECONDS,
                            new ArrayBlockingQueue<Runnable>(maxThreads, false),
                            new ThreadPoolExecutor.CallerRunsPolicy());
                    CompletionService<Song> pool = new ExecutorCompletionService<Song>(executor);
                    for (final String rawData : songRawDataList) {
                        pool.submit(new Callable<Song>() {
                            @Override
                            public Song call() throws Exception {
                                return SongHelper.buildSong(rawData);
                            }
                        });
                    }
                    // process the result from the threads
                    int mSize = songRawDataList.size();
                    for (int i = 0; i < mSize; i++) {
                        Song song = pool.take().get();
                        if (context.liveOkeUDPClient.songs == null) {
                            context.liveOkeUDPClient.songs = new ArrayList<Song>();
                        }
                        context.liveOkeUDPClient.songs.add(song);
                    }
                    executor.shutdown();
                    //while (!executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
                    while (!executor.isTerminated()) {
                    }
                    songRawDataList = null;
                    if (context.liveOkeUDPClient.songs != null && !context.liveOkeUDPClient.songs.isEmpty()) {
                        insertDBNow(context.liveOkeUDPClient.songs);
                    }
                    executor = null;
                    pool = null;
                    context.liveOkeUDPClient.doneGettingSongList = true;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    public void insertDBNow(ArrayList<Song> songsList) throws Exception {
        try {
            db = new SongListDataSource(context);
            db.open();
            db.getDbHelper().resetDB(db.getDatabase());
            db.insertAll(songsList);
            db.close();
            db = null;
        } catch (Exception e) {
            Log.e(context.app.TAG,e.getMessage(),e);
            throw new Exception(e);
        }
    }
}
