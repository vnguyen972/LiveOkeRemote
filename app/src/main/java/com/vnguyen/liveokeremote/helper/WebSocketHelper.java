package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.data.User;
import com.vnguyen.liveokeremote.db.SongListDataSource;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Deprecated
public class WebSocketHelper {
    private WebSocketClient mWebSocketClient;
    private MainActivity context;
    private URI uri;
    private ArrayList<String> songRawDataList;
    private ArrayList<Song> songs;
    private SongListDataSource db;
    public boolean doneGettingSongList;
    public boolean gotTotalSongResponse;
    public boolean updatingRsvpList;
    public String currentSong;
    public ArrayList<ReservedListItem> rsvpList;

    public WebSocketHelper(Context context) {
        this.context = (MainActivity) context;
        this.rsvpList = new ArrayList<>();
//        if (this.context.wsInfo == null || this.context.wsInfo.ipAddress.equals("")) {
//            this.context.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    (new AlertDialogHelper(WebSocketHelper.this.context)).
//                            popupIPAddressDialogGeneric();
//                }
//            });
//        }
    }

    public void init(URI uri) {
        //uri = URI.create("ws://" + this.context.ipAddress + ":8181");
        uri = URI.create(context.wsInfo.uri);
        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i(LiveOkeRemoteApplication.TAG, "Websocket: Opened");
//                final SwitchCompat onOffSwitch = (SwitchCompat) context.onOffSwitch.getActionView().findViewById(R.id.switchForActionBar);
//                if (!onOffSwitch.isChecked()) {
//                    context.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            onOffSwitch.toggle();
//                        }
//                    });
//                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SnackbarManager.show(Snackbar.with(context)
                                .type(SnackbarType.MULTI_LINE)
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .textColor(Color.WHITE)
                                .text("Connected @ " + context.wsInfo.ipAddress));
                    }
                });
            }

            @Override
            public void onMessage(String message) {
                Log.i(LiveOkeRemoteApplication.TAG,"Websocket: Message - " + message);
                processMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(LiveOkeRemoteApplication.TAG,"Websocket: Closed " + reason);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        SwitchCompat onOffSwitch = (SwitchCompat) context.onOffSwitch.getActionView().findViewById(R.id.switchForActionBar);
//                        if (onOffSwitch.isChecked()) {
//                            onOffSwitch.toggle();
//                        }
                        FloatingActionButton playButton = (FloatingActionButton) context.findViewById(R.id.playBtn);
                        if (playButton.getTag().equals("PAUSE")) {
                            final IconDrawable pauseBtnIcon = new IconDrawable(context, Iconify.IconValue.md_pause);
                            pauseBtnIcon.sizeDp(40);
                            pauseBtnIcon.colorRes(R.color.orange_800);
                            playButton.setImageDrawable(pauseBtnIcon);
                            playButton.setTag("PLAY");
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    context.nowPlayingHelper.popTitle();
                                }
                            });
                        }
                        //context.webSocketHelper.rsvpList.clear();
                        context.liveOkeUDPClient.rsvpList.clear();
                        //context.rsvpPanelHelper.refreshRsvpList(context.webSocketHelper.rsvpList);
                        context.rsvpPanelHelper.refreshRsvpList(context.liveOkeUDPClient.rsvpList);
                        SnackbarManager.show(Snackbar.with(context)
                                .type(SnackbarType.MULTI_LINE)
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .textColor(Color.WHITE)
                                .text("Disconnected!"));
                        mWebSocketClient = null;
                        }
                });
            }

            @Override
            public void onError(Exception ex) {
                final String errMsg = ex.getMessage();
                Log.i(LiveOkeRemoteApplication.TAG,"Websocket: Error " + ex.getMessage());
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SnackbarManager.show(Snackbar.with(context)
                                .type(SnackbarType.MULTI_LINE)
                                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                .textColor(Color.WHITE)
                                .color(Color.RED)
                                .text("ERROR: " + errMsg));
                    }
                });
                mWebSocketClient = null;
            }
        };
    }
    public void connect() {
        init(uri);
        mWebSocketClient.connect();
    }

    public void disconnect() {
        mWebSocketClient.close();
    }

    public void sendMessage(String message) {

        mWebSocketClient.send(message);
    }

    public boolean isConnected() {
        return (mWebSocketClient.getReadyState() == WebSocket.READYSTATE.OPEN);
    }

    private void processMessage(final String message) {
        if (message.startsWith("MasterCode:")) {
            String code = message.substring(11, message.length());
            if (!code.equalsIgnoreCase("")) {
                context.serverMasterCode = message.substring(11, message.length());
            }
        } else if (message.startsWith("Songlist:")) {
            String songData = message.substring(9, message.length());
            songRawDataList.add(songData);
        } else if (message.startsWith("totalsong:")) {
            context.totalSong = Integer.parseInt(message.substring(10, message.length()));
            songRawDataList = new ArrayList<>();
            gotTotalSongResponse = true;
        } else if (message.startsWith("Track:")) {
            String nextTrack = message.substring(6, message.length());
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
        } else if (message.startsWith("Pause:")) {
            final String currentAudioTrack = message.substring(6, message.length());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.nowPlayingHelper.pushTitle("Pause:<br><b>" + currentSong+"</b>");
                    context.floatingButtonsHelper.togglePlayBtn();
                    if (currentAudioTrack.equalsIgnoreCase("Karaoke")) {
                        context.floatingButtonsHelper.micOn();
                    } else {
                        context.floatingButtonsHelper.micOff();
                    }
                }
            });
        } else if (message.startsWith("Play:")) {
            final String currentAudioTrack = message.substring(5, message.length());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.nowPlayingHelper.setTitle("Now Playing:<br><b>"+currentSong+"</b>");
                    context.floatingButtonsHelper.togglePlayBtn();
                    if (currentAudioTrack.equalsIgnoreCase("Karaoke")) {
                        context.floatingButtonsHelper.micOn();
                    } else {
                        context.floatingButtonsHelper.micOff();
                    }
                }
            });
        } else if (message.startsWith("Playing:")) {
            currentSong = message.substring(8, message.length());
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.nowPlayingHelper.setTitle("Current Song:<br><b>" + currentSong + "</b>");
                }
            });
        } else if (message.startsWith("Reserve:")) {
            if (rsvpList != null) {
                rsvpList.clear();
            }
            String msgList = message.substring(8, message.length());
            StringTokenizer stok = new StringTokenizer(msgList," ");
            while (stok.hasMoreTokens()) {
                updatingRsvpList = true;
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
                                            Log.v(LiveOkeRemoteApplication.TAG,"Found requester on friendlist!");
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
                            rsvpList.add(rsvpItem);
//                            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
//
//                                @Override
//                                protected Void doInBackground(Void... params) {
//                                    if (u.avatar == null) {
//                                        u.avatar = (new DrawableHelper()).buildDrawable(u.name.substring(0, 1), "round");
//                                    }
//                                    return null;
//                                }
//
//                                @Override
//                                protected void onPostExecute(Void aVoid) {
//                                    final ReservedListItem rsvpItem = new ReservedListItem(u, song.title, song.icon, songID);
//                                    rsvpList.add(rsvpItem);
//                                }
//                            };
//                            task.execute((Void[])null);
                        }
                    } catch (Exception ex) {
                        Log.e(LiveOkeRemoteApplication.TAG,ex.getMessage(),ex);
                    } finally {
                        context.db.close();
                    }
                }
            }
            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    context.rsvpPanelHelper.refreshRsvpList(rsvpList);
                    context.updateRsvpCounter(rsvpList.size());
                }
            });
            updatingRsvpList = false;
        } else if (message.startsWith("Finish")) {
            // done receiving songs list
            try {
                if (songs != null && !songs.isEmpty()) {
                    songs.clear();
                }
                ExecutorService executor;
                int cpus = Runtime.getRuntime().availableProcessors();
                int maxThreads = cpus * 2;
                maxThreads = (maxThreads > 0 ? maxThreads : 1);
                Log.d(LiveOkeRemoteApplication.TAG, "CPUs: " + cpus);
                Log.d(LiveOkeRemoteApplication.TAG, "Max Thread: " + maxThreads);
                executor = new ThreadPoolExecutor(
                        cpus, // core thread pool size
                        maxThreads, // maximum thread pool size
                        40, // time to wait before resizing pool
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(maxThreads, false),
                        new ThreadPoolExecutor.CallerRunsPolicy());
                CompletionService<Song> pool = new ExecutorCompletionService<>(executor);
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
                    if (songs == null) {
                        songs = new ArrayList<>();
                    }
                    songs.add(song);
                }
                executor.shutdown();
                while (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                }
                songRawDataList = null;
                if (songs != null && !songs.isEmpty()) {
                    insertDBNow(songs);
                }
                executor = null;
                pool = null;
                doneGettingSongList = true;
            } catch (Exception ex) {
                ex.printStackTrace();
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
            Log.e(LiveOkeRemoteApplication.TAG,e.getMessage(),e);
            throw new Exception(e);
        }
    }

}
