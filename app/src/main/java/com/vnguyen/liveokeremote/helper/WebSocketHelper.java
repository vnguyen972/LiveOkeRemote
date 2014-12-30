package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.R;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.db.SongListDataSource;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WebSocketHelper {
    private WebSocketClient mWebSocketClient;
    private MainActivity context;
    private URI uri;
    private ArrayList<String> songRawDataList;
    private ArrayList<Song> songs;
    private SongListDataSource db;
    public boolean doneGettingSongList;
    public boolean gotTotalSongResponse;

    public WebSocketHelper(Context context) {
        this.context = (MainActivity) context;
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
                Log.i(context.app.TAG, "Websocket: Opened");
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
                Log.i(context.app.TAG,"Websocket: Message - " + message);
                processMessage(message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.i(context.app.TAG,"Websocket: Closed " + reason);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SwitchCompat onOffSwitch = (SwitchCompat) context.onOffSwitch.getActionView().findViewById(R.id.switchForActionBar);
                        if (onOffSwitch.isChecked()) {
                            onOffSwitch.toggle();
                            SnackbarManager.show(Snackbar.with(context)
                                    .type(SnackbarType.MULTI_LINE)
                                    .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                                    .textColor(Color.WHITE)
                                    .text("Disconnected!"));
                        }
                    }
                });
            }

            @Override
            public void onError(Exception ex) {
                final String errMsg = ex.getMessage();
                Log.i(context.app.TAG,"Websocket: Error " + ex.getMessage());
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

    private void processMessage(String message) {
        if (message.startsWith("Songlist:")) {
            String songData = message.substring(9, message.length());
            songRawDataList.add(songData);
        } else if (message.startsWith("totalsong:")) {
            context.totalSong = Integer.parseInt(message.substring(10, message.length()));
            songRawDataList = new ArrayList<>();
            gotTotalSongResponse = true;
        } else if (message.startsWith("Finish")) {
            // done receiving songs list
            try {
                ExecutorService executor = Executors.newFixedThreadPool(2);
                int cpus = Runtime.getRuntime().availableProcessors();
                int maxThreads = cpus * 2;
                maxThreads = (maxThreads > 0 ? maxThreads : 1);
                Log.d(context.app.TAG, "CPUs: " + cpus);
                Log.d(context.app.TAG, "Max Thread: " + maxThreads);
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
                    if (songs == null) {
                        songs = new ArrayList<Song>();
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
            Log.e(context.app.TAG,e.getMessage(),e);
            throw new Exception(e);
        }
    }

}
