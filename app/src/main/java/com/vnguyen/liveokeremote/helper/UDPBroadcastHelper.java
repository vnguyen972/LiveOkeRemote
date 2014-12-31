package com.vnguyen.liveokeremote.helper;


import android.util.Log;

import com.vnguyen.liveokeremote.LiveOkeRemoteApplication;
import com.vnguyen.liveokeremote.data.WebSocketInfo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Enumeration;

public class UDPBroadcastHelper {

    public WebSocketInfo findServer() {
        DatagramSocket c;
        WebSocketInfo wsInfo = null;
// Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            c = new DatagramSocket();
            c.setBroadcast(true);

            byte[] sendData = "WSAddress".getBytes();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), 8888);
                c.send(sendPacket);
                Log.v(LiveOkeRemoteApplication.TAG,getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, 8888);
                        c.send(sendPacket);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.v(LiveOkeRemoteApplication.TAG,getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            Log.v(LiveOkeRemoteApplication.TAG,getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            c.setSoTimeout(10000);
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            try {
                c.receive(receivePacket);
                //We have a response
                Log.v(LiveOkeRemoteApplication.TAG,getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                //Check if the message is correct
                String message = new String(receivePacket.getData()).trim();
                if (message.startsWith("ws://")) {
                    //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                    Log.v(LiveOkeRemoteApplication.TAG,"message = " + message);
                    wsInfo = new WebSocketInfo();
                    wsInfo.uri = message.trim();
                    String address = receivePacket.getAddress().toString();
                    wsInfo.ipAddress = (address.startsWith("/") ? address.substring(1,address.length()) : address);
                    wsInfo.port = receivePacket.getPort()+"";
                }

                //Close the port!
                c.close();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.v(LiveOkeRemoteApplication.TAG, "Exception: " + ex.getMessage());
        }
        return wsInfo;
    }
}
