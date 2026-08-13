package com.example.synergy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.items.EventInvite;
import com.example.synergy.sheets.InviteFriendToEventSheet;

import org.json.JSONObject;

import java.util.List;

public class InviteFriendAdapter extends RecyclerView.Adapter<InviteFriendAdapter.EventInviteViewHolder>{

    private final Context context;
    private List<EventInvite> invites;
    private final String userDetailStirng;

    public InviteFriendAdapter(Context context, List<EventInvite> invites, String userDetailStirng) {
        this.context = context;
        this.invites = invites;
        this.userDetailStirng = userDetailStirng;
    }

    @NonNull
    @Override
    public EventInviteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event_invite, parent, false);

        return new EventInviteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventInviteViewHolder holder, int position) {
        EventInvite invite = invites.get(position);
        holder.userName.setText(invite.getFriend().getName());

        int friendId = invite.getFriend().getId();


        handleButtonClicks(holder, friendId);

    }

    private void handleButtonClicks( EventInviteViewHolder holder, int friendId){


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteFriendToEventSheet sheet = InviteFriendToEventSheet
                        .newInstance(userDetailStirng, friendId);
                sheet.show(((FragmentActivity) context).getSupportFragmentManager(),
                        "InviteFriendToEventSheet");

            }
        });

    }

    @Override
    public int getItemCount() {
        return this.invites.size();
    }

    public static class EventInviteViewHolder extends RecyclerView.ViewHolder {
        TextView userName;


        public EventInviteViewHolder(@NonNull View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.friendEventName);

        }
    }

    private void acceptInvite(int inviteId){
        String url = "http://coms-3090-016.class.las.iastate.edu:8080/api/events/invite/" + inviteId + "/accept";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("VOLLEY ERROR", error.toString());
                    }
                }
        );
    }
}
