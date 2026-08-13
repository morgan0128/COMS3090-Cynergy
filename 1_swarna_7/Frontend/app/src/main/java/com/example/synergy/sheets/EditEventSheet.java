package com.example.synergy.sheets;

import android.annotation.SuppressLint;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EditEventSheet extends BottomSheetDialogFragment {

    private static final String ARG_USER_DETAILS = "user_details" ;
    private static final String ARG_EVENT_DETAILS = "event_details";
    EditText eventName;
    EditText eventLocation;
    EditText eventDate;
    EditText eventTime;
    Button editEventButton;
    TextView sheetTitle;

    EditText description;
    boolean createNodeForEvent;
    String userDetails;
    String eventDetails;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch createNode;
    boolean hasNode = false;
    String locationForEditedEvent;
    EditText editTags;
    EditText nodeDescription;
    int eventIdForEditedEvent;



    public static EditEventSheet newInstance(String userDetails, String eventDetails) {
        EditEventSheet fragment = new EditEventSheet();
        Bundle args = new Bundle();
        args.putString(ARG_USER_DETAILS, userDetails);
        args.putString(ARG_EVENT_DETAILS, eventDetails);
        fragment.setArguments(args);
        return fragment;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.create_event_sheet, container, false);



        if(getArguments() != null){
            userDetails = getArguments().getString(ARG_USER_DETAILS);

            eventDetails =getArguments().getString(ARG_EVENT_DETAILS);
        }

        editTags = root.findViewById(R.id.editTags);
        description = root.findViewById(R.id.editDescription);
        nodeDescription = root.findViewById(R.id.eventDescription);
        createNode = root.findViewById(R.id.toggleNode);
        sheetTitle = root.findViewById(R.id.textView5);
        eventName = root.findViewById(R.id.editEventName);
        eventLocation = root.findViewById(R.id.editEventLocation);
        eventDate = root.findViewById(R.id.editEventDate);
        eventTime = root.findViewById(R.id.editEventTime);
        sheetTitle.setText("Edit your Event!");

        try {
            JSONObject eventDetailObject = new JSONObject(eventDetails);
            JSONObject userDetailObject = new JSONObject(userDetails);
//          If event has a map node display options to edit map node, if not user
//          can choose to create a map node. Edit options would not show up then
            Log.d("EVENT DETAIL", eventDetailObject.toString());
            editTags.setEnabled(false);
            eventName.setText(eventDetailObject.getString("eventName"));
            eventLocation.setText(eventDetailObject.getString("eventLocation"));
            eventDate.setText(eventDetailObject.getString("eventDate"));
            eventTime.setText(eventDetailObject.getString("eventTime"));
            description.setText(eventDetailObject.getString("description"));





            editEventButton = root.findViewById(R.id.createButton);

            editEventButton.setText("Save");

            nodeDescription.setVisibility(View.GONE);

            getMapNode(eventDetailObject.getInt("id"));
            eventIdForEditedEvent = eventDetailObject.getInt("id");






            editEventButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        locationForEditedEvent = String.valueOf(eventLocation.getText());
                        makeEditRequest(eventDetailObject.getInt("id"), userDetailObject.getInt("id"));


                        if (createNodeForEvent){
                            // Make a POST request to create a map node
                            createNodeRequest();
                        }
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

//    TODO: Fix body of request
    private void makeEditRequest(int eventId, int userId) throws JSONException {
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/" + eventId
                + "?userId=" + userId;

        JSONObject jsonBody = new JSONObject();

        jsonBody.put("eventName", eventName.getText().toString());
        jsonBody.put("eventLocation", eventLocation.getText().toString());
        jsonBody.put("eventDate", eventDate.getText().toString());
        jsonBody.put("eventTime", eventTime.getText().toString());
        jsonBody.put("description", description.getText().toString());

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                server_url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        showStatusDialog("Success", "Event Updated");
                        hasNode = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        showStatusDialog("Failure", volleyError.toString());
                        hasNode = false;
                    }
                }

        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }


    private void getMapNode(int eventId){
        /**
         * Method used to carry out a get request to et all map nodes
         * This usually helps to populate map nodes, but in this context
         * we use it to get map nodes and validate it with the event that we
         * are currently editing.
         */
        String get_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/map/node/all";



        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                get_url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for (int i =0; i < jsonArray.length(); i++){
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                JSONArray eventsOnNode = obj.getJSONArray("events");
                                for (int j =0; j < eventsOnNode.length(); j++){
                                    if (eventsOnNode.getJSONObject(j).getInt("id") == eventId){
                                        hasNode = true;
                                        createNode.setChecked(true);
                                        createNode.setEnabled(false);
                                        Toast.makeText(requireContext(), "Found Node", Toast.LENGTH_SHORT).show();
                                        description.setVisibility(View.VISIBLE);
                                        break;
                                    }
                                }
                                if (hasNode){
                                    break;
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if (!hasNode){
                            createNode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                createNodeForEvent = isChecked;

                                if (isChecked){
                                    description.setVisibility(View.VISIBLE);
                                } else {
                                    description.setVisibility(View.GONE);
                                }

                            });
                        }


                    }
                },
                error -> {
                    showStatusDialog("Failure", "Map Node Fetch went wrong!");

                }

        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(request);
    }

    private void createNodeRequest() throws JSONException {
        /**
         * Function to make a request call to POST new event map node
         */
        String descriptionString = String.valueOf(description.getText());
        JSONObject jsonBody = new JSONObject();
        jsonBody.put("description", descriptionString);
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/map/node/create/";

        final Context context = requireContext(); // capture safely on main thread
        new Thread(() -> {
            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            try {
                List<Address> address = geocoder.getFromLocationName(locationForEditedEvent, 1);
                if (address != null && !address.isEmpty()) {
                    Address location = address.get(0);
                    String node_url = server_url + eventIdForEditedEvent + "/" + location.getLatitude() + "/" + location.getLongitude();

                    JsonObjectRequest request = new JsonObjectRequest(
                            Request.Method.POST,
                            node_url,
                            jsonBody,
                            response -> Toast.makeText(context, "Node Created", Toast.LENGTH_SHORT).show(),
                            volleyError -> {}

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


    private void showStatusDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }



}
