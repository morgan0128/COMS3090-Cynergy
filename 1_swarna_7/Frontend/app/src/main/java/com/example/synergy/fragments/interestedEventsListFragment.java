package com.example.synergy.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.EventAdapter;
import com.example.synergy.items.EventItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class interestedEventsListFragment extends Fragment {

    private RecyclerView recyclerView;

    private SearchView eventSv;
    private EventAdapter adapter;
    private final List<EventItem> eventList = new ArrayList<>();


    private String userDetailString;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_interested_events, container, false);

        setupUI(view);

        handleArguments(getArguments());
        searchViewFunctionality();
        setupAdapter();

        try {
            JSONObject userDetailBody = new JSONObject(userDetailString);
            int userID = userDetailBody.getInt("id");
            getUserInterestedEvents(userID);
        } catch (JSONException e) {
            Log.d("ERROR", e.toString());
        }

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setupAdapter(){
        try {
            adapter = new EventAdapter(requireContext(), eventList, null, false, userDetailString,
                    getParentFragmentManager(), true);
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

    private void handleArguments(Bundle args){
        if (args != null) {
            userDetailString = args.getString("userDetails");

        }
    }
    private void setupUI(View view){
        recyclerView = view.findViewById(R.id.recyclerView);
        eventSv = view.findViewById(R.id.searchEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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

        DiffUtil.DiffResult diffResult = handleDifferentResult(newList);

        eventList.clear();
        eventList.addAll(newList);
        adapter.userEvents = response;
        diffResult.dispatchUpdatesTo(adapter);
        adapter.updateFullList(newList);
    }


    private DiffUtil.DiffResult handleDifferentResult(List<EventItem> newList){
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
                        Log.d("ERROR", volleyError.toString());
                    }
                });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }


    private boolean eventsLoaded = false;

    @Override
    public void onResume() {
        super.onResume();

        if (!eventsLoaded) {
            try {
                JSONObject userDetailsBody = new JSONObject(userDetailString);
                int userID = userDetailsBody.getInt("id");
                getUserInterestedEvents(userID);
                eventsLoaded = true;
            } catch (JSONException e) {
                Log.d("ERROR", e.toString());
            }
        }
    }

}
