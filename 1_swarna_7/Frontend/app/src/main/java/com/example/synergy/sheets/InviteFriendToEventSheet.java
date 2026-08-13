package com.example.synergy.sheets;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.EventAdapter;
import com.example.synergy.adapters.eventInvitesAdapter;
import com.example.synergy.items.EventItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InviteFriendToEventSheet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InviteFriendToEventSheet extends BottomSheetDialogFragment {

    private RecyclerView event_invites_rv;

    private eventInvitesAdapter adapter;
    private int friendId;
    private final List<EventItem> eventList = new ArrayList<>();
    private String userDetailString;
    private static final String ARG_USER_DETAILS = "userDetails";
    private static final String ARG_FRIEND_ID = "friendId";


    public InviteFriendToEventSheet() {
        // Required empty public constructor
    }


    public static InviteFriendToEventSheet newInstance(String userDetails, int friendId) {
        InviteFriendToEventSheet fragment = new InviteFriendToEventSheet();
        Bundle args = new Bundle();
        args.putString(ARG_USER_DETAILS, userDetails);
        args.putInt(ARG_FRIEND_ID, friendId);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userDetailString = getArguments().getString(ARG_USER_DETAILS);
            friendId = getArguments().getInt(ARG_FRIEND_ID);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_invite_friend_to_event_sheet, container, false);
        // Inflate the layout for this fragment

        event_invites_rv = view.findViewById(R.id.eventInviteRv);
        event_invites_rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        eventList.clear();
        try {
            adapter = new eventInvitesAdapter(requireContext(), eventList,
                    userDetailString, friendId, getParentFragmentManager());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        event_invites_rv.setAdapter(adapter);

        try {
            JSONObject user = new JSONObject(userDetailString);
            int userId = user.getInt("id");


            makeMyEventRequest(userId);
            getUserInterestedEvents(userId);

        } catch (JSONException e) {
            Log.d("JSON PARSE ERROR", e.toString());
        }



        return view;
    }

    private void populateEvents(JSONArray jsonArray) throws JSONException {


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


    }

    private void makeMyEventRequest(int id) {


        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/" +id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, server_url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            populateEvents(response);
                            adapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void getUserInterestedEvents(int userId){
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/" + userId + "/interested";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                server_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            JSONArray attending = jsonObject.getJSONArray("attendingEvents");
                            populateEvents(attending);
                            adapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        JSONArray userEvents = null;
                        Log.d("ERROR", volleyError.toString());
                    }
                });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

}