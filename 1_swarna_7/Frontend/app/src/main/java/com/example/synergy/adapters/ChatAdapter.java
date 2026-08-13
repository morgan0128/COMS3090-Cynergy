package com.example.synergy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synergy.items.ChatMessage;
import com.example.synergy.R;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.VH> {

    private final List<ChatMessage> data = new ArrayList<>();

    public ChatAdapter() {}

    public void add(ChatMessage m) {
        data.add(m);
        notifyItemInserted(data.size() - 1);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_incoming, parent, false);  // always incoming layout
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ChatMessage m = data.get(position);

        holder.name.setVisibility(View.VISIBLE);
        holder.name.setText(m.senderId);

        holder.msg.setText(m.text);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, msg;
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.nameText);
            msg  = v.findViewById(R.id.msgText);
        }
    }
}

