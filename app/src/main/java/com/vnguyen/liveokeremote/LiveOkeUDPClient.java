package com.vnguyen.liveokeremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.service.UDPListenerService;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;

public abstract class LiveOkeUDPClient extends BroadcastReceiver {
    public static int LIVEOKE_UDP_PORT = 8888;


    private MainActivity context;
    private UDPListenerService udpListenerService;
    public String liveOkeIPAddress;
    public int pingCount = 0;
    public String currentSong;
    public boolean gotTotalSongResponse;
    public ArrayList<ReservedListItem> rsvpList;
    public ArrayList<Song> songs;
    public boolean doneGettingSongList;


    LiveOkeUDPClient(UDPListenerService udpListenerService,Context context) {
        this.context = (MainActivity) context;
        this.udpListenerService = udpListenerService;
        rsvpList = new ArrayList<>();
        initClient();
    }

    private void initClient() {
//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
                if (udpListenerService != null) {
                    // if udp listener service bound (we have access to it)
                    // then go look for LiveOke instance running on the network
                    udpListenerService.sendMessage("WhoYouAre", null, LiveOkeUDPClient.LIVEOKE_UDP_PORT);
                }
//                return null;
//            }
//        };
//        task.execute((Void[])null);
    }

    public void sendMessage(final String sendMsg, final String ipAddress, final int port) {
//        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... params) {
                if (udpListenerService != null) {
                    udpListenerService.sendMessage(sendMsg, ipAddress, port);
                }
//                return null;
//            }
//        };
//        task.execute((Void[])null);
    }

    public String getMyIP() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e(LiveOkeRemoteApplication.TAG, "Unable to get host address:" + ex.getMessage(), ex);
            ipAddressString = null;
        }

        return ipAddressString;
    }

}
