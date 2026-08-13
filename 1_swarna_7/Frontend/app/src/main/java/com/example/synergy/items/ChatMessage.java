package com.example.synergy.items;

public class ChatMessage {
    public final String text;
    public final String senderId;
    public final long timestamp;

    public ChatMessage(String text, String senderId, long timestamp) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }
}

