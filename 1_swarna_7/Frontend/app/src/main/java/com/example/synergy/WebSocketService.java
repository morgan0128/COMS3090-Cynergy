package com.example.synergy;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class WebSocketService extends Service {

    // key to WebSocketClient obj mapping - for multiple WebSocket connections
    private final Map<String, WebSocketClient> webSockets = new HashMap<>();

    public WebSocketService() {}

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("CONNECT".equals(action)) {
                String url = intent.getStringExtra("url");      // eg, "ws://localhost:8080/chat/1/uname"
                String key = intent.getStringExtra("key");
                connectWebSocket(key, url);                           // Initialize WebSocket connection
            } else if ("DISCONNECT".equals(action)) {
                String key = intent.getStringExtra("key");
                disconnectWebSocket(key);
            } else if ("TEST_NOTIFICATION".equals(action)) {
                showNotifications("Test notification: Hello from Service!");
                }
        }
        return START_STICKY;
    }

    @Override
    public void onCreate() {    // Register BroadcastReceiver to listen for messages from Activities
        super.onCreate();
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(messageReceiver, new IntentFilter("SendWebSocketMessage"));
    }

    @Override
    public void onDestroy() {   // Close WebSocket connection to prevent memory leaks
        super.onDestroy();
        for (WebSocketClient client : webSockets.values()) {
            client.close();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Initialize WebSocket client and define callback for message reception
    private void connectWebSocket(String key, String url) {

        try {
            URI serverUri = URI.create(url);
            WebSocketClient webSocketClient = new WebSocketClient(serverUri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.d(key, "Connected");
                }

                @Override
                public void onMessage(String message) {
                    Log.d(key, "Message received: " + message);


                    // whenever a message is received for this WebSocketClient obj
                    // broadcast the message internally (within the app) with its corresponding key
                    // only the Activities who care about this message will act accordingly
                    Intent intent = new Intent("WebSocketMessageReceived");
                    intent.putExtra("key", key);
                    intent.putExtra("message", message);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

                    showNotifications(message);

                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.d(key, "Closed");
                }

                @Override
                public void onError(Exception ex) {
                    Log.d(key, "Error");
                }
            };

            webSocketClient.connect();              // connect to the websocket
            webSockets.put(key, webSocketClient);   // add this instance to the mapping

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Listen to the messages from Activities
    // Send the message to its designated Websocket connection
    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            String message = intent.getStringExtra("message");

            WebSocketClient webSocket = webSockets.get(key);
            if (webSocket != null) {
                webSocket.send(message);
            }
        }
    };

    private void disconnectWebSocket(String key) {
        if (webSockets.containsKey(key))
            webSockets.get(key).close();
    }

    public void showNotifications(String message){
        new android.os.Handler(android.os.Looper.getMainLooper()).post(() -> {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View layout = inflater.inflate(R.layout.custom_message, null);

            TextView text = layout.findViewById(R.id.toast_text);
            text.setText(message);

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 150);
            toast.show();



        });



    }
}
