package com.example.synergy.sheets;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.EventAdapter;
import com.example.synergy.items.EventItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class friendsEventListSheet extends BottomSheetDialogFragment {
    private RecyclerView friends_events_rv;

    private final List<EventItem> eventList = new ArrayList<>();
    private String userDetailString;
    private TextView friendName_tv;
    private static final String ARG_USER_DETAILS = "userDetails";
    private static final String ARG_FRIEND_ID= "friendId";
    private int friendId;

    public static friendsEventListSheet newInstance(String user, int friendId) {
        friendsEventListSheet fragment = new friendsEventListSheet();
        Bundle args = new Bundle();
        args.putString(ARG_USER_DETAILS, user);
        args.putInt(ARG_FRIEND_ID, friendId);

        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_friend_events, container, false);

        friends_events_rv = view.findViewById(R.id.recyclerView);
        friendName_tv = view.findViewById(R.id.friendName);
        friends_events_rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        handleRequestSetup();


        return view;
    }

    private void handleRequestSetup(){
        Bundle args = getArguments();
        if (args!= null){
            userDetailString = args.getString(ARG_USER_DETAILS);
            friendId = args.getInt(ARG_FRIEND_ID);
            JSONObject userDetails = null;
            try {
                userDetails = new JSONObject(userDetailString);
                int userId = userDetails.getInt("id");
                getFriendsEventsRequest(userId);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private void getFriendsEventsRequest(int userId){
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/friends/" + userId+ "/friendsInterestedEvents";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                server_url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("RESPONSE", response.toString());
                        for (int i =0; i < response.length(); i++){
                            try {
                                JSONObject friendDetail = (JSONObject) response.get(i);
                                if (friendDetail.getInt("friendId") == friendId){
                                    handleFriendEvents(friendDetail);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VOLLEY ERROR", error.toString());
            }
        });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void populateEvents(JSONArray jsonArray) throws JSONException {
        eventList.clear();

        try {
            // Takes in JSONArray object from response, loops over it
            // extracts event details and calls EventAdapter to create a event item
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                eventList.add(new EventItem(
                        obj.getInt("id"),
                        obj.getString("eventName"),
                        obj.getString("eventTime"),
                        obj.getString("eventLocation"),
                        obj.getString("eventDate"),
                        obj.getString("description")
                ));
            }
        } catch (Exception e) {
            Log.d("ERROR", e.toString());
        }

        EventAdapter friends_events_adapter = new EventAdapter(requireContext(), eventList, null,
                false, userDetailString, getParentFragmentManager());
        friends_events_rv.setAdapter(friends_events_adapter);

    }

    private void handleFriendEvents(JSONObject friendDetails) throws JSONException {
        String friendUsername = friendDetails.getString("friendUsername");
        friendName_tv.setText(friendUsername + "'s Events");

        JSONObject interestedEvents = friendDetails.getJSONObject("interestedEvents");
        JSONArray attending = interestedEvents.getJSONArray("attendingEvents");
        populateEvents(attending);

    }

}
