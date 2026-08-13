// package: com.example.synergy.adapters
package com.example.synergy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synergy.R;
import com.example.synergy.items.AdminTicket;

import java.util.List;

public class AdminTicketAdapter extends RecyclerView.Adapter<AdminTicketAdapter.VH> {

    // Listener for approve/deny actions
    public interface TicketActionListener {
        void onApprove(AdminTicket ticket);
        void onDeny(AdminTicket ticket);
    }

    private final List<AdminTicket> tickets;
    private final TicketActionListener listener;

    public AdminTicketAdapter(List<AdminTicket> tickets, TicketActionListener listener) {
        this.tickets = tickets;
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle, status, description;
        Button approve, deny;

        VH(@NonNull View v) {
            super(v);
            title       = v.findViewById(R.id.ticketTitle);
            subtitle    = v.findViewById(R.id.ticketSubtitle);
            status      = v.findViewById(R.id.ticketStatus);
            description = v.findViewById(R.id.ticketDescription);
            approve     = v.findViewById(R.id.approveBtn);
            deny        = v.findViewById(R.id.closeBtn); // "Deny"
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_ticket, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        AdminTicket t = tickets.get(position);

        // Title + info
        holder.title.setText(t.getType());
        holder.subtitle.setText("User: " + t.getProposedUsername()
                + " (id " + t.getProposedUserId() + ")");
        holder.status.setText("Status: " + t.getStatus());
        holder.description.setText(t.getDescription());

        boolean pending = t.isPending();
        holder.approve.setEnabled(pending);
        holder.deny.setEnabled(pending);

        holder.approve.setOnClickListener(v -> {
            if (listener != null && pending) {
                listener.onApprove(t);
            }
        });

        holder.deny.setOnClickListener(v -> {
            if (listener != null && pending) {
                listener.onDeny(t);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }
}



