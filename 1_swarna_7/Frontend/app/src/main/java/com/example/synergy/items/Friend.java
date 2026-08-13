package com.example.synergy.items;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend {

    private String name;
    private int id;

    public Friend(JSONObject friend) throws JSONException {
        try {
            this.name = friend.getString("friendName");
            this.id = friend.getInt("friendId");
        } catch (JSONException e) {
            this.name = friend.getString("friendUsername");
            this.id = friend.getInt("friendId");
        }

    }

    public String getName(){
        return this.name;
    }

    public int getId(){ return this.id; }
}
