package com.example.synergy.items;

public class EventInvite {

    private String eventName;
    private Friend friend;

    public EventInvite(Friend friend){
        this.friend = friend;
    }

    public Friend getFriend() {
        return friend;
    }
    public String getEventName(){
        return eventName;
    }
}
