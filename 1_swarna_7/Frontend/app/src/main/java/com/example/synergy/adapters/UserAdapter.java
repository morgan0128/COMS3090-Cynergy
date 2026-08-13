package com.example.synergy.adapters;

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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.VolleySingleton;
import com.example.synergy.items.Friend;
import com.example.synergy.R;
import com.example.synergy.items.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {


    private Context context;
    private List<User> users;
    private String userDetailString;

    public interface OnUserActionListener {
        void onFriendRequestSent(int friendId);
    }

    private OnUserActionListener listener;

    public void setOnUserActionListener(OnUserActionListener listener) {
        this.listener = listener;
    }

    public UserAdapter(Context context, List<User> users, String userDetailString){
        this.context = context;
        this.users = users;
        this.userDetailString = userDetailString;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);

        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User friend = users.get(position);
        holder.userName.setText(friend.getName());

        holder.addFriend.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Friend Request")
                    .setMessage("Send a friend request?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        int friendId = friend.getId();
                        try {
                            putFriendRequest(friendId);
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

            Toast.makeText(context, "You clicked " + friend.getName() + "!", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView addFriend;

        public UserViewHolder(@NonNull View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.friendName);
            addFriend = itemView.findViewById(R.id.addFriendButton);

        }
    }

    public void updateList(List<User> filteredList) {
        this.users = filteredList; // assuming your list variable is called userList
        notifyDataSetChanged();
    }

    private void putFriendRequest(int friendId) throws JSONException {
        JSONObject userDetails = new JSONObject(userDetailString);
        int userId = userDetails.getInt("id");
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/friends/request/" + userId
                + "/" + friendId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                server_url,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(context, "User befriended", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onFriendRequestSent(friendId);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(context, String.valueOf(volleyError), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }

    private void showStatusDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
