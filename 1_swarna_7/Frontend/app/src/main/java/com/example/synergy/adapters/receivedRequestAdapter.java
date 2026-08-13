package com.example.synergy.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.synergy.VolleySingleton;

import com.example.synergy.R;
import com.example.synergy.items.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class receivedRequestAdapter extends RecyclerView.Adapter<receivedRequestAdapter.receivedRequestViewHolder> {


    private final Context context;
    private List<User> users;
    private final String userDetailString;


    public receivedRequestAdapter(Context context, List<User> users, String userDetailString){
        this.context = context;
        this.users = users;
        this.userDetailString = userDetailString;
    }

    @NonNull
    @Override
    public receivedRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_received_request, parent, false);

        return new receivedRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull receivedRequestViewHolder holder, int position) {
        User friend = users.get(position);
        holder.userName.setText(friend.getName());

        holder.rejectFriend.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Decline Request")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        try {
                            rejectRequest(friend.getId());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss(); // just close the dialog
                    })
                    .show();

        });

        holder.acceptFriend.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Accept Request")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        try {
                            acceptRequest(friend.getId());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .setNegativeButton("No", (dialog, which) -> {
                        dialog.dismiss(); // just close the dialog
                    })
                    .show();
        });



        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "You clicked on " + friend.getName() + "!", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     *  Update the list based on the filtered list
     * @param filteredList : list that is returned after filtering
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<User> filteredList) {
        this.users = filteredList; // assuming your list variable is called userList
        notifyDataSetChanged();
    }

    public static class receivedRequestViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView rejectFriend;
        ImageView acceptFriend;

        public receivedRequestViewHolder(@NonNull View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.friendName);
            rejectFriend = itemView.findViewById(R.id.reject_button);
            acceptFriend = itemView.findViewById(R.id.accept_button);


        }
    }

    private void rejectRequest(int friendId) throws JSONException {
        JSONObject userDetails = new JSONObject(userDetailString);
        int userId = userDetails.getInt("id");
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/friends/decline/"
                + userId + "/" + friendId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                server_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(context, "Friend Request Rejected", Toast.LENGTH_SHORT).show();
                    }
                },
                volleyError -> {
                    Toast.makeText(context, volleyError.toString(), Toast.LENGTH_SHORT).show();
                }

        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void acceptRequest(int friendId) throws JSONException {
        JSONObject userDetails = new JSONObject(userDetailString);
        int userId = userDetails.getInt("id");
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/friends/accept/"
                + userId + "/" + friendId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                server_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(context, "Friend Added", Toast.LENGTH_SHORT).show();
                    }
                },
                volleyError -> {
                    Toast.makeText(context, volleyError.toString(), Toast.LENGTH_SHORT).show();
                }

        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
