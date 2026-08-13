package com.example.synergy.items;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    private String name;
    private int id;

    public User(JSONObject friend) throws JSONException {
        try {
            this.name = friend.getString("userName");

        } catch (JSONException e) {
            this.name = friend.getString("name");
        }

        this.id = friend.getInt("id");

    }

    public String getName(){
        return this.name;
    }

    public int getId(){ return this.id; }
}
