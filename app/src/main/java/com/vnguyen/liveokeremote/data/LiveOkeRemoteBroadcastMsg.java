package com.vnguyen.liveokeremote.data;

public class LiveOkeRemoteBroadcastMsg {
    public String greeting;
    public String from;
    public String name;

    public LiveOkeRemoteBroadcastMsg(String greeting, String from, String name) {
        this.greeting = greeting;
        this.from = from;
        this.name = name;
    }
}
