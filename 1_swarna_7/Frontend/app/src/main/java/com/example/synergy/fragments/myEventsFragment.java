package com.example.synergy.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.synergy.sheets.CreateEventSheet;
import com.example.synergy.adapters.EventAdapter;
import com.example.synergy.items.EventItem;
import com.example.synergy.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class myEventsFragment extends Fragment {

    RecyclerView recyclerView;
    EventAdapter adapter;
    Button createEventButton;
    String userDetailString;

    List<EventItem> eventList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_my_events, container,false);


        handleArguments(getArguments());
        setupUI(view);
        setupAdapter();
        try {
            JSONObject userDetailBody = new JSONObject(userDetailString);
            int userID = userDetailBody.getInt("id");
            makeMyEventRequest(userID);
        } catch (JSONException e) {
            Log.d("ERROR", e.toString());
        }

        handleListeners();

        return view;
    }

    private void handleListeners(){
        createEventButton.setOnClickListener(v -> {
            CreateEventSheet sheet = CreateEventSheet.newInstance(userDetailString);
            sheet.show(getParentFragmentManager(), "CreateEventSheet");
        });
    }

    private void setupAdapter(){
        try {
            adapter = new EventAdapter(requireContext(), eventList, null, true, userDetailString,
                    getParentFragmentManager());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        adapter.setOnEventDeletedListener(eventId -> {
            // Remove from list
            for (int i = 0; i < eventList.size(); i++) {
                if (eventList.get(i).getId() == eventId) {
                    eventList.remove(i);
                    break;
                }
            }

            adapter.notifyDataSetChanged();
        });
        recyclerView.setAdapter(adapter);
    }

    private void handleArguments(Bundle args){
        if (args != null) {
            userDetailString = args.getString("userDetails");
        }
    }

    private void setupUI(View view){
        recyclerView = view.findViewById(R.id.myRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        createEventButton = view.findViewById(R.id.createEvent);

    }


    private void populateEvents(JSONArray response) throws JSONException {

        List<EventItem> newList = new ArrayList<>();

        for (int i = 0; i < response.length(); i++) {
            JSONObject obj = response.getJSONObject(i);
            newList.add(new EventItem(
                    obj.getInt("id"),
                    obj.getString("eventName"),
                    obj.getString("eventTime"),
                    obj.getString("eventLocation"),
                    obj.getString("eventDate"),
                    obj.getString("description")
            ));
        }

        DiffUtil.DiffResult diffResult = handleDifferentResults(newList);

        eventList.clear();
        eventList.addAll(newList);
        adapter.userEvents = response;
        diffResult.dispatchUpdatesTo(adapter);

    }

    private DiffUtil.DiffResult handleDifferentResults(List<EventItem> newList){
        // Compute diff and update

        return DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() { return eventList.size(); }
            @Override
            public int getNewListSize() { return newList.size(); }
            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return eventList.get(oldItemPosition).getId() == newList.get(newItemPosition).getId();
            }
            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                EventItem oldItem = eventList.get(oldItemPosition);
                EventItem newItem = newList.get(newItemPosition);
                return oldItem.equals(newItem); // implement equals in EventItem
            }
        });
    }

    private void makeMyEventRequest(int id) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());


        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/" +id;

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
        queue.add(request);
    }


    private boolean eventsLoaded = false;

    @Override
    public void onResume() {
        super.onResume();

        if (!eventsLoaded) {
            try {
                JSONObject userDetailsBody = new JSONObject(userDetailString);
                int userID = userDetailsBody.getInt("id");
                makeMyEventRequest(userID);
                eventsLoaded = true;
            } catch (JSONException e) {
                Log.d("ERROR", e.toString());
            }
        }
    }

    public void refreshEvents() {
        try {
            JSONObject userDetailsBody = new JSONObject(userDetailString);
            int userID = userDetailsBody.getInt("id");
            makeMyEventRequest(userID);
        } catch (JSONException e) {
            Log.d("ERROR", e.toString());
        }
    }
}


