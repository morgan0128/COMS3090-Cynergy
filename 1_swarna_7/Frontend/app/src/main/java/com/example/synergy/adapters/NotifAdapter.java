package com.example.synergy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.items.NotificationItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.ViewHolder>{


    private List<NotificationItem> notifications;
    private String userDetailString;
    private final Context context;


    public NotifAdapter(Context context, List<NotificationItem> notifications, String userDetailString){
        this.notifications = notifications;
        this.userDetailString = userDetailString;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationItem item = notifications.get(position);
        holder.title.setText(item.getTitle());
        holder.message.setText(item.getMessage());
        holder.time.setText(item.getStatus());

        if ("READ".equals(item.getStatus())) {
            holder.itemView.setAlpha(0.5f);
            holder.title.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.message.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
        } else {
            holder.itemView.setAlpha(1.0f);
        }

        int noti_id = item.getNotiId();

        if ("CLEARED".equals(item.getStatus())) {
            holder.itemView.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (item.getMessage().contains("invited")){
                showInviteDecisionDialog(
                );
            } else {
                if (!"READ".equals(item.getStatus())) { // Only mark if unread
                    try {
                        readNotification(noti_id, position);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }


        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, time;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            time = itemView.findViewById(R.id.notification_time);
        }


    }

    private void readNotification(int noti_id, int position) throws JSONException {
        String url = "http://coms-3090-016.class.las.iastate.edu:8080/api/notifications/" + noti_id + "/read";

        StringRequest arrayRequest = new StringRequest(
                Request.Method.PUT,
                url,
                response -> {

                    notifications.get(position).setStatus("READ");
                    notifyItemChanged(position);

                    /**
                     * TODO: Implement Response change
                     * the notification, which would include either friend info
                     * or event info which should take me to
                     * friend chat or event info page
                     */
                },
                error -> {
                    Toast.makeText(context, "Volley Error: " + error, Toast.LENGTH_SHORT).show();
                }
        );


        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(arrayRequest);

    }

    private void showInviteDecisionDialog(
            ) {
        new AlertDialog.Builder(context)
                .setTitle("Event Invitation")
                .setMessage("You were sent an Event Invite!")
                .setPositiveButton("Accept", (dialog, which) -> {
                    int inviteId = 0;
//                    eventInviteAccept(inviteId);
                    Toast.makeText(context, "Accepted Invite", Toast.LENGTH_SHORT).show();
                })

                .setNegativeButton("Reject", (dialog, which) -> {
                    int eventId = 0;
                    Toast.makeText(context, "Rejected Invite", Toast.LENGTH_SHORT).show();
//                    try {
//                        eventInviteReject(eventId);
//
//                    } catch (JSONException e) {
//                        throw new RuntimeException(e);
//                    }
                })
                .show();
    }

    private void eventInviteAccept(int inviteId){
        String url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/invite/" + inviteId + "/accept";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Invite Accepted", Toast.LENGTH_SHORT).show();
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

    private void eventInviteReject(int eventId) throws JSONException {
        JSONObject userDetailObject = new JSONObject(userDetailString);
        int receiverId = userDetailObject.getInt("id");
        String url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/invite/" + eventId + "/decline/" + receiverId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Invite rejected", Toast.LENGTH_SHORT).show();
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
}
