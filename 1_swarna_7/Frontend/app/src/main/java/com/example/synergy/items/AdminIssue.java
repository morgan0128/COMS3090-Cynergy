package com.example.synergy.items;


import org.json.JSONObject;
import java.io.Serializable;

public class AdminIssue implements Serializable {

    private int id;
    private String type;         // EVENTSISSUE / USERISSUE / etc.
    private String title;        // short text to show in list
    private String description;  // longer message
    private boolean open;        // true if in "open" list, false if in "closed" list

    public AdminIssue(int id, String type, String title, String description, boolean open) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.description = description;
        this.open = open;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type != null ? type : "";
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public String getDescription() {
        return description != null ? description : "";
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     * Helper to build from backend JSON.
     * This is defensive and tries multiple key names so it is easier to debug.
     */
    public static AdminIssue fromJson(JSONObject obj, boolean openList) {
        int id = obj.optInt("adminIssueId", obj.optInt("id", -1));
        String type = obj.optString("type", obj.optString("issueType", ""));
        String title = obj.optString("title", obj.optString("summary", "Issue #" + id));
        String description = obj.optString("description", obj.optString("details", ""));
        return new AdminIssue(id, type, title, description, openList);
    }
}

