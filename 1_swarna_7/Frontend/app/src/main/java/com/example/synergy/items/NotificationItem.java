package com.example.synergy.items;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationItem {
    private String title;
    private String message;

    private String status;
    private String time;
    private int notiId;

    public NotificationItem(JSONObject notification) throws JSONException {

//        this.notiId = notification.getInt("noti_id");
        this.title = notification.getString("type");
        this.message = notification.getString("message");
//        this.time = notification.getString("createdAt");
        this.status = notification.getString("status");
        this.notiId = notification.getInt("notiId");
    }

    public String getTitle(){return this.title;}
    public String getMessage(){return this.message;}
    public String getStatus(){return this.status;}
    public int getNotiId(){return this.notiId;}

    public void setStatus(String read) {
        this.status = read;
    }
}
