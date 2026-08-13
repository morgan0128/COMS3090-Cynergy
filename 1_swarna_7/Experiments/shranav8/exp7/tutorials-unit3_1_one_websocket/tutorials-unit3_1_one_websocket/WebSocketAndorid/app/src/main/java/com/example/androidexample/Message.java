package com.example.androidexample;

public class Message {
    private String text;
    private boolean sentByUser;
    private String username;

    public Message(String text, boolean sentByUser) {
        this.text = text;
        this.sentByUser = sentByUser;
    }

    public String getText() { return text; }
    public boolean isSentByUser() { return sentByUser; }
    public String getUsername() { return username; }

}
