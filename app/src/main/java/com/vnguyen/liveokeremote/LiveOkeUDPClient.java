package com.vnguyen.liveokeremote;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

import com.vnguyen.liveokeremote.data.LiveOkeSocketInfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public abstract class LiveOkeUDPClient extends BroadcastReceiver {
    public LiveOkeSocketInfo liveOkeSocketInfo;
    private DatagramSocket cSocket;
    private MainActivity context;

    LiveOkeUDPClient(final Context context) {
        this.context = (MainActivity) context;
        // hunt for LiveOke address here
        // but for now default it
        liveOkeSocketInfo = new LiveOkeSocketInfo();
        liveOkeSocketInfo.port = "8888";
        initClient();
    }

    private void initClient() {
        if (context.udpListenerService != null) {
            // if udp listener service bound (we have access to it)
            // then go look for LiveOke instance running on the network
            context.udpListenerService.sendMessageBroadcast("WhoYouAre");
        }
    }

    public void sendMessage(final String sendMsg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (cSocket == null || cSocket.isClosed()) {
                        cSocket = new DatagramSocket();
                        cSocket.setBroadcast(true);
                    }
                    byte[] sData = sendMsg.getBytes();
                    InetAddress destinationAddr = InetAddress.getByName(liveOkeSocketInfo.ipAddress);
                    Log.i(LiveOkeRemoteApplication.TAG,"+++ About to send to " + liveOkeSocketInfo.ipAddress);
                    DatagramPacket sPacket = new DatagramPacket(sData,sData.length,destinationAddr,Integer.parseInt(liveOkeSocketInfo.port));
                    cSocket.send(sPacket);
                    Log.i(LiveOkeRemoteApplication.TAG,"+++ Sent to LiveOke, now we wait");
                    cSocket.setSoTimeout(10000);
                    if (sendMsg.equalsIgnoreCase("getsonglist")) {
                        String response = "";
                        while (!response.startsWith("Finish")) {
                            byte[] recvBuf = new byte[15000];
                            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                            cSocket.receive(receivePacket);
                            response = new String(receivePacket.getData()).trim();
                            //onReceived(response);
                        }
                    }
                } catch (SocketTimeoutException x) {
                    Log.e(LiveOkeRemoteApplication.TAG,x.getMessage(),x);
                } catch (Exception x) {
                    Log.e(LiveOkeRemoteApplication.TAG,x.getMessage(),x);
                } finally {
                    if (cSocket != null) {
                        cSocket.close();
                    }
                }
            }
        }).start();
    }
}
