package com.example.synergy.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.synergy.EventWebSocketListener;
import com.example.synergy.EventWebSocketManager;
import com.example.synergy.R;
import com.example.synergy.adapters.ChatAdapter;
import com.example.synergy.items.ChatMessage;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;

import java.util.Random;


/**
 * Activity that manages real-time event chat.
 *
 * <p>This screen allows users to:</p>
 * <ul>
 *     <li>Send and receive chat messages</li>
 *     <li>Load chat history from the backend</li>
 *     <li>Receive real-time updates through WebSocket</li>
 *     <li>See connection status (connected/disconnected) through the dot</li>
 * </ul>
 *
 * <p>
 * It uses EventWebSocketManager to maintain a live connection and
 * ChatAdapter to display messages inside a RecyclerView.
 * </p>
 */
public class EventChatActivity extends AppCompatActivity implements EventWebSocketListener {

    /** Back button that allows us to exit the chat screen. */
    private ImageButton backBtn;

    /** Send button used to send a chat message. */
    private ImageButton sendBtn;

    /** Text field where the user types the message. */
    private EditText msgEdt;

    /** RecyclerView displaying all chat messages. */
    private RecyclerView messagesRv;

    /** Small colored dot indicating online/offline WebSocket status. */
    private View statusDot;

    /** The title of the chat screen (event name). */
    private TextView titleTv;

    /** Adapter that holds and displays all chat messages. */
    private ChatAdapter adapter;

    /** ID of the current user. */
    private String userId;

    /** The user's display name shown in chat messages. */
    private String displayName;

    /** The chatroom ID the current user is placed in. */
    private String chatroomId;

    /** The ID of the event this chat belongs to. */
    private String eventId;

    /** Base URL for GET request */
    private static final String BASE =
            "http://coms-3090-016.class.las.iastate.edu:8080";

    /**
     * Initializes the activity:
     * - Loads views
     * - Reads intent data
     * - Sets up RecyclerView and buttons
     * - Loads chat history from backend
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        initIntentData();
        setupRecycler();
        setupButtons();
        loadChatHistory();
    }


    /**
     * Finds all the UI elements used in this activity.
     * Separating UI from the rest of the code.
     */
    private void initViews() {
        backBtn    = findViewById(R.id.backBtn);
        sendBtn    = findViewById(R.id.sendBtn);
        msgEdt     = findViewById(R.id.msgEdt);
        messagesRv = findViewById(R.id.messagesRv);
        statusDot  = findViewById(R.id.statusDot);
        titleTv    = findViewById(R.id.titleTv);
    }

    /**
     * Reads data passed from EventAttendanceActivity.
     * Sets:
     * - userId
     * - eventId
     * - displayName
     * - chatroomId
     */
    private void initIntentData() {
        Intent i = getIntent();

        userId = String.valueOf(i.getIntExtra("user_id", 1)); // default 1
        eventId = String.valueOf(i.getIntExtra("event_id", 1));

        Random random = new Random();
        chatroomId = eventId;


        // Display event name
        String eventTitle = i.getStringExtra("event_name");
        if (!TextUtils.isEmpty(eventTitle))
            titleTv.setText(eventTitle + " Chat");
    }

    /**
     * Sets up RecyclerView for displaying chat messages.
     */
    private void setupRecycler() {
        adapter = new ChatAdapter();

        LinearLayoutManager lm =
                new LinearLayoutManager(this, RecyclerView.VERTICAL, false);

        messagesRv.setLayoutManager(lm);
        messagesRv.setAdapter(adapter);
    }

    /**
     * Sets listeners for buttons:
     * - Back button closes activity
     * - Send button sends WebSocket message
     */
    private void setupButtons() {

        backBtn.setOnClickListener(v -> finish()); //back button listener

        sendBtn.setOnClickListener(v -> {
            String txt = msgEdt.getText().toString().trim(); //trimming the text
            if (txt.isEmpty()) return;

            // Send message through WebSocket
            EventWebSocketManager.getInstance().send(txt);
            msgEdt.setText("");  // clear text field after sending
        });
    }


    /**
     * Retrieves old chat messages from backend.
     * Expecting a JSON array of raw strings.
     */
    private void loadChatHistory() {
        String url = BASE + "/api/events/" + chatroomId + "/chat"; // The full GET URL for chat history

        StringRequest req = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray arr = new JSONArray(response);

                        for (int i = 0; i < arr.length(); i++) { // going through all messages

                            String raw = arr.getString(i).trim(); //trimming
                            if (raw.isEmpty()) continue;

                            adapter.add(new ChatMessage( //adding the chat message
                                    raw,
                                    "other",
                                    System.currentTimeMillis()
                            ));
                        }

                    } catch (Exception ignored) {}
                },
                error -> Log.e("CHAT-HISTORY", "GET FAILED: " + error)); //error handling

        Volley.newRequestQueue(this).add(req); //add to queue
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
     * Called when WebSocket successfully connects.
     */
    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
        runOnUiThread(() -> setStatus(true));
    }

    /**
     * Called when a message is received through WebSocket.
     * - Splits multi-line messages
     * - Filters join/leave system messages
     * - Parses "sender: message" format
     */
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


    /**
     * Called when WebSocket connection closes.
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        runOnUiThread(() -> setStatus(false));
    }

    /**
     * Called when a WebSocket error occurs.
     */
    @Override
    public void onWebSocketError(Exception ex) {
        runOnUiThread(() -> setStatus(false));
    }
}

