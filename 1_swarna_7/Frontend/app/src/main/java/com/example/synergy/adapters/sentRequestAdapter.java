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

import com.example.synergy.R;
import com.example.synergy.items.User;

import java.util.List;

public class sentRequestAdapter extends RecyclerView.Adapter<sentRequestAdapter.sentRequestViewHolder> {


    private final Context context;
    private List<User> users;


    public sentRequestAdapter(Context context, List<User> users){
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public sentRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sent_request, parent, false);

        return new sentRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull sentRequestViewHolder holder, int position) {
        User friend = users.get(position);
        holder.userName.setText(friend.getName());

        holder.cancelFriend.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Friend Request Pending")
                    .setMessage(friend.getName() + " hasn't accepted your request yet.")
                    .setPositiveButton("Yes", (dialog, which) -> {
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

    /**
     *  Update the list based on the filtered list
     * @param filteredList : list that is returned after filtering
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<User> filteredList) {
        this.users = filteredList; // assuming your list variable is called userList
        notifyDataSetChanged();
    }

    public static class sentRequestViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView cancelFriend;

        public sentRequestViewHolder(@NonNull View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.friendName);
            cancelFriend = itemView.findViewById(R.id.cancel_button);

        }
    }
}
