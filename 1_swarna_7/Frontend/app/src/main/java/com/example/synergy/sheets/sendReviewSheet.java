package com.example.synergy.sheets;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class sendReviewSheet extends BottomSheetDialogFragment {
    private static final String ARG_USER_DETAILS = "userDetails";

    private EditText etRating;
    private EditText etComment;
    private Button sendReviewButton;
    private static final String ARG_EVENT_ID= "eventId";

    public static sendReviewSheet newInstance(String userDetails, int eventId){
        sendReviewSheet sheet = new sendReviewSheet();
        Bundle args = new Bundle();
        args.putString(ARG_USER_DETAILS, userDetails);
        args.putInt(ARG_EVENT_ID, eventId);
        sheet.setArguments(args);

        return sheet;

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sheet_send_review, container, false);

        etRating = view.findViewById(R.id.etRating);
        etComment = view.findViewById(R.id.commentsTv);

        sendReviewButton = view.findViewById(R.id.sendReviewButton);
        assert getArguments() != null;
        int eventId = getArguments().getInt(ARG_EVENT_ID);
        String userDetails = getArguments().getString(ARG_USER_DETAILS);

        try {
            JSONObject obj = new JSONObject(userDetails);
            int userId = obj.getInt("id");
            sendReviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        sendRequest(eventId, userId);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });


        } catch (JSONException e) {
            throw new RuntimeException(e);
        }





        return view;
    }

    private void sendRequest(int eventId, int userId) throws JSONException {
        String url = "http://coms-3090-016.class.las.iastate.edu:8080/api/reviews/event/" + eventId + "/user/" + userId;
        JSONObject requestBody = new JSONObject();
        requestBody.put("rating", Integer.valueOf(etRating.getText().toString()));
        requestBody.put("comment", etComment.getText().toString());
        Log.d("REQUEST BODY", requestBody.toString());
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(requireContext(), "Review Submitted", Toast.LENGTH_SHORT).show();
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
