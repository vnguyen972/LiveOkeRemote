package com.vnguyen.liveokeremote;

import android.content.Context;
import android.util.Log;

import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.db.SongListDataSource;
import com.vnguyen.liveokeremote.helper.SongHelper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SongListWorker implements SongListListener {
    private String ip;
    private int port;
    private MainActivity context;
    private ArrayList<String> songRawDataList;
    private SongListDataSource db;
    private boolean stopListen = false;

    public SongListWorker(Context context) {
        this.context = (MainActivity) context;
    }

    @Override
    public void onReceived(String senderMSG) {

        if (senderMSG.startsWith("totalsong:")) {
            context.totalSong = Integer.parseInt(senderMSG.substring(10, senderMSG.length()));
            songRawDataList = new ArrayList<>();
            context.liveOkeUDPClient.gotTotalSongResponse = true;
        } else if (senderMSG.startsWith("Songlist:")) {
            String songData = senderMSG.substring(9, senderMSG.length());
            songRawDataList.add(songData);
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
            stopListen = true;
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

    public void getSongList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket c = null;
                try {
                    ip = context.liveOkeUDPClient.liveOkeIPAddress;
                    port = context.liveOkeUDPClient.LIVEOKE_UDP_PORT;
                    c = new DatagramSocket();
                    byte[] sendData = "getsonglist".getBytes();
                    // Send the broadcast package!
                    InetAddress address = InetAddress.getByName(ip);
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
                        c.send(sendPacket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    System.out.println(">>> Done sending. Now waiting for a reply!");

                    //Wait for a response
                    c.setSoTimeout(30000);
                    String message = "";
                    while (!stopListen) {
                        byte[] recvBuf = new byte[15000];
                        final DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                        c.receive(receivePacket);
                        //message = new String(receivePacket.getData(), "UTF-8").trim();

                        //We have a response
                        onReceived(new String(receivePacket.getData()).trim());
                    }

                } catch (Exception x) {
                    x.printStackTrace();
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
        }).start();
    }
}
