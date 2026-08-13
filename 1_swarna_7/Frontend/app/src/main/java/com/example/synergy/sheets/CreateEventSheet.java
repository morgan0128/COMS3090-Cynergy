package com.example.synergy.sheets;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CreateEventSheet extends BottomSheetDialogFragment {


    private static final String ARG_USER_DETAILS = "user_details" ;
    EditText eventName;
    EditText eventLocation;
    EditText eventDate;
    EditText eventTime;
    EditText eventDesc;
    Button createEventButton;
    EditText nodeDescription;

    EditText eventTags;
    boolean[] selectedTags;
    List<String> chosenTags = new ArrayList<>();

//    Supress warning because we are using CompatSwitch not the default
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch createNode;
    String userDetails;
    boolean createNodeForEvent;
    String eventIdForCreatedEvent;
    String locationForCreatedEvent;
    List<String> tagList;

    public static CreateEventSheet newInstance(String userDetails) {
        CreateEventSheet fragment = new CreateEventSheet();
        Bundle args = new Bundle();
        args.putString(ARG_USER_DETAILS, userDetails);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.create_event_sheet, container, false);

        tagList = new ArrayList<String>();
        getTags();
        createNodeForEvent = false;


        if(getArguments() != null){
            userDetails = getArguments().getString(ARG_USER_DETAILS);
        }



        nodeDescription = root.findViewById(R.id.eventDescription);
        createNode = root.findViewById(R.id.toggleNode);
        eventName = root.findViewById(R.id.editEventName);
        eventLocation = root.findViewById(R.id.editEventLocation);
        eventDate = root.findViewById(R.id.editEventDate);
        eventTime = root.findViewById(R.id.editEventTime);
        eventDesc = root.findViewById(R.id.editDescription);
        eventTags = root.findViewById(R.id.editTags);
        createEventButton = root.findViewById(R.id.createButton);

        createNode.setChecked(false);
        nodeDescription.setVisibility(View.GONE);

        createNode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            createNodeForEvent = isChecked;
            if(isChecked){
                nodeDescription.setVisibility(View.VISIBLE);
            } else {
                nodeDescription.setVisibility(View.GONE);
            }
        });



        try {
            JSONObject userDetailObject = new JSONObject(userDetails);



            createEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        makeCreateRequest(userDetailObject.getInt("id"));

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return root;
    }

    private void createNodeRequest() throws JSONException {
        String descriptionString = String.valueOf(nodeDescription.getText());
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("description", descriptionString);
        final Context context = requireContext(); // capture safely on main thread
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> address = geocoder.getFromLocationName(locationForCreatedEvent, 1);
                if (address != null && !address.isEmpty()) {
                    Address location = address.get(0);
                    String node_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/map/node/create/"
                            + eventIdForCreatedEvent + "/" + location.getLatitude() + "/" + location.getLongitude();

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            node_url,
                            jsonBody,
                            response -> Toast.makeText(context, "Node Created", Toast.LENGTH_SHORT).show(),
                            error -> Toast.makeText(context, "Node not created", Toast.LENGTH_SHORT).show()
                    );

                    VolleySingleton.getInstance(context).addToRequestQueue(request);
                } else {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(context, "Could not find location", Toast.LENGTH_SHORT).show()
                    );
                }
            } catch (IOException e) {
                Log.d("ERROR", e.toString());
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(context, "Geocoder error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }
        }).start();
    }

//    TODO: Currently sending an incorrect json body
    private void makeCreateRequest(int userID) throws JSONException {

        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/user/" + userID;
        JSONArray tagArray = new JSONArray();

        JSONObject event = new JSONObject();
        event.put("eventName", eventName.getText().toString());
        event.put("eventLocation", eventLocation.getText().toString());
        event.put("eventDate", eventDate.getText().toString());
        event.put("eventTime", eventTime.getText().toString());
        event.put("description", eventDesc.getText().toString());
        for (String tag : chosenTags) tagArray.put(tag);
        event.put("tags", tagArray);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                server_url,
                event,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        showStatusDialog("Success", String.valueOf(jsonObject));
                        try {
                            JSONObject eventObject = jsonObject.getJSONObject("event");
                            eventIdForCreatedEvent = eventObject.getString("id");
                            locationForCreatedEvent = eventObject.getString("eventLocation");

                            if (createNodeForEvent){
                                createNodeRequest();
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showStatusDialog("Failure", String.valueOf(volleyError));
                    }
                }

        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void setupTagSelector() {
        if (tagList == null || tagList.isEmpty()) return;

        selectedTags = new boolean[tagList.size()];

        eventTags.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Select Tags");

            builder.setMultiChoiceItems(tagList.toArray(new String[0]), selectedTags,
                    (dialog, index, isChecked) -> {
                        if (isChecked) {
                            if (!chosenTags.contains(tagList.get(index))) {
                                chosenTags.add(tagList.get(index));
                            }
                        } else {
                            chosenTags.remove(tagList.get(index));
                        }
                    });

            builder.setPositiveButton("OK", (dialog, which) -> {
                String joined = String.join(", ", chosenTags);
                eventTags.setText(joined);
            });

            builder.setNegativeButton("Cancel", null);

            builder.show();
        });
    }

    private void getTags(){
        String url = "http://coms-3090-016.class.las.iastate.edu:8080/api/tags";

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("RESPONSE", response.toString());
                        for (int i =0; i< response.length(); i++){
                            try {
                                tagList.add(response.getString(i));
                            } catch (JSONException e) {
                                Log.d("JSON ERROR", e.toString());
                            }
                        }
                        setupTagSelector();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY ERROR", error.toString());
                    }
                });

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);

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
