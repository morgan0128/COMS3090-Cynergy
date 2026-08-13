package com.example.synergy.fragments;

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
import com.android.volley.toolbox.Volley;
import com.example.synergy.R;
import com.example.synergy.adapters.myEventsReviewAdapter;
import com.example.synergy.items.EventItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class myEventsReviewsFragment extends Fragment {

    private static final String ARGS_USER_DETAILS= "userDetails";
    private String userDetails;
    private RecyclerView myEventsRv;
    private List<EventItem> userEvents;
    private myEventsReviewAdapter adapter;

    public myEventsReviewsFragment() {
        // Required empty public constructor
    }


    public static myEventsReviewsFragment newInstance(String userDetails) {
        myEventsReviewsFragment fragment = new myEventsReviewsFragment();
        Bundle args = new Bundle();
        args.putString(ARGS_USER_DETAILS, userDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userDetails = getArguments().getString(ARGS_USER_DETAILS);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_my_events_reviews, container, false);



        userEvents = new ArrayList<>();
        myEventsRv = view.findViewById(R.id.myEventRv);
        myEventsRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        try {
            JSONObject userObject = new JSONObject(userDetails);
            getUserEvents(userObject.getInt("id"));
        } catch (JSONException e) {
            Log.d("PARSE ERROR", e.toString());
        }




        return view;

    }

    private void setUpAdapter(){
        adapter = new myEventsReviewAdapter(requireContext(), userDetails, userEvents, getParentFragmentManager());
        myEventsRv.setAdapter(adapter);
    }

    private void getUserEvents(int id){
        RequestQueue queue = Volley.newRequestQueue(requireContext());


        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/" +id;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, server_url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            for (int i =0 ; i < response.length(); i++){
                                JSONObject obj = (JSONObject) response.get(i);
                                Log.d("RESPONSE", obj.toString());
                                userEvents.add(new EventItem(
                                        obj.getInt("id"),
                                        obj.getString("eventName"),
                                        obj.getString("eventTime"),
                                        obj.getString("eventLocation"),
                                        obj.getString("eventDate"),
                                        obj.getString("description")
                                ));
                            }

                            setUpAdapter();
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
}