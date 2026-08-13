package com.example.synergy.items;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.synergy.R;
import com.example.synergy.adapters.NotifAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class Notifications extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotifAdapter adapter;
    private List<NotificationItem> notifications;
    private String userDetailsString;
    private Button clearallButton;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.notification_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        clearallButton = findViewById(R.id.clear_all);
        backButton = findViewById(R.id.backbuttonToHome);
        recyclerView = findViewById(R.id.notifications_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notifications = new ArrayList<>();
        adapter = new NotifAdapter(this, notifications, userDetailsString);
        recyclerView.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        userDetailsString = extras.getString("userDetails");

        clearallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    clearAllNotifications();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        try {
            getAllNotifications();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    public void addNotification(JSONObject notification) throws JSONException {
        notifications.add(0, new NotificationItem(notification));
        adapter.notifyItemInserted(0);
        recyclerView.scrollToPosition(0);
    }

    private void getAllNotifications() throws JSONException {
        JSONObject userDetails = new JSONObject(userDetailsString);
        int userId = userDetails.getInt("id");

        String all_notifications = "http://coms-3090-016.class.las.iastate.edu:8080/api/notifications/" + userId;

        JsonArrayRequest arrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                all_notifications,
                null,
                response -> {
                    if (response != null){
                        JSONArray notifArray = response;
                        for (int i =0; i < notifArray.length(); i++){
                            try {
                                JSONObject notification = notifArray.getJSONObject(i);
                                addNotification(notification);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    } else {
                        Toast.makeText(Notifications.this, "No New Notifications", Toast.LENGTH_SHORT).show();
                    }

                },
                error -> {
                    Toast.makeText(Notifications.this, "Volley Error: " + error, Toast.LENGTH_SHORT).show();
                }
        );


        RequestQueue queue = Volley.newRequestQueue(Notifications.this);
        queue.add(arrayRequest);
    }

    private void clearAllNotifications() throws JSONException {
        JSONObject userDetails = new JSONObject(userDetailsString);
        int userId = userDetails.getInt("id");

        String clear_all_notifications = "http://coms-3090-016.class.las.iastate.edu:8080/api/notifications/" + userId + "/clear";

        @SuppressLint("NotifyDataSetChanged") StringRequest arrayRequest = new StringRequest(
                Request.Method.DELETE,
                clear_all_notifications,
                response -> {
                    Toast.makeText(Notifications.this, "Notifications Cleared", Toast.LENGTH_SHORT).show();
                    notifications.clear();
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Toast.makeText(Notifications.this, "Notifications Cleared", Toast.LENGTH_SHORT).show();
                }
        );


        RequestQueue queue = Volley.newRequestQueue(Notifications.this);
        queue.add(arrayRequest);
    }
}