package com.example.synergy.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.items.TicketApi;

import org.json.JSONException;
import org.json.JSONObject;

public class EventIssueFragment extends Fragment {

    private static final String ARG_USER_ID = "userId"; // this is your sponsorId
    private static final String TAG = "EventIssueFragment";

    private EditText eventNameInput;
    private EditText eventLocationInput;
    private EditText eventDateInput;
    private EditText eventTimeInput;
    private Button  submitButton;

    // sponsorId == userId passed in from the Activity
    private int sponsorId = -1;

    public static EventIssueFragment newInstance(int userId) {
        EventIssueFragment fragment = new EventIssueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_event_issue_form, container, false);

        eventNameInput     = view.findViewById(R.id.eventNameInput);
        eventLocationInput = view.findViewById(R.id.eventLocationInput);
        eventDateInput     = view.findViewById(R.id.eventDateInput);
        eventTimeInput     = view.findViewById(R.id.eventTimeInput);
        submitButton       = view.findViewById(R.id.submitEventIssueBtn);

        if (getArguments() != null) {
            sponsorId = getArguments().getInt(ARG_USER_ID, -1);
        }

        submitButton.setOnClickListener(v -> submitEventIssue());

        return view;
    }

    private void submitEventIssue() {
        if (sponsorId <= 0) {
            Toast.makeText(requireContext(), "Missing sponsor id", Toast.LENGTH_SHORT).show();
            return;
        }

        String name     = eventNameInput.getText().toString().trim();
        String location = eventLocationInput.getText().toString().trim();
        String date     = eventDateInput.getText().toString().trim();
        String time     = eventTimeInput.getText().toString().trim();

        if (TextUtils.isEmpty(name) ||
                TextUtils.isEmpty(location) ||
                TextUtils.isEmpty(date) ||
                TextUtils.isEmpty(time)) {

            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("eventName",     name);
            body.put("eventLocation", location);
            body.put("eventDate",     date);
            body.put("eventTime",     time);
            body.put("sponsorId",     sponsorId);
        } catch (JSONException e) {
            Log.e(TAG, "JSON build error", e);
            Toast.makeText(requireContext(), "Failed to build request body", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = TicketApi.POST_APPROVE_EVENT;

        Log.d(TAG, "submit body = " + body);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    Toast.makeText(requireContext(), "Event approval request sent", Toast.LENGTH_SHORT).show();
                    clearFields();
                },
                error -> {
                    Log.e(TAG, "Request failed", error);
                    Toast.makeText(requireContext(), "Failed: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void clearFields() {
        eventNameInput.setText("");
        eventLocationInput.setText("");
        eventDateInput.setText("");
        eventTimeInput.setText("");
    }
}

