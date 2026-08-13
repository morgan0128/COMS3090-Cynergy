package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.java_websocket.handshake.ServerHandshake;

import java.util.ArrayList;
import java.util.List;

/**
 * ChatActivity handles the chat interface where users can send and receive messages
 * using a WebSocket connection.
 */
public class ChatActivity extends AppCompatActivity implements WebSocketListener{

    private Button sendBtn;
    private EditText msgEtx;


    private RecyclerView chatRecyclerView;
    private List<Message> messages;
    private ChatAdapter chatAdapter;
    private String myUsername;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        /* initialize UI elements */
        sendBtn = (Button) findViewById(R.id.sendBtn);
        msgEtx = (EditText) findViewById(R.id.msgEdt);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(messages);

        chatRecyclerView.setAdapter(chatAdapter);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        /* connect this activity to the websocket instance */
        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);


        Bundle extras= getIntent().getExtras();
        if (extras != null){
            myUsername = extras.getString("username");

        } else {
            myUsername = "";
        }


        messages.add(new Message("User: "+ myUsername + " has joined the chat", true));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatRecyclerView.scrollToPosition(messages.size() - 1);

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            String msg = String.valueOf(msgEtx.getText());
            if (!msg.isEmpty()){

                try {
                    // send message
                    WebSocketManager.getInstance().sendMessage(msgEtx.getText().toString());
                    //clear text box once the msg is sent
                    msgEtx.setText("");
                } catch (Exception e) {
                    Log.d("ExceptionSendMessage:", e.getMessage().toString());
                }

                msgEtx.setText("");
            }
        });

    }

    private boolean addIncomingMessage(String rawMessage) {
        // rawMessage format: "myUsername: message"
        int colonIndex = rawMessage.indexOf(":");
        if (colonIndex == -1) return false; // invalid format

        String username = rawMessage.substring(0, colonIndex).trim();
        String messageText = rawMessage.substring(colonIndex + 1).trim();

        return username.equals(myUsername);

    }

    /**
     * Called when a message is received from the WebSocket.
     * This method ensures that UI updates happen on the main thread.
     */
    @Override
    public void onWebSocketMessage(String message) {
        /**
         * In Android, all UI-related operations must be performed on the main UI thread
         * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
         * is used to post a runnable to the UI thread's message queue, allowing UI updates
         * to occur safely from a background or non-UI thread.
         */
        runOnUiThread(() -> {


            messages.add(new Message(message, addIncomingMessage(message)));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.scrollToPosition(messages.size() - 1);
        });
    }

    /**
     * Called when the WebSocket connection is closed.
     * Displays the closure reason in the TextView.
     *
     * @param code   The status code of the closure
     * @param reason The reason provided for closure
     */
    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {

            messages.add(new Message("Connection closed by " + closedBy + "\nReason: " + reason, false));
            chatAdapter.notifyItemInserted(messages.size() - 1);
            chatRecyclerView.scrollToPosition(messages.size() - 1);

        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {}


    @Override
    public void onWebSocketError(Exception ex) {}
}