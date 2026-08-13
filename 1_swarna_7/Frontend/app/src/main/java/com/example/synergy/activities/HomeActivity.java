package com.example.synergy.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;



import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.items.AdminUser;
import com.example.synergy.items.Notifications;
import com.example.synergy.sheets.ProfileBottomSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class HomeActivity extends AppCompatActivity {
    private String response;
    public final String GET_URL = "http://coms-3090-016.class.las.iastate.edu:8080/api/profile/";

    public String email;

    public int userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        Button settingButton = findViewById(R.id.settings);
        Button eventButton = findViewById(R.id.eventButton);
        TextView test_msg = findViewById(R.id.testView);
        Button createProfileButton = findViewById(R.id.create_profile_button);
        Button profileButton = findViewById(R.id.profileButton);
        Button notificationButton = findViewById(R.id.notification);
        Button friendsButton = findViewById(R.id.friendsButton);
        Button adminButton = findViewById(R.id.adminButton);
//        Button eventInviteButton = findViewById(R.id.eventInviteButton);
        Bundle extras = getIntent().getExtras();
        Button fileRequestButton = findViewById(R.id.fileTicketButton);
        Button reviewsButton= findViewById(R.id.goToEventReviews);

        if (extras == null){
//            Login credentials didnt pass through, this is not supposed to happen
//            critical error
        } else {
            if (extras.containsKey("response")) {
//            Testing for passed extras
                response = extras.getString("response");

//                showStatusDialog("response", response, true);
                test_msg.setText("Welcome Back!");

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    email = jsonObject.optString("emailId", "");
                    userId = jsonObject.optInt("id", -1);

                } catch (JSONException e) {
                    Log.d("ERROR", e.toString());
                }
            }
            if (extras.containsKey("email")){
                email = extras.getString("emailId");
            }


        }


        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                intent.putExtra("response", response);
                Log.d("response", response);
                assert extras != null;
                intent.putExtra("password", extras.getString("password"));

                startActivity(intent);
            }
        });

        eventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EventsActivity.class);
                intent.putExtra("userDetails", response);
                startActivity(intent);
            }
        });

        createProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSupportFragmentManager().findFragmentByTag("ProfileSheet") == null) {
                    ProfileBottomSheet bottomSheet = ProfileBottomSheet.newInstance(userId);
                    bottomSheet.show(getSupportFragmentManager(), "ProfileSheet");
                } else {
                    Log.d("BottomSheet", "Already showing, skipping new one");
                }
            }
        });


        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, Notifications.class);
                intent.putExtra("userDetails", response);
                startActivity(intent);
            }
        });

        friendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, FriendsActivity.class);
                intent.putExtra("userDetails", response);
                startActivity(intent);


            }
        });

//        eventInviteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomeActivity.this, eventInviteActivity.class);
//                intent.putExtra("userDetails", response);
//
//                startActivity(intent);
//            }
//        });


        profileButton.setOnClickListener(v -> {

            sendServerReq(email);
        });

        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EventReviewActivity.class);
                intent.putExtra("userDetails", response);
                startActivity(intent);
            }
        });


        // Inside onCreate, after findViewById for the admin button:
        Button adminBtn = findViewById(R.id.adminButton);
        adminBtn.setOnClickListener(v -> {
            // TODO: Build this list from whatever data you already have in Home.
            ArrayList<AdminUser> users = new ArrayList<>();
            users.add(new AdminUser(1, "Professor G", "professorG@gmail.com", 2));

            Intent i = new Intent(HomeActivity.this, AdminTicketActivity.class);
            i.putExtra(AdminTicketActivity.EXTRA_ADMIN_ID, userId);
            i.putExtra("users", users); // Serializable payload
            startActivity(i);
        });


        fileRequestButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, TicketRequestActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
        });



        View root = findViewById(R.id.home_root);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }


    }



    private void sendServerReq(String email) {
        String Full_URL = GET_URL + userId;
        Log.d("url", Full_URL);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Full_URL,
                response -> {
                    Log.d("Volley Response", response);
                    Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                    intent.putExtra("response", response);
                    intent.putExtra("email", email);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                },
                error -> {
                    Log.e("Volley Error", error.toString());
                    showStatusDialog(false);
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                return headers;
            }
        };

        VolleySingleton.getInstance(HomeActivity.this).addToRequestQueue(stringRequest);
    }


    private void showStatusDialog(boolean success) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Failed to fetch profile data.")
                .setPositiveButton("OK", null)
                .show();
    }




//    Receiving Messages

    private final BroadcastReceiver webSocketReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            String message = intent.getStringExtra("message");

            if ("notifications".equals(key)){
                Toast.makeText(context, "Received: " + message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(webSocketReceiver, new IntentFilter("WebSocketMessageReceived"));
    }

    @Override
    protected void onStop(){
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(webSocketReceiver);
    }

    @Override
    protected void onResume(){
        super.onResume();
        try {
            updateDetails();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private void updateDetails() throws JSONException {
        JSONObject responseObject = new JSONObject(response);
        int id = responseObject.getInt("id");
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/accounts/" + id;

        JsonObjectRequest getDetails = new JsonObjectRequest(
                Request.Method.GET,
                server_url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject object) {
                        response = object.toString();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }

        );

        VolleySingleton.getInstance(HomeActivity.this).addToRequestQueue(getDetails);
    }

}