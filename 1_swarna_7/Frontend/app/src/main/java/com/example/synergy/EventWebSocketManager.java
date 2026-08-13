package com.example.synergy;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class EventWebSocketManager {

    private static EventWebSocketManager instance;

    private WebSocketClient client;
    private EventWebSocketListener listener;

    private EventWebSocketManager() {}

    public static synchronized EventWebSocketManager getInstance() {
        if (instance == null) instance = new EventWebSocketManager();
        return instance;
    }

    public void setWebSocketListener(EventWebSocketListener l) {
        this.listener = l;
    }

    public void removeWebSocketListener() {
        this.listener = null;
    }

    public void connect(String url) {
        try {
            if (client != null && client.isOpen()) return;

            client = new WebSocketClient(URI.create(url)) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    if (listener != null) listener.onWebSocketOpen(handshake);
                }

                @Override
                public void onMessage(String message) {
                    if (listener != null) listener.onWebSocketMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    if (listener != null) listener.onWebSocketClose(code, reason, remote);
                }

                @Override
                public void onError(Exception ex) {
                    if (listener != null) listener.onWebSocketError(ex);
                }
            };

            client.connect();

        } catch (Exception e) {
            Log.e("WS", "Connection error: " + e);
        }
    }

    public void send(String msg) {
        if (client != null && client.isOpen()) {
            client.send(msg);
        }
    }

    public void close() {
        try {
            if (client != null) client.close();
        } catch (Exception ignored) {}
    }
}

