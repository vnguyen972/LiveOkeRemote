package com.vnguyen.liveokeremote;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.db.SongListDataSource;
import com.vnguyen.liveokeremote.helper.SongHelper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SongListRetriever implements  LiveOkeTCPClient {
    public boolean doneGettingData;
    public boolean invalidConnection = true;
    private ArrayList<String> songRawDataList;
    private MainActivity context;
    public SongListDataSource db;
    private ProgressDialog pd;

    public SongListRetriever(Context context, ProgressDialog pd) {
        this.context = (MainActivity) context;
        this.pd = pd;
    }

    public void getSongList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (context.liveOkeUDPClient != null &&
                        context.liveOkeUDPClient.liveOkeIPAddress != null &&
                        !context.liveOkeUDPClient.liveOkeIPAddress.equals("")) {
                    doneGettingData = false;
                    Socket socket = null;
                    ByteArrayOutputStream byteArrayOutputStream = null;
                    InputStream inputStream = null;
                    String response = "";

                    try {
                        // first send an UDP command "getsonglistTCP" to LiveOke
                        context.liveOkeUDPClient.sendMessage("getsonglistTCP",
                                context.liveOkeUDPClient.liveOkeIPAddress,
                                context.liveOkeUDPClient.LIVEOKE_UDP_PORT);
                        // that would trigger LiveOke to switch TCP server on
                        // we then connect to it.
                        socket = new Socket(context.liveOkeUDPClient.liveOkeIPAddress, SERVER_TCP_PORT);
                        socket.setKeepAlive(false);
                        Log.v(LiveOkeRemoteApplication.TAG, "Connected to LiveOke-TCP.");
                        byteArrayOutputStream =
                                new ByteArrayOutputStream(socket.getReceiveBufferSize());
                        byte[] buffer = new byte[socket.getReceiveBufferSize()];
                        int bytesRead;
                        inputStream = socket.getInputStream();
                        OutputStream outputStream = socket.getOutputStream();
                        PrintStream printStream = new PrintStream(outputStream);
                        // after connect, LiveOke sends "totalsongs"
                        bytesRead = inputStream.read(buffer);
                        invalidConnection = false;
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        byteArrayOutputStream.flush();
                        response = byteArrayOutputStream.toString("UTF-8");
                        Log.v(LiveOkeRemoteApplication.TAG,"RESPONSE = " + response);
                        onReceived(response);
                        printStream.print("getsong");
                        printStream.flush();
                        // clear out the outputstream array
                        byteArrayOutputStream.reset();
                        int i = 0;
                        buffer = new byte[1024*1024];
                        response = "";
                        // keep reading the stream until the end
                        while (true) {
                            Log.d(LiveOkeRemoteApplication.TAG, "*** START READING ***");
                            bytesRead = inputStream.read(buffer);
                            Log.d(LiveOkeRemoteApplication.TAG, "*** BYTES READ = " + bytesRead);
                            if (bytesRead == -1) {
                                break;
                            }
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                            response += byteArrayOutputStream.toString("UTF-8");
                            // clear out the outputstream array
                            // send 'getsong' until we get everything
                            byteArrayOutputStream.reset();
                            printStream.print("getsong");
                            printStream.flush();
                            pd.incrementSecondaryProgressBy(1);
                        }
                        //final String res = response;
                        //Log.d(LiveOkeRemoteApplication.TAG,"RESPONSE = " + i + " - " + res);

                        // response string will be a string with delimiter |
                        // parse it and feed it into a processor
                        StringTokenizer stok = new StringTokenizer(response,"|");
                        Log.d(LiveOkeRemoteApplication.TAG,"Total Tokens = " + stok.countTokens());
                        while (stok.hasMoreTokens()) {
                            String rawSong = stok.nextToken().trim();
                            //Log.d(LiveOkeRemoteApplication.TAG,rawSong);
                            if (!rawSong.startsWith("Finish")) {
                                songRawDataList.add(rawSong);
                            } else {
                                onReceived(rawSong);
                            }
                            //pd.incrementProgressBy(1);
                        }
                    } catch (SocketException e) {
                        onErrored(e);
                    } catch (SocketTimeoutException e) {
                        onErrored(e);
                    } catch (UnknownHostException e) {
                        onErrored(e);
                    } catch (IOException e) {
                        onErrored(e);
                    } finally {
                        if (socket != null) {
                            try {
                                Log.v(LiveOkeRemoteApplication.TAG, "Closing the socket...");
                                //socket.shutdownInput();
                                //socket.shutdownOutput();
                                socket.close();
                                byteArrayOutputStream.close();
                                inputStream.close();
                                doneGettingData = true;
                                invalidConnection = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    onErrored(new Exception("Server Host is null or empty"));
                }
            }
        }).start();
    }

    @Override
    public void onReceived(String message) {
        //Log.v(LiveOkeRemoteApplication.TAG,"message = " + message);
        if (message.startsWith("Songlist:")) {
            // this is no longer valid, LiveOke is no longer sending datat
            // that starts with "Songlist:" anymore (2/2015)
            String songData = message.substring(9, message.length());
            songRawDataList.add(songData);
        } else if (message.startsWith("totalsong:")) {
            context.totalSong = Integer.parseInt(message.substring(10, message.length()));
            songRawDataList = new ArrayList<>();
            context.liveOkeUDPClient.gotTotalSongResponse = true;
        } else if (message.startsWith("Finish")) {
            // done receiving songs list
            // now it's time to process them
            try {
                if (context.liveOkeUDPClient.songs != null && !context.liveOkeUDPClient.songs.isEmpty()) {
                    context.liveOkeUDPClient.songs.clear();
                }
                ExecutorService executor;
                int cpus = Runtime.getRuntime().availableProcessors();
                int maxThreads = cpus * 2;
                maxThreads = (maxThreads > 0 ? maxThreads : 1);
                Log.d(LiveOkeRemoteApplication.TAG, "CPUs: " + cpus);
                Log.d(LiveOkeRemoteApplication.TAG, "Max Thread: " + maxThreads);
                Log.d(LiveOkeRemoteApplication.TAG,"Total RAW = " + songRawDataList.size());
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
                            pd.incrementProgressBy(1);
                            return SongHelper.buildSong(rawData);
                        }
                    });
                }
                // process the result from the threads
                int mSize = songRawDataList.size();
                for (int i = 0; i < mSize; i++) {
                    Song song = pool.take().get();
                    if (context.liveOkeUDPClient.songs == null) {
                        context.liveOkeUDPClient.songs = new ArrayList<>();
                    }
                    context.liveOkeUDPClient.songs.add(song);
                }
                executor.shutdown();
                while (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                }
                if (context.liveOkeUDPClient.songs != null && !context.liveOkeUDPClient.songs.isEmpty()) {
                    Log.v(LiveOkeRemoteApplication.TAG,"TOTAL SONG = " + context.liveOkeUDPClient.songs.size());
                    insertDBNow(context.liveOkeUDPClient.songs);
                }
                executor = null;
                pool = null;
                context.liveOkeUDPClient.doneGettingSongList = true;
                if (db != null) {
                    db.saveDB();
                }
                songRawDataList.clear();
                // now update the display
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        context.getPagerTitles();
                        context.updateMainDisplay();
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onErrored(final Exception exception) {
        Log.e(LiveOkeRemoteApplication.TAG,exception.getMessage(),exception);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SnackbarManager.show(Snackbar.with(context)
                        .type(SnackbarType.MULTI_LINE)
                        .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                        .textColor(Color.WHITE)
                        .color(Color.RED)
                        .text("ERROR: " + exception.getMessage()));
            }
        });
    }

    public void insertDBNow(ArrayList<Song> songsList) throws Exception {
        try {
            db = new SongListDataSource(context);
            db.open();
            db.getDbHelper().resetDB(db.getDatabase());
            db.insertAll(songsList);
            db.close();
            //db = null;
        } catch (Exception e) {
            Log.e(LiveOkeRemoteApplication.TAG,e.getMessage(),e);
            throw new Exception(e);
        }
    }
}
