package com.vnguyen.liveokeremote.helper;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.data.LiveOkeSocketInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPHelper {

    private MainActivity context;
    public UDPHelper(Context context) {
        this.context = (MainActivity) context;
    }

    public void findLiveOke(final LiveOkeSocketInfo info) {
        DatagramSocket c = null;

        try {
            //Open a random port to send the package
            c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "WhoYouAre".getBytes();

            InetAddress address = (new UDPBroadcastHelper()).getBroadcastAddress((WifiManager)context.getSystemService(Context.WIFI_SERVICE));
            Log.i(context.app.TAG,"--> About to broadcast to: " + address.getHostAddress());
            DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,address, Integer.parseInt(info.port));
            c.send(sendPacket);
            c.setSoTimeout(10000);
            String response = "";
            while (!response.startsWith("MasterCode")) {
                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                c.receive(receivePacket);
                response = new String(receivePacket.getData()).trim();
                Log.v(LiveOkeRemoteApplication.TAG,"-->response = " + response);
                info.ipAddress = receivePacket.getAddress().toString();
                info.ipAddress = info.ipAddress.substring(1, info.ipAddress.length());
                if (response.startsWith("MasterCode:")) {
                    info.masterCode = response.substring(11,response.length());
                }
                Thread.sleep(100);
                //c.send(sendPacket);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v(LiveOkeRemoteApplication.TAG, "Exception: " + ex.getMessage());
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }

}
