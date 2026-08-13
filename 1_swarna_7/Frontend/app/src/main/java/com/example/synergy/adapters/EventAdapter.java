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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

//Might need some extra work
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final FragmentManager fragmentManager;
    private final Context context;
    private final List<EventItem> eventList;
    private final int userId;
    private final String emailId;

    private List<EventItem> fullList;

    private final boolean showDelete;
    private final String userDetailString;

    private boolean showAttendees;
    public JSONArray userEvents;


    public  EventAdapter(Context context, List<EventItem> eventList, JSONArray userEvents, boolean showDelete,
                        String userDetailString, FragmentManager fragmentManager) throws JSONException {
        this(context, eventList, userEvents, showDelete, userDetailString, fragmentManager, false);

    }

    public EventAdapter(Context context, List<EventItem> eventList, JSONArray userEvents, boolean showDelete,
                        String userDetailString, FragmentManager fragmentManager, boolean showAttendees) throws JSONException {
        this.context = context;
        this.eventList = eventList;
        this.fullList = new ArrayList<>(eventList);
        this.showDelete = showDelete;
        this.userEvents = userEvents;
        this.userDetailString = userDetailString;
        this.fragmentManager = fragmentManager;
        this.showAttendees = showAttendees;

        JSONObject userDetailsTemp = new JSONObject(userDetailString);
        userId = userDetailsTemp.getInt("id");
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
        Set<Integer> userEventIds = new HashSet<>();
        boolean isMyEvents = false;

        setupUI(holder, item);
        collectUserEvents(userEventIds);

        // Then in onBindViewHolder:
        isMyEvents = userEventIds.contains(item.getId());
        handleIfMyEventOrNot(isMyEvents, item, holder);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text) {
        eventList.clear();

        if (text == null || text.trim().isEmpty()) {
            eventList.addAll(fullList);
        } else {
            String query = text.toLowerCase().trim();

            for (EventItem item : fullList) {
                if (item.getEventName().toLowerCase().contains(query) ||
                        item.getEventLocation().toLowerCase().contains(query) ||
                        item.getEventDate().toLowerCase().contains(query)) {

                    eventList.add(item);
                }
            }
        }

        notifyDataSetChanged();
    }

    public void updateFullList(List<EventItem> newFullList) {
        fullList.clear();
        fullList.addAll(newFullList);
    }


    private void collectUserEvents(Set<Integer> userEventIds){
        if (userEvents != null) {
            for (int i = 0; i < userEvents.length(); i++) {
                try {
                    JSONObject event = userEvents.getJSONObject(i);
                    userEventIds.add(event.getInt("id"));
                } catch (JSONException e) {
                    Log.d("ERROR", e.toString());
                }
            }
        }
    }
    private void setupUI(EventViewHolder holder, EventItem item){
        holder.eventTitle.setText(item.getEventName());
        holder.eventDetails.setText(item.getEventTime() + " | "+ item.getEventDate() + " | " +
                item.getEventLocation());

    }

    private void handleIfMyEventOrNot(Boolean isMyEvents, EventItem item, EventViewHolder holder){
        if (isMyEvents && showDelete) {
            myEventFunctionality(item, holder);
            holder.eventCard.setOnClickListener(v -> {
                startEditEventSheet(item);
            });

        } else {

            holder.deleteButton.setVisibility(View.GONE);
            if (showAttendees){
                holder.attendees.setVisibility(View.VISIBLE);
            } else {
                holder.attendees.setVisibility(View.GONE);
            }

            holder.attendees.setOnClickListener(v ->{
                Intent intent = new Intent(context, EventAttendanceActivity.class);
                intent.putExtra("event_id", item.getId());
                intent.putExtra("user_id", userId);
                intent.putExtra("event_name", item.getEventName());
                intent.putExtra("emailId", emailId);
                context.startActivity(intent);
            });

            holder.eventCard.setOnClickListener(v -> {
                startEventInfoFragment(item);
            });
        }
    }

    private void myEventFunctionality(EventItem item, EventViewHolder holder){
        holder.deleteButton.setVisibility(View.VISIBLE);
        holder.attendees.setVisibility(View.VISIBLE);

        holder.deleteButton.setOnClickListener(v ->
                makeDeleteRequest(item.getId(), userId, holder.deleteButton)
        );

        holder.attendees.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventAttendanceActivity.class);
            intent.putExtra("event_id", item.getId());
            intent.putExtra("user_id", userId);
            intent.putExtra("event_name", item.getEventName());
            intent.putExtra("emailId", emailId);
            context.startActivity(intent);
        });
    }
    private void startEditEventSheet(EventItem item){
        // Only create JSON for editing
        JSONObject eventDetails = new JSONObject();
        try {
            eventDetails.put("id", item.getId());
            eventDetails.put("eventName", item.getEventName());
            eventDetails.put("eventLocation", item.getEventLocation());
            eventDetails.put("eventDate", item.getEventDate());
            eventDetails.put("eventTime", item.getEventTime());
            eventDetails.put("description", item.getDescription());
        } catch (JSONException e) {
            Log.d("JSON ERROR", e.toString());
        }
        EditEventSheet sheet = EditEventSheet.newInstance(userDetailString, eventDetails.toString());
        sheet.show(fragmentManager, "EditEventSheet");
    }

    private void startEventInfoFragment(EventItem item){
        EventInfoFragment fragment = new EventInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("eventId", item.getId());
        bundle.putInt("userId", userId);
        bundle.putString("userDetails", userDetailString);
        bundle.putString("eventName", item.getEventName());
        bundle.putString("eventDate", item.getEventDate());
        bundle.putString("eventTime", item.getEventTime());
        bundle.putString("eventLocation", item.getEventLocation());
        fragment.setArguments(bundle);

        fragmentManager.beginTransaction()
                .replace(R.id.all_events_root, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void makeDeleteRequest(int eventId, int userId, Button deleteButton){
        deleteButton.setEnabled(false);
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/" + eventId +
                "?userId=" + userId;
        StringRequest request = new StringRequest(
                Request.Method.DELETE,
                server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        showStatusDialog();

                        if (deleteListener != null) {
                            deleteListener.onEventDeleted(eventId);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                deleteButton.setEnabled(true);
            }
        }

        );

        VolleySingleton.getInstance(this.context).addToRequestQueue(request);
    }


    private void showStatusDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Success")
                .setMessage("Object Deleted")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
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