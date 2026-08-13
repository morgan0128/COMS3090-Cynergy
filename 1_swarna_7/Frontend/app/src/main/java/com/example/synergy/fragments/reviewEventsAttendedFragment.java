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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.myEventsReviewAdapter;
import com.example.synergy.adapters.reviewEventsAttendedAdapter;
import com.example.synergy.items.EventItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link reviewEventsAttendedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class reviewEventsAttendedFragment extends Fragment {

    public List<EventItem> attendedEvents;
    private RecyclerView attendedEventsRv;
    private reviewEventsAttendedAdapter adapter;
    private static final String ARG_USER_DETAILS = "userDetails";
    private String userDetails;

    public reviewEventsAttendedFragment() {
        // Required empty public constructor
    }



    public static reviewEventsAttendedFragment newInstance(String userDetails) {
        reviewEventsAttendedFragment fragment = new reviewEventsAttendedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_DETAILS, userDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.userDetails = getArguments().getString(ARG_USER_DETAILS);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_review_events_attended, container, false);

        attendedEvents = new ArrayList<>();
        attendedEventsRv = view.findViewById(R.id.attendedEventsRv);
        attendedEventsRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        try {
            JSONObject userObject = new JSONObject(userDetails);
            fetchAttendingEvents(userObject.getInt("id"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
;

        return view;


    }

    public void setUpAdapter(){
        adapter = new reviewEventsAttendedAdapter(requireContext(), userDetails, attendedEvents, getParentFragmentManager());
        attendedEventsRv.setAdapter(adapter);
    }


    private void fetchAttendingEvents(int userId){
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/" + userId + "/interested";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                server_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        try {
                            JSONArray attending = jsonObject.getJSONArray("attendingEvents");
                            for (int i =0 ; i < attending.length(); i++){
                                JSONObject obj = (JSONObject) attending.get(i);
                                Log.d("RESPONSE", obj.toString());
                                attendedEvents.add(new EventItem(
                                        obj.getInt("id"),
                                        obj.getString("eventName"),
                                        obj.getString("eventTime"),
                                        obj.getString("eventLocation"),
                                        obj.getString("eventDate"),
                                        obj.getString("description")
                                ));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        setUpAdapter();

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
}