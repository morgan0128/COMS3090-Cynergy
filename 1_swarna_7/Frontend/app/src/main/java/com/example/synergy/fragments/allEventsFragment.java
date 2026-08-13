package com.example.synergy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.EventAdapter;
import com.example.synergy.items.EventItem;
import com.example.synergy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class allEventsFragment extends Fragment {
    private RecyclerView recyclerView;
    private EventAdapter adapter;
    private SearchView eventSv;
    private final List<EventItem> eventList = new ArrayList<>();

    private  JSONObject userDetails;
    private String userDetailString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_events, container, false);


        recyclerView = view.findViewById(R.id.recyclerView);
        eventSv = view.findViewById(R.id.searchEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Bundle args = getArguments();
        handleArguments(args);

        makeAllEventRequest();

        searchViewFunctionality();


        return view;

    }

    private void handleArguments(Bundle args){
        if (args != null) {
            userDetailString = args.getString("userDetails");
            try {
                userDetails = new JSONObject(userDetailString);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void searchViewFunctionality(){
        eventSv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (adapter != null) adapter.filter(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (adapter != null) adapter.filter(s);
                return true;
            }
        });
    }

    private boolean eventsLoaded = false;

    @Override
    public void onResume() {
        super.onResume();
        if (!eventsLoaded) {
            makeAllEventRequest();
            eventsLoaded = true;
        }
    }

    private void populateEvents(JSONArray response) throws JSONException {
        eventList.clear();
        for (int i = 0; i < response.length(); i++) {
            JSONObject obj = response.getJSONObject(i);
            eventList.add(new EventItem(
                    obj.getInt("id"),
                    obj.getString("eventName"),
                    obj.getString("eventTime"),
                    obj.getString("eventLocation"),
                    obj.getString("eventDate"),
                    obj.getString("description")
            ));
        }

        // Only fetch user events once
        if (adapter == null) {
            getUserEvents(userDetails.getInt("id"));
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void makeAllEventRequest() {
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events";

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, server_url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            populateEvents(response);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void getUserEvents(int userId){
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/" + userId;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET,
                server_url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                        try {
                            adapter = new EventAdapter(requireContext(), eventList, jsonArray, false, userDetailString,
                                    getParentFragmentManager());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        recyclerView.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        JSONArray userEvents = null;
                    }
                });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }



}
