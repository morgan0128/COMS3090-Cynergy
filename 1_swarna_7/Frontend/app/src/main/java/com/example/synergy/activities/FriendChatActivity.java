package com.example.synergy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synergy.EventWebSocketListener;
import com.example.synergy.EventWebSocketManager;
import com.example.synergy.R;
import com.example.synergy.adapters.ChatAdapter;
import com.example.synergy.items.ChatMessage;

import org.java_websocket.handshake.ServerHandshake;

public class FriendChatActivity extends AppCompatActivity implements EventWebSocketListener {

    private ImageButton backButton;

    private ChatAdapter adapter;
    private ImageButton sendButton;
    private RecyclerView messagesRv;
    private String displayName;
    private EditText msgEdt;
    private int userId;
    private int chatroomId;
    private View statusDot;
    private TextView titleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friend_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.friend_chat_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupUI();

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        titleTv.setText(extras.getString("friend_name"));
        chatroomId = extras.getInt("chatroom_id");
        userId = extras.getInt("userId");

        adapter = new ChatAdapter();

        LinearLayoutManager lm =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        messagesRv.setLayoutManager(lm);
        messagesRv.setAdapter(adapter);

        handleButtonClicks();

    }

    private void setupUI(){
        backButton = findViewById(R.id.backbuttonToHome);
        sendButton = findViewById(R.id.sendBtn);
        msgEdt = findViewById(R.id.msgEdt);
        titleTv = findViewById(R.id.titleTv);
        messagesRv = findViewById(R.id.messagesRv);
        statusDot  = findViewById(R.id.statusDot);
    }

    private void handleButtonClicks(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String txt = msgEdt.getText().toString().trim(); //trimming the text
                if (txt.isEmpty()) return;

                // Send message through WebSocket
                EventWebSocketManager.getInstance().send(txt);
                msgEdt.setText("");  // clear text field after sending
            }
        });
    }

    /**
     * Updates the status dot (green = connected, red = disconnected).
     */
    private void setStatus(boolean connected) {
        statusDot.setBackgroundResource(
                connected //if connected green, else red
                        ? R.drawable.shape_status_dot_connected
                        : R.drawable.shape_status_dot_disconnected
        );
    }



    /**
     * Called when activity becomes visible. (user in chat room)
     * Connects to WebSocket and sets WebSocket listener.
     */
    @Override
    protected void onStart() {
        super.onStart();

        EventWebSocketManager
                .getInstance()
                .setWebSocketListener(this);

        String wsUrl =
                "ws://coms-3090-016.class.las.iastate.edu:8080/chat/"
                        + chatroomId + "/" + userId;

        // Connect to WebSocket room
        EventWebSocketManager.getInstance().connect(wsUrl);
        setStatus(false); // show disconnected until onWebSocketOpen fires
    }

    /**
     * Called when activity is no longer visible. (user no longer in chat room)
     * Closes WebSocket and removes listener to avoid leaks.
     */
    @Override
    protected void onStop() {
        EventWebSocketManager.getInstance().close();
        EventWebSocketManager.getInstance().removeWebSocketListener();
        super.onStop();
    }



    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        runOnUiThread(() -> setStatus(true));
    }

    @Override
    public void onWebSocketMessage(String msg) {
        if (msg == null) return; // ignore empty messages

        // WebSocket delivers messages on a background thread → switch to UI thread
        runOnUiThread(() -> {

            String[] lines = msg.split("\n"); // handle multi-line messages

            for (String raw : lines) {

                raw = raw.trim();
                if (raw.isEmpty()) continue;

                // Skip system join/leave notifications
                if (raw.contains("Joined the Chat") ||
                        raw.contains("Left the Chat"))
                    continue;

                // Expect format: "Sender: message"
                int index = raw.indexOf(":");
                if (index < 1) continue; // if no sender found, ignore

                String sender = raw.substring(0, index).trim();
                String text   = raw.substring(index + 1).trim();

                // Fix duplicate prefix cases: "name: name: hi"
                if (text.startsWith(sender + ":"))
                    text = text.substring(sender.length() + 1).trim();

                boolean isMe = sender.equalsIgnoreCase(displayName);

                adapter.add(new ChatMessage(
                        text,
                        isMe ? displayName : sender,
                        System.currentTimeMillis()
                ));
            }

            messagesRv.scrollToPosition(adapter.getItemCount() - 1);
        });
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        runOnUiThread(() -> setStatus(false));
    }

    @Override
    public void onWebSocketError(Exception ex) {
        runOnUiThread(() -> setStatus(false));
    }
}