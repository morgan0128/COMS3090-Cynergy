package com.example.synergy.items;

import org.json.JSONObject;

public class Attendee {

    private final String name;
    private final int avatarResId;
    private final int id;

    public Attendee(int id, String name, int avatarResId) {
        this.id = id;
        this.name = name;
        this.avatarResId = avatarResId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getAvatarResId() { return avatarResId; }

    public static Attendee fromJson(JSONObject o) {
        if (o == null) return null;

        int id = o.optInt("id", -1);
        String name = o.optString("name", "Unknown");

        int avatar = Placeholders.pick(name);
        return new Attendee(id, name, avatar);
    }
}




