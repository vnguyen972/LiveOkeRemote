package com.vnguyen.liveokeremote;

import android.app.ProgressDialog;
import android.content.Context;

import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.db.SongListDataSource;
import com.vnguyen.liveokeremote.helper.LogHelper;
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
    private ArrayList<String> songRawDataList;
    private MainActivity context;
    private ProgressDialog pd;
    public SongListDataSource db;
    public Exception exception;
    public int bytesRead;

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
                        LogHelper.i("Connected to LiveOke-TCP.");
                        byteArrayOutputStream =
                                new ByteArrayOutputStream(socket.getReceiveBufferSize());
                        byte[] buffer = new byte[socket.getReceiveBufferSize()];
                        inputStream = socket.getInputStream();
                        OutputStream outputStream = socket.getOutputStream();
                        PrintStream printStream = new PrintStream(outputStream);
                        // after connect, LiveOke sends "totalbytes"
                        bytesRead = inputStream.read(buffer);
                        byteArrayOutputStream.write(buffer, 0, bytesRead);
                        byteArrayOutputStream.flush();
                        response = byteArrayOutputStream.toString("UTF-8");
                        LogHelper.i("RESPONSE = " + response);
                        onReceived(response);
                        printStream.print("getsong");
                        printStream.flush();
                        // clear out the outputstream array
                        byteArrayOutputStream.reset();
                        int i = 0;
                        buffer = new byte[1024*1024*5];
                        response = "";
                        // keep reading the stream until the end
                        int totalBytesReadSoFar = 0;
                        int percentage = 0;
                        while (true) {
                            //LogHelper.i("*** START READING ***");
                            bytesRead = inputStream.read(buffer);
//                            LogHelper.i("*** BYTES READ = " + bytesRead);
                            totalBytesReadSoFar += bytesRead;
//                            LogHelper.i("*** TOTAL BYTES READ = " + totalBytesReadSoFar);
                            byteArrayOutputStream.write(buffer, 0, bytesRead);
                            response += byteArrayOutputStream.toString("UTF-8");
                            byteArrayOutputStream.reset();
                            percentage = totalBytesReadSoFar * 100 / context.totalBytes;
                            LogHelper.i(percentage + "% ...");
                            pd.incrementSecondaryProgressBy(percentage);
                            if (totalBytesReadSoFar == context.totalBytes) {
                                break;
                            }
                        }
                        // response string will be a string with delimiter |
                        // parse it and feed it into a processor
                        StringTokenizer stok = new StringTokenizer(response,"|");
                        //LogHelper.i("Total Tokens = " + stok.countTokens());
                        context.totalSong = stok.countTokens();
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setMessage("Total song: " + context.totalSong);
                            }
                        });
                        context.liveOkeUDPClient.gotTotalSongResponse = true;
                        while (stok.hasMoreTokens()) {
                            String rawSong = stok.nextToken().trim();
                            if (!rawSong.startsWith("Finish")) {
                                songRawDataList.add(rawSong);
                            }
                            //pd.incrementProgressBy(1);
                        }
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.setMessage("Processing...");

                            }
                        });
                        onReceived("Finish");
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
                                LogHelper.i("Closing the socket...");
                                socket.close();
                                byteArrayOutputStream.close();
                                inputStream.close();
                            } catch (IOException e) {
                                onErrored(e);
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
        if (message.startsWith("Songlist:")) {
            // this is no longer valid, LiveOke is no longer sending datat
            // that starts with "Songlist:" anymore (2/2015)
            String songData = message.substring(9, message.length());
            songRawDataList.add(songData);
        } else if (message.startsWith("totalsong:")) {
            context.totalBytes = Integer.parseInt(message.substring(10, message.length()));
            //context.totalSong = Integer.parseInt(message.substring(10, message.length()));
            songRawDataList = new ArrayList<>();
            //context.liveOkeUDPClient.gotTotalSongResponse = true;
        } else if (message.startsWith("Finish")) {
            // done receiving songs list
            // now it's time to process them
            ExecutorService executor = null;
            try {
                if (context.liveOkeUDPClient.songs != null && !context.liveOkeUDPClient.songs.isEmpty()) {
                    context.liveOkeUDPClient.songs.clear();
                }
                int cpus = Runtime.getRuntime().availableProcessors();
                int maxThreads = cpus * 2;
                maxThreads = (maxThreads > 0 ? maxThreads : 1);
                LogHelper.i("CPUs: " + cpus);
                LogHelper.i("Max Thread: " + maxThreads);
                LogHelper.i("Total RAW = " + songRawDataList.size());
                executor = new ThreadPoolExecutor(
                        cpus, // core thread pool size
                        maxThreads, // maximum thread pool size
                        40, // time to wait before resizing pool
                        TimeUnit.SECONDS,
                        new ArrayBlockingQueue<Runnable>(maxThreads, false),
                        new ThreadPoolExecutor.CallerRunsPolicy());
                CompletionService<Song> pool = new ExecutorCompletionService<>(executor);
                context.totalSong = songRawDataList.size();
                for (final String rawData : songRawDataList) {
                    pool.submit(new Callable<Song>() {
                        @Override
                        public Song call() throws Exception {
                            pd.incrementProgressBy(1);
                            final Song song = SongHelper.buildSong(rawData);
                            return song;
                        }
                    });
                }
                // process the result from the threads
                int mSize = songRawDataList.size();
                for (int i = 0; i < mSize; i++) {
                    final Song song = pool.take().get();
                    if (context.liveOkeUDPClient.songs == null) {
                        context.liveOkeUDPClient.songs = new ArrayList<>();
                    }
                    context.liveOkeUDPClient.songs.add(song);
                }
                executor.shutdown();
                while (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                }
                if (context.liveOkeUDPClient.songs != null && !context.liveOkeUDPClient.songs.isEmpty()) {
                    LogHelper.i("TOTAL SONG = " + context.liveOkeUDPClient.songs.size());
                    insertDBNow(context.liveOkeUDPClient.songs);
                }
                executor = null;
                pool = null;

                if (db != null) {
                    db.saveDB();
                }
                songRawDataList.clear();
                // now update the display
            } catch (Exception ex) {
                LogHelper.e(ex.getMessage(), ex);
                exception = ex;
            } finally {
                if (executor != null) {
                    executor.shutdown();
                    try {
                        while (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        context.getPagerTitles();
                        context.updateMainDisplay();
                    }
                });
                context.liveOkeUDPClient.doneGettingSongList = true;
            }
        }
    }

    @Override
    public void onErrored(final Exception exception) {
        LogHelper.e(exception.getMessage(),exception);
        context.liveOkeUDPClient.doneGettingSongList = true;
        this.exception = exception;
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
            LogHelper.e(e.getMessage(),e);
            throw new Exception(e);
        }
    }
}
