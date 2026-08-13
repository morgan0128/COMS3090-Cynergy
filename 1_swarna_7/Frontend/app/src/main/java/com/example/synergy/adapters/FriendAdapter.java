package com.example.synergy.adapters;



import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.VolleySingleton;

import com.example.synergy.activities.FriendChatActivity;

import com.example.synergy.items.Friend;
import com.example.synergy.R;

import com.example.synergy.sheets.friendsEventListSheet;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {


    private final Context context;
    private List<Friend> friends;

    private final int userId;
    private final String userDetailString;

    /**
     * Initializes the Friend Adapter, which handles populating the list on
     * the fragment
     *
     * @param context : The context of the parent activity
     * @param friends : The list of friends that is returned from a HTTP Request
     * @param userDetailString : User Detail that is passed during login
     */
    public FriendAdapter(Context context, List<Friend> friends, String userDetailString){
        this.context = context;
        this.friends = friends;
        this.userDetailString = userDetailString;

        try {
            this.userId = new JSONObject(userDetailString).getInt("id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Initializesx the Friend item view holder, so that the layout for a friend item
     * on the list is inflated
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return FriendViewHolder
     */
    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);

        return new FriendViewHolder(view);
    }

    /**
     *  Provide functionality for the currently bind Friend Item on the list.
     *
     * @param holder : The holder of the friend list
     * @param position : position of the selected Friend Item on the holder
     */
    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friends.get(position);
        holder.friendName.setText(friend.getName());


        holder.unfriend.setOnClickListener(v -> {
            handleUnfriendAction(friend, position);

        });

        holder.friendEvents.setOnClickListener(v -> {
            friendsEventListSheet sheet = friendsEventListSheet.newInstance(userDetailString, friend.getId());
            FragmentActivity activity = (FragmentActivity) context;
            sheet.show(activity.getSupportFragmentManager(), "friendsEventListSheet");
        });



        holder.itemView.setOnClickListener(v -> {
            getFriendChats(userId, friend);
        });

    }

    private void startChat(int chatroomId, Friend friend){
        Toast.makeText(context, "Chat clicked for " + friend.getName(), Toast.LENGTH_SHORT).show();
        Intent i = new Intent(context, FriendChatActivity.class);
        i.putExtra("friend_name", friend.getName());
        i.putExtra("friend_id", friend.getId());
        i.putExtra("chatroom_id", chatroomId);
        i.putExtra("userId",userId);
        context.startActivity(i);

    }

    private void handleUnfriendAction(Friend friend, int position){
        new AlertDialog.Builder(context)
                .setTitle("Unfriend User")
                .setMessage("Are you sure you want to unfriend this person?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    try {
                        unfriendRequest(friend.getId(), position);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss(); // just close the dialog
                })
                .show();
    }

    /**
     *  Update the list based on the filtered list
     * @param filteredList : list that is returned after filtering
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<Friend> filteredList) {
        this.friends = filteredList; // assuming your list variable is called userList
        notifyDataSetChanged();
    }

    /**
     *  Get number of friend items on the list
     * @return size of the list
     */
    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView friendName;
        ImageView unfriend;
        ImageView friendEvents;

        /**
         *
         */
        public FriendViewHolder(@NonNull View itemView){
            super(itemView);
            friendName = itemView.findViewById(R.id.friendName);
            unfriend = itemView.findViewById(R.id.unfriendButton);
            friendEvents = itemView.findViewById(R.id.interestedEventsButton);

        }
    }

    /**
     *  A server request that removes the friend from the user's table which
     *  causes the friends list to be updated and filtered. Returns a JSOnObject,
     *  which isn't relevant on successful response it is removed from the list
     *  and the UI is updated
     *
     * @param friendId : friend to be removed
     * @param position : position of the friend on the list View
     * @throws JSONException : Error in parsing the JSON
     */
    private void unfriendRequest(int friendId, int position) throws JSONException {

        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/friends/remove/"
                + this.userId + "/" + friendId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                server_url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {

                        friends.remove(position);
                        notifyItemRemoved(position);

                        Toast.makeText(context, "Unfriended", Toast.LENGTH_SHORT).show();
                    }
                },
                volleyError -> {
                    Toast.makeText(context, volleyError.toString(), Toast.LENGTH_SHORT).show();
                }

        );

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }


    private void getFriendChats(int userId, Friend friend){
        String server_url = "http://coms-3090-016.class.las.iastate.edu:8080/api/chat/friendChats/" + userId;

        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                server_url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i =0; i < response.length(); i++){
                            try {
                                JSONObject friend_object = (JSONObject) response.get(i);
                                if (friend_object.getInt("friendUserId") == friend.getId()){
                                    int chatroomId = friend_object.getInt("chatId");
                                    startChat(chatroomId, friend);
                                    break;
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ERROR", error.toString());
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }
}
