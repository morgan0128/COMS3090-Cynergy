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
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.myEventsReviewAdapter;
import com.example.synergy.adapters.reviewAdapter;
import com.example.synergy.items.EventItem;
import com.example.synergy.items.review;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class myEventReviewsSheet extends BottomSheetDialogFragment {
    private static final String ARG_USER_DETAILS = "userDetails";
    private static final String ARG_EVENT_ID = "eventIds";
    private static final String ARG_EVENT_NAME = "eventName";
    private reviewAdapter adapter;
    private String userDetails;

    private int eventId;
    private String eventName;
    private TextView eventNameTv;
    private RecyclerView listOfReviewsRv;


    public static myEventReviewsSheet newInstance(String userDetails, int eventId, String eventName) {
        myEventReviewsSheet fragment = new myEventReviewsSheet();
        Bundle args = new Bundle();
        args.putString(ARG_USER_DETAILS, userDetails);
        args.putInt(ARG_EVENT_ID, eventId);
        args.putString(ARG_EVENT_NAME, eventName);
        fragment.setArguments(args);

        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_my_event_reviews, container, false);



        eventNameTv = view.findViewById(R.id.my_event_name);
        listOfReviewsRv = view.findViewById(R.id.list_of_reviews_rv);
        listOfReviewsRv.setLayoutManager(new LinearLayoutManager(requireContext()));

        assert getArguments() != null;
        userDetails = getArguments().getString(ARG_USER_DETAILS);


        eventName = getArguments().getString(ARG_EVENT_NAME);
        eventId = getArguments().getInt(ARG_EVENT_ID);

        eventNameTv.setText(eventName);
        getReviews(eventId);


        return view;
    }

    private void setUpAdapter(JSONArray reviewList){
        Log.d("REVIEWS", reviewList.toString());
        adapter = new reviewAdapter(requireContext(), userDetails, reviewList);
        listOfReviewsRv.setAdapter(adapter);
    }


    private void getReviews(int eventId){
        String url= "http://coms-3090-016.class.las.iastate.edu:8080/api/reviews/event/" + eventId + "/reviews";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            setUpAdapter(response.getJSONArray("reviews"));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }


                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY ERROR", error.toString());
                    }
                }

        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }
}
