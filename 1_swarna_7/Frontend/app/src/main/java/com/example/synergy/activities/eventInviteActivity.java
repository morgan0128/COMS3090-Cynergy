package com.example.synergy.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.InviteFriendAdapter;
import com.example.synergy.items.EventInvite;
import com.example.synergy.items.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class eventInviteActivity extends AppCompatActivity {

    private Button backButton;
    private RecyclerView inviteRv;
    private InviteFriendAdapter adapter;
    private String userDetailString;
    private List<EventInvite> inviteFriends;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_invites);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.event_invites), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        handleExtras();
        setUpUI();
        try {
            fetchFriends();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void handleExtras(){
        Bundle extras = getIntent().getExtras();
        assert extras != null;
        userDetailString = extras.getString("userDetails");
    }

    private void setUpUI(){
        backButton = findViewById(R.id.backFromInvitesToHome);
        inviteRv = findViewById(R.id.invite_rv);
        inviteRv.setLayoutManager(new LinearLayoutManager(this));
        inviteFriends = new ArrayList<>();
        adapter = new InviteFriendAdapter(this, inviteFriends, userDetailString);
        inviteRv.setAdapter(adapter);

    }


    private void fetchFriends() throws JSONException {
        JSONObject userDetails = new JSONObject(userDetailString);
        int userId = userDetails.getInt("id");
        String server_url ="http://coms-3090-016.class.las.iastate.edu:8080/api/friends/" + userId;


        @SuppressLint("NotifyDataSetChanged") JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                server_url,
                null,
                this::handlingFriendsSuccess,
                error -> {
                    Log.d("ERROR", error.toString());
                    Toast.makeText(this, "Failed to load friends", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);

    }

    private void handlingFriendsSuccess(JSONArray response){
        inviteFriends.clear();
        // response is a JsonArray so we loop through this
        // array to get the Friend JsonObject
        for (int i = 0; i < response.length(); i++) {
            try {

                JSONObject object = response.getJSONObject(i);
                JSONObject friendObject;
                friendObject = object.getJSONObject("friend");

                inviteFriends.add(new EventInvite(new Friend(friendObject)));
            } catch (JSONException e) {
                Log.d("ERROR", e.toString());
            }
        }
        adapter.notifyDataSetChanged();
    }




}