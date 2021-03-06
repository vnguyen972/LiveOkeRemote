package com.vnguyen.liveokeremote.helper;


import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import com.vnguyen.liveokeremote.MainActivity;
import com.vnguyen.liveokeremote.data.LiveOkeSocketInfo;
import com.vnguyen.liveokeremote.service.UDPListenerService;

import java.io.IOException;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Enumeration;

public class UDPBroadcastHelper {

    private MainActivity context;

    public UDPBroadcastHelper() {

    }

    public static String getMyIP(final Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
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
            LogHelper.e( "Unable to get host address:" + ex.getMessage(),ex);
            ipAddressString = null;
        }

        return ipAddressString;
    }

    public InetAddress getBroadcastAddress(WifiManager wifi) throws IOException {
//        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }


    public void broadcastToOtherSelves(final String message, final WifiManager wifi) {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                DatagramSocket c = null;
                try {
                    //Open a random port to send the package
                    c = new DatagramSocket();
                    c.setBroadcast(true);

                    byte[] sendData = message.getBytes();

                    InetAddress address = getBroadcastAddress(wifi);
                    if (address != null) {
                        LogHelper.i("*** About to broadcast to: " + address.getHostAddress());
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, UDPListenerService.BROADCAST_PORT);
                        c.send(sendPacket);
                    } else {
                        // not on WIFI
                        LogHelper.e("*** Not on WIFI? Turn on and connect to WIFI then try again!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    LogHelper.v("Exception: " + ex.getMessage());
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
                return null;
            }
        };
        task.execute((Void[])null);
    }

    public LiveOkeSocketInfo findServer() {
        DatagramSocket c;
        LiveOkeSocketInfo wsInfo = null;
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
                LogHelper.v(getClass().getName() + ">>> Request packet sent to: 255.255.255.255 (DEFAULT)");
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

                    LogHelper.v(getClass().getName() + ">>> Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }

            LogHelper.v(getClass().getName() + ">>> Done looping over all network interfaces. Now waiting for a reply!");

            //Wait for a response
            c.setSoTimeout(10000);
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            try {
                c.receive(receivePacket);
                //We have a response
                LogHelper.v(getClass().getName() + ">>> Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

                //Check if the message is correct
                String message = new String(receivePacket.getData()).trim();
                if (message.startsWith("ws://")) {
                    //DO SOMETHING WITH THE SERVER'S IP (for example, store it in your controller)
                    LogHelper.v("message = " + message);
                    wsInfo = new LiveOkeSocketInfo();
                    wsInfo.uri = message.trim();
                    String address = receivePacket.getAddress().toString();
                    wsInfo.ipAddress = (address.startsWith("/") ? address.substring(1,address.length()) : address);
                    //wsInfo.port = receivePacket.getPort()+"";
                }

                //Close the port!
                c.close();
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            LogHelper.v("Exception: " + ex.getMessage());
        }
        return wsInfo;
    }
}
