package com.vnguyen.liveokeremote.data;

public class LiveOkeRemoteBroadcastMsg {
    public String greeting;
    public String fromWhere;
    public String ipAddress;
    public String name;
    public String message;
    //public boolean isMine;

    public LiveOkeRemoteBroadcastMsg(String greeting, String fromWhere, String name) {
        this.greeting = greeting;
        this.fromWhere = fromWhere;
        this.name = name;
    }
}
