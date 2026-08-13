package com.example.synergy.sheets;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.EventNodeAdapter;
import com.example.synergy.items.EventItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class mapEventListSheet extends BottomSheetDialogFragment {

    private RecyclerView recyclerView;
    private EventNodeAdapter adapter;
    private List<EventItem> eventList = new ArrayList<>();
    private Button delete_node;

    private static final String ARG_EVENTS = "events_list";
    private static final String ARG_USER_DETAILS = "userDetails";
    private static final String ARG_MAP_NODE_ID = "id";
    private String associatedEvents;
    private JSONObject userDetails;
    private JSONArray userEvents;
    private String userDetailsString;
    private int map_node_id;

    public static mapEventListSheet newInstance(JSONArray events, String user, int map_node_id) {
        mapEventListSheet fragment = new mapEventListSheet();
        Bundle args = new Bundle();
        args.putString(ARG_USER_DETAILS, user);
        args.putString(ARG_EVENTS, events.toString());
        args.putInt(ARG_MAP_NODE_ID, map_node_id);
        fragment.setArguments(args);

        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_events, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        delete_node = view.findViewById(R.id.delete_map_node);
        delete_node.setVisibility(View.VISIBLE);

        Bundle args = getArguments();
        if (args != null){
            associatedEvents = args.getString(ARG_EVENTS);
            Log.d("LIST map", associatedEvents.toString());
            Log.d("EVENTS FOR MAP", associatedEvents.toString());
            userDetailsString = args.getString(ARG_USER_DETAILS);
            map_node_id = args.getInt(ARG_MAP_NODE_ID);




            try {
                userDetails = new JSONObject(userDetailsString);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }
        try {
            adapter = new EventNodeAdapter(requireContext(), eventList, null, false, userDetailsString, getParentFragmentManager());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        recyclerView.setAdapter(adapter);

        try {
            populateEvents(new JSONArray(associatedEvents));
            makeMyEventRequest(userDetails.getInt("id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        delete_node.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    makeDeleteNodeRequest();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });




        return view;
    }

    private void makeMyEventRequest(int id) {
        RequestQueue queue = Volley.newRequestQueue(requireContext());


        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/" +id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, server_url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        userEvents = response;
                        adapter.userEvents = userEvents;
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        queue.add(request);
    }


    private void populateEvents(JSONArray response) {
        /**
         * Populate events
         */
        eventList.clear();
        try {
//            Takes in JSONArray object from response, loops over it
//            extracts event details and calls EventAdapter to create a event item
            JSONArray jsonArray = response;
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                eventList.add(new EventItem(
                        obj.getInt("id"),
                        obj.optString("eventName", "Untitled Event"),
                        obj.optString("eventTime", "00:00"),
                        obj.optString("eventLocation", "Unknown Location"),
                        obj.optString("eventDate", "1970-01-01"),
                        obj.optString("description", "No Description")
                ));
                Log.d("ADDED TO LIST", eventList.get(i).getEventName().toString());
            }
        } catch (Exception e) {
            Log.d("MAP LIST ERROR", e.toString());
        }

        adapter.updateFullList(eventList);
        adapter.notifyDataSetChanged();

    }

    private void makeDeleteNodeRequest() throws JSONException {

        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/map/node/delete/" + map_node_id;
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        showStatusDialog("Success", "Map Node Deleted");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                showStatusDialog("Failure", "Object didn't delete");
            }
        });

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);



    }

    private void showStatusDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}