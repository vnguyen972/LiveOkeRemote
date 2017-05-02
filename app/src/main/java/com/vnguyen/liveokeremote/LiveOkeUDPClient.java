package com.vnguyen.liveokeremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import com.vnguyen.liveokeremote.data.ReservedListItem;
import com.vnguyen.liveokeremote.data.Song;
import com.vnguyen.liveokeremote.helper.LogHelper;
import com.vnguyen.liveokeremote.helper.PreferencesHelper;
import com.vnguyen.liveokeremote.service.UDPListenerService;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Enumeration;

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
        doneGettingSongList = true;
        rsvpList = new ArrayList<>();
        liveOkeIPAddress = PreferencesHelper.getInstance(this.context).getPreference("ipAddress");
        initClient();
    }

    public void initClient() {
        if (udpListenerService != null) {
            // if udp listener service bound (we have access to it)
            // then go look for LiveOke instance running on the network
            udpListenerService.sendMessage("WhoYouAre", liveOkeIPAddress, LiveOkeUDPClient.LIVEOKE_UDP_PORT);
        }
    }

    public void sendMessage(final String sendMsg, final String ipAddress, final int port) {
        if (udpListenerService != null) {
            udpListenerService.sendMessage(sendMsg, ipAddress, port);
        }
    }

    public boolean isMine(String senderIP) {
        return senderIP.equals(getMyIP());
    }


    public String getMyIP() {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        String ipAddress = "";
        if (wifi.isAvailable() && wifi.isConnected()) {
            ipAddress = getMyIPWIFI();
        } else if (mobile.isAvailable()) {
            ipAddress = GetLocalIpAddress();
        } else {
        }
        return ipAddress;
    }

    private String GetLocalIpAddress() {
        String ip = "";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ip = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            LogHelper.e(ex.getMessage(),ex);
        }
        return ip;
    }

    public String getMyIPWIFI() {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        // Convert little-endian to big-endianif needed
        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString = "";
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            LogHelper.e("Unable to get host address:" + ex.getMessage(), ex);
            ipAddressString = "";
        }

        return ipAddressString;
    }

}
