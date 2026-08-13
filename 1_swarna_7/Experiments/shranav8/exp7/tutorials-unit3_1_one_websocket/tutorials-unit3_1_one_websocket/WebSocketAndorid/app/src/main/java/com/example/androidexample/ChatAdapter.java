package com.example.androidexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private final List<Message> messages;

    public ChatAdapter(List<Message> messages){
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isSentByUser() ? 1 : 0;
    }



    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = viewType == 1 ? R.layout.message_item: R.layout.other_message_item;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout,parent,false);

        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.msgText.setText(messages.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView msgText;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            msgText = itemView.findViewById(R.id.msgText);
        }
    }
}
