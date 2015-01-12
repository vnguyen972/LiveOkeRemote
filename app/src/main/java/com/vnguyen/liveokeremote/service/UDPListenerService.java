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
    // UDP Action Name
    public static String UDP_BROADCAST = "UDPBroadcast";
    // UDP Port this listener is listening on
    public static int BROADCAST_PORT = 9999;

    private boolean shouldRestartSocketListen = true;
    private DatagramSocket socket;
    private final IBinder myBinder = new MyLocalBinder();
    private InetAddress broadcastIP;

    private Thread UDPBroadcastThread;


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

    /**
     * send UDP packet to the provided ipAddress and port
     * If ipAddress was NULL, then it'll the UDP packet to the broadcast address
     * within the network.
     *
     * @param message
     * @param ipAddress
     * @param port
     */
    public void sendMessage(final String message, final String ipAddress, final int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (ipAddress != null && !ipAddress.equals("")) {
                        byte[] sendData = message.getBytes();

                        InetAddress address = InetAddress.getByName(ipAddress);
                        Log.i(LiveOkeRemoteApplication.TAG,"** --> About to cast '" + message + "' to: " + address.getHostAddress() + ":" + port);
                        DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,address, port);
                        socket.send(sendPacket);
                    } else {
                        // if no address specified, then send to broadcast address
                        sendMessageBroadcast(message, port);
                    }
                } catch (Exception ex) {
                    Log.e(LiveOkeRemoteApplication.TAG,ex.getMessage(),ex);
                } finally {
                }
            }
        }).start();
    }

    /**
     * Send a UDP packet to the broadcast address on certain PORT
     *
     * @param message
     * @param port
     */
    private void sendMessageBroadcast(String message, int port) {
        try {
            byte[] sendData = message.getBytes();

            InetAddress address = (new UDPBroadcastHelper()).getBroadcastAddress((WifiManager)getSystemService(Context.WIFI_SERVICE));
            Log.i(LiveOkeRemoteApplication.TAG,"** --> About to broadcast '" + message + "' to: " + address.getHostAddress() +":"+port);
            DatagramPacket sendPacket = new DatagramPacket(sendData,sendData.length,address, port);
            socket.send(sendPacket);
        } catch (Exception ex) {
            Log.e(LiveOkeRemoteApplication.TAG,ex.getMessage(),ex);
        } finally {

        }
    }

    /**
     * Broadcast the Intent with the DATA back to the Activity's Receiver
     *
     * @param senderIP
     * @param senderPORT
     * @param message
     */
    private void broadcastIntent(String senderIP, int senderPORT,String message) {
        Intent intent = new Intent(UDP_BROADCAST);
        intent.putExtra("senderIP", senderIP);
        intent.putExtra("senderPORT", senderPORT);
        intent.putExtra("message", message);
        //sendBroadcast(intent);
        sendOrderedBroadcast(intent,null);
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
        byte[] recvBuf = new byte[590];
        if (socket == null || socket.isClosed()) {
            socket = new DatagramSocket(BROADCAST_PORT, broadcastIP);
            socket.setBroadcast(true);
        }
        //socket.setSoTimeout(1000);
        DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
        //Log.e(LiveOkeRemoteApplication.TAG, "Waiting for UDP broadcast");
        socket.receive(packet);

        final String senderIP = packet.getAddress().getHostAddress();
        final int senderPORT = packet.getPort();
        byte[] data = packet.getData();
        final String message = new String(data,0,data.length).trim();

        //Log.v(LiveOkeRemoteApplication.TAG, "Got UDP broadcast from " + senderIP + ":" + senderPORT + ", message: " + message);
        broadcastIntent(senderIP, senderPORT, message);
        //socket.close();
    }

    public class MyLocalBinder extends Binder {
        public UDPListenerService getService() {
            return UDPListenerService.this;
        }
    }
}
