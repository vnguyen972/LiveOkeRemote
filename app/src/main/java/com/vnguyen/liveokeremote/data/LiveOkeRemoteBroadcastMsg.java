package com.vnguyen.liveokeremote.data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LiveOkeRemoteBroadcastMsg {
    public String greeting;
    public String fromWhere;
    public String ipAddress;
    public String name;
    public String message;
    public Date time;
    //public boolean isMine;

    public LiveOkeRemoteBroadcastMsg(String greeting, String fromWhere, String name) {
        this.greeting = greeting;
        this.fromWhere = fromWhere;
        this.name = name;
        this.time = new Date(Calendar.getInstance().getTimeInMillis());
    }

    public String getDateTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        String formattedDate = formatter.format(time);
        return formattedDate;
    }
}
