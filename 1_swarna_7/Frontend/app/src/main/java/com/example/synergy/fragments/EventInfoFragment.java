package com.example.synergy.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.FriendAdapter;
import com.example.synergy.items.Friend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EventInfoFragment extends Fragment {

    // ---------------- UI ----------------
    private TextView nameText, dateText, timeText, locationText, ownerText, attendeeChip;
    private Button interestButton;
    private ImageButton backButton;

    // ---------------- Data ----------------
    private int eventId;
    private int userId;
    private RecyclerView friendsRv;
    private List<Friend> friends;
    private FriendAdapter adapter;
    private boolean isInterested = false;
    private String userDetailString;

    private static final String BASE =
            "http://coms-3090-016.class.las.iastate.edu:8080/api/events/";

    // ----------------------------------------------------------
    // Lifecycle
    // ----------------------------------------------------------
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_event_info, container, false);

        initUI(view);
        readArguments();

        friends = new ArrayList<>();
        adapter = new FriendAdapter(requireContext(), friends, userDetailString);
        friendsRv.setAdapter(adapter);

        getFriendsAttendingEvent();

        // Check interest using POST
        checkInterestOnOpen();

        // Load attendance count
        loadAttendanceCount();

        // Handle clicks
        interestButton.setOnClickListener(v -> {
            if (isInterested) {
                leaveEvent();
            } else {
                joinEvent();
            }
        });

        return view;
    }

    // ----------------------------------------------------------
    // Initialization
    // ----------------------------------------------------------
    private void initUI(View view) {
        nameText       = view.findViewById(R.id.event_name);
        dateText       = view.findViewById(R.id.event_date);
        timeText       = view.findViewById(R.id.event_time);
        locationText   = view.findViewById(R.id.event_location);
        ownerText      = view.findViewById(R.id.event_owner);
        attendeeChip   = view.findViewById(R.id.attendee_count_chip);
        interestButton = view.findViewById(R.id.interest_button);
        backButton     = view.findViewById(R.id.back_button);
        friendsRv      = view.findViewById(R.id.friendsAttending);

        friendsRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());
        updateButton();
    }

    private void readArguments() {
        if (getArguments() == null) return;

        eventId = getArguments().getInt("eventId");
        userId  = getArguments().getInt("userId", -1);
        userDetailString = getArguments().getString("userDetails");
        nameText.setText(getArguments().getString("eventName"));
        dateText.setText(getArguments().getString("eventDate"));
        timeText.setText(getArguments().getString("eventTime"));
        locationText.setText(getArguments().getString("eventLocation"));
        ownerText.setText("by " + getArguments().getString("ownerName", ""));
    }

    // ----------------------------------------------------------
    // INTEREST CHECK ON OPEN (POST ONLY)
    // ----------------------------------------------------------
    private void checkInterestOnOpen() {
        String url = BASE + eventId + "/attend/" + userId;

        StringRequest req = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.optString("status");

                        if (status.equalsIgnoreCase("warning")) {
                            // User already attending
                            isInterested = true;
                            updateButton();
                        }
                        else if (status.equalsIgnoreCase("success")) {
                            // User was NOT attending, POST added them
                            // → Must immediately revert
                            revertAccidentalJoin();
                        }
                        Log.d("MESSAGE", status);

                    } catch (Exception ignored) {}
                },
                err -> Toast.makeText(requireContext(), "Check failed", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(req);
    }

    // Undo accidental join caused by POST-on-open
    private void revertAccidentalJoin() {
        String url = BASE + eventId + "/attend/" + userId;

        StringRequest req = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> {
                    isInterested = false;
                    updateButton();
                },
                err -> {
                    isInterested = false;
                    updateButton();
                }
        );

        Volley.newRequestQueue(requireContext()).add(req);
    }

    // ----------------------------------------------------------
    // JOIN EVENT (POST ON PRESS)
    // ----------------------------------------------------------
    private void joinEvent() {
        String url = BASE + eventId + "/attend/" + userId;

        StringRequest req = new StringRequest(
                Request.Method.POST,
                url,
                response -> {

                    try {
                        JSONObject json = new JSONObject(response);
                        String status = json.optString("status");

                        if (status.equalsIgnoreCase("success") ||
                                status.equalsIgnoreCase("warning")) {
                            isInterested = true;
                        }

                    } catch (Exception ignored) {}

                    updateButton();
                    loadAttendanceCount();
                },
                err -> Toast.makeText(requireContext(),
                        "Failed to join event", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(req);
    }

    // ----------------------------------------------------------
    // LEAVE EVENT (DELETE ON PRESS)
    // ----------------------------------------------------------
    private void leaveEvent() {
        String url = BASE + eventId + "/attend/" + userId;

        StringRequest req = new StringRequest(
                Request.Method.DELETE,
                url,
                response -> {
                    isInterested = false;
                    updateButton();
                    loadAttendanceCount();
                },
                err -> Toast.makeText(requireContext(),
                        "Failed to leave event", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(requireContext()).add(req);
    }

    // ----------------------------------------------------------
    // ATTENDANCE COUNT
    // ----------------------------------------------------------
    private void loadAttendanceCount() {
        String url = BASE + eventId + "/attendees";

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                json -> {
                    int count = json.optInt("attendeeCount", 0);
                    attendeeChip.setText(count + " going");
                },
                err -> attendeeChip.setText("0 going")
        );

        Volley.newRequestQueue(requireContext()).add(req);
    }

    // ----------------------------------------------------------
    // UI UPDATE
    // ----------------------------------------------------------
    private void updateButton() {
        if (isInterested) {
            interestButton.setText("Uninterested");
            interestButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.red)
            );
        } else {
            interestButton.setText("Interested");
            interestButton.setBackgroundTintList(
                    ContextCompat.getColorStateList(requireContext(), R.color.blue)
            );
        }
    }

//    TODO: MAKE sure this mapping is tested
    private void getFriendsAttendingEvent(){
        String url = "http://coms-3090-016.class.las.iastate.edu:8080/api/friends/" + userId + "/friendsAttending/" + eventId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        handlingFriendsSuccess(response);
                        Log.d("RESPONSE", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley Error", error.toString());
            }
        });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void handlingFriendsSuccess(JSONArray response){
        friends.clear();
        // response is a JsonArray so we loop through this
        // array to get the Friend JsonObject
        Log.d("SUCCESS",response.toString());
        for (int i = 0; i < response.length(); i++) {
            try {

                JSONObject friendObject = response.getJSONObject(i);
                friends.add(new Friend(friendObject));
            } catch (JSONException e) {
                Log.d("RESPONSE PARSE ERROR", e.toString());
            }
        }
        adapter.notifyDataSetChanged();
    }

    // ----------------------------------------------------------
    // DEBUG DIALOG
    // ----------------------------------------------------------
    private void showStatusDialog(String title, String content) {
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext());

        TextView tv = new TextView(requireContext());
        tv.setPadding(50, 40, 50, 40);
        tv.setTextSize(15f);
        tv.setText(content);

        ScrollView scroll = new ScrollView(requireContext());
        scroll.addView(tv);

        builder.setTitle(title);
        builder.setView(scroll);

        builder.setPositiveButton("Close", null);

        builder.setNeutralButton("Copy", (d, w) -> {
            android.content.ClipboardManager clipboard =
                    (android.content.ClipboardManager)
                            requireContext().getSystemService(requireContext().CLIPBOARD_SERVICE);

            android.content.ClipData clip =
                    android.content.ClipData.newPlainText("Server Response", content);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(requireContext(), "Copied", Toast.LENGTH_SHORT).show();
        });

        builder.show();
    }
}

