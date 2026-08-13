package com.example.synergy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.synergy.sheets.EditEventSheet;
import com.example.synergy.activities.EventAttendanceActivity;
import com.example.synergy.fragments.EventInfoFragment;
import com.example.synergy.items.EventItem;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class eventInvitesAdapter extends RecyclerView.Adapter<eventInvitesAdapter.EventViewHolder> {

    private final FragmentManager fragmentManager;
    private final Context context;
    private final List<EventItem> eventList;
    private final int userId;
    private final int friendId;
    private final String emailId;

    private List<EventItem> fullList;


    private final String userDetailString;

    private boolean showAttendees;





    public eventInvitesAdapter(Context context, List<EventItem> eventList,
                        String userDetailString, int friendId, FragmentManager fragmentManager) throws JSONException {
        this.context = context;
        this.eventList = eventList;


        this.userDetailString = userDetailString;
        this.fragmentManager = fragmentManager;

        JSONObject userDetailsTemp = new JSONObject(userDetailString);
        userId = userDetailsTemp.getInt("id");
        this.friendId = friendId;
        emailId = userDetailsTemp.getString("emailId");

    }
    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_button, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventItem item = eventList.get(position);

        int eventId = item.getId();

        setupUI(holder, item);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvite(eventId);
            }
        });







    }

    private void sendInvite(int eventId){
        String url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/"
                +eventId+ "/invite/" + userId + "/" + friendId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("error")){
                                Toast.makeText(context, "User already invited to this event", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(context, "Invite Sent", Toast.LENGTH_SHORT).show();
                            }
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

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }


    private void setupUI(EventViewHolder holder, EventItem item){
        holder.eventTitle.setText(item.getEventName());
        holder.eventDetails.setText(item.getEventTime() + " | "+ item.getEventDate() + " | " +
                item.getEventLocation());

    }





    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        LinearLayout eventCard;
        TextView eventTitle, eventDetails;
        Button deleteButton;
        Button attendees;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            eventCard = itemView.findViewById(R.id.eventCard);
            eventTitle = itemView.findViewById(R.id.textEventTitle);
            eventDetails = itemView.findViewById(R.id.textEventDetails);
            deleteButton = itemView.findViewById(R.id.deleteEventButton);
            attendees = itemView.findViewById(R.id.showAttendeesButton);
        }
    }


    //    Delete Event Listener to implement live updates
    public interface OnEventDeletedListener {
        void onEventDeleted(int eventId);
    }

    private OnEventDeletedListener deleteListener;

    public void setOnEventDeletedListener(OnEventDeletedListener listener) {
        this.deleteListener = listener;
    }
}