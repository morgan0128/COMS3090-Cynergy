package com.example.synergy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.synergy.items.AdminUser;
import com.example.synergy.R;

import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.VH> {

    public interface OnUserClick {
        void onUserClicked(AdminUser user);
    }

    private final List<AdminUser> list;
    private final OnUserClick listener;

    public AdminUserAdapter(List<AdminUser> list, OnUserClick listener) {
        this.list = list;
        this.listener = listener;
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView name, email, tier;
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.nameText);
            email = v.findViewById(R.id.emailText);
            tier = v.findViewById(R.id.tierText);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int vt) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_user, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(VH h, int pos) {
        AdminUser u = list.get(pos);

        h.name.setText(u.getName());
        h.email.setText(u.getEmail());
        h.tier.setText("Tier " + u.getTier());
        h.itemView.setOnClickListener(v -> listener.onUserClicked(u));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}


