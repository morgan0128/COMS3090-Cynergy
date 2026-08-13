package com.example.synergy.items;

public class EventItem {
    private final int id;
    private final String eventName;
    private final String eventTime;
    private final String eventLocation;
    private final String eventDate;
    private final String description;

    public EventItem(int id, String eventName, String eventTime, String eventLocation, String eventDate, String description) {
        this.id = id;
        this.eventName = eventName;
        this.eventTime = eventTime;
        this.eventLocation = eventLocation;
        this.eventDate = eventDate;
        this.description = description;
    }

    public int getId() { return id; }
    public String getEventName() { return eventName; }
    public String getEventTime() { return eventTime; }
    public String getEventLocation() { return eventLocation; }
    public String getEventDate() { return eventDate; }
    public String getDescription() { return description; }
}
