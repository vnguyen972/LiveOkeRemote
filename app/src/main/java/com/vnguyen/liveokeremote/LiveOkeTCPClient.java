package com.vnguyen.liveokeremote;

public interface LiveOkeTCPClient {
    public static int SERVER_TCP_PORT = 8080;

    public void onReceived(String message);
    public void onErrored(Exception exception);
}
