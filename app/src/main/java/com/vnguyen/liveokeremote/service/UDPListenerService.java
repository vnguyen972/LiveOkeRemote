package com.vnguyen.liveokeremote.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.helper.UDPBroadcastHelper;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPListenerService extends Service {
    public static String UDP_BROADCAST = "UDPBroadcast";
    public static int BROADCAST_PORT = 9999;

    private boolean shouldRestartSocketListen = true;
    private DatagramSocket socket;
    private final IBinder myBinder = new MyLocalBinder();
    private InetAddress broadcastIP;
    Thread UDPBroadcastThread;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        shouldRestartSocketListen = true;
        startListenForUDPBroadcast();
        Log.e(LiveOkeRemoteApplication.TAG,"UDP Listener Service Started.");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopListen();
    }

    private void stopListen() {
        shouldRestartSocketListen = false;
        if (socket != null) {
            socket.close();
        }
    }

    public void sendMessageBroadcast(String message) {
        try {
            byte[] sendData = message.getBytes();

            InetAddress address = (new UDPBroadcastHelper()).getBroadcastAddress((WifiManager)getSystemService(Context.WIFI_SERVICE));
            Log.i(LiveOkeRemoteApplication.TAG,"** --> About to broadcast to: " + address.getHostAddress());
            DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,address, 8888);
            socket.send(sendPacket);
        } catch (Exception ex) {
            Log.e(LiveOkeRemoteApplication.TAG,ex.getMessage(),ex);
        } finally {

        }
    }

    private void broadcastIntent(String senderIP, int senderPORT,String message) {
        Intent intent = new Intent(UDP_BROADCAST);
        intent.putExtra("senderIP", senderIP);
        intent.putExtra("senderPORT", senderPORT);
        intent.putExtra("message", message);
        sendBroadcast(intent);
    }

    void startListenForUDPBroadcast() {
        UDPBroadcastThread = new Thread(new Runnable() {
            public void run() {
                try {
                    broadcastIP = InetAddress.getByName("0.0.0.0"); //172.16.238.42 //192.168.1.255
                    while (shouldRestartSocketListen) {
                        listenAndWaitAndThrowIntent(broadcastIP);
                    }
                } catch (Exception e) {
                    Log.e(LiveOkeRemoteApplication.TAG, "no longer listening for UDP broadcasts cause of error " + e.getMessage());
                }
            }
        });
        UDPBroadcastThread.start();
    }

    private void listenAndWaitAndThrowIntent(InetAddress broadcastIP) throws Exception {
        byte[] recvBuf = new byte[15000];
        if (socket == null || socket.isClosed()) {
            socket = new DatagramSocket(BROADCAST_PORT, broadcastIP);
            socket.setBroadcast(true);
        }
        //socket.setSoTimeout(1000);
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        Log.e(LiveOkeRemoteApplication.TAG, "Waiting for UDP broadcast");
        socket.receive(packet);

        String senderIP = packet.getAddress().getHostAddress();
        int senderPORT = packet.getPort();
        String message = new String(packet.getData()).trim();

        Log.e(LiveOkeRemoteApplication.TAG, "Got UDP broadcast from " + senderIP + ", message: " + message);

        broadcastIntent(senderIP, senderPORT, message);
        //socket.close();
    }

    public class MyLocalBinder extends Binder {
        public UDPListenerService getService() {
            return UDPListenerService.this;
        }
    }
}
