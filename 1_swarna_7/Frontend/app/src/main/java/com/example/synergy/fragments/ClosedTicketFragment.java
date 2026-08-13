// package: com.example.synergy.fragments
package com.example.synergy.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.AdminTicketAdapter;
import com.example.synergy.items.AdminTicket;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ClosedTicketFragment extends Fragment {

    private static final String ARG_ADMIN_ID = "adminId";
    private static final String BASE_URL = "http://coms-3090-016.class.las.iastate.edu:8080";

    private int adminId = -1;

    private RecyclerView ticketRecycler;
    private ProgressBar loading;
    private TextView emptyView;

    private final ArrayList<AdminTicket> tickets = new ArrayList<>();
    private AdminTicketAdapter adapter;

    public ClosedTicketFragment() {
        super(R.layout.fragment_admin_ticket_list);
    }

    public static ClosedTicketFragment newInstance(int adminId) {
        ClosedTicketFragment f = new ClosedTicketFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_ADMIN_ID, adminId);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ticketRecycler = view.findViewById(R.id.ticketRecycler);
        loading        = view.findViewById(R.id.loading);
        emptyView      = view.findViewById(R.id.emptyView);

        if (getArguments() != null) {
            adminId = getArguments().getInt(ARG_ADMIN_ID, -1);
        }

        ticketRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        // No actions needed; closed tickets are read-only here
        adapter = new AdminTicketAdapter(tickets, null);
        ticketRecycler.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadClosedTickets();
    }

    private void loadClosedTickets() {
        if (getContext() == null) return;

        loading.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        ticketRecycler.setVisibility(View.GONE);

        String url = BASE_URL + "/api/adminIssue/closed/all";

        JsonArrayRequest req = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                this::handleResponse,
                error -> {
                    loading.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    emptyView.setText("Failed to load tickets.");
                    Toast.makeText(getContext(), "Error loading tickets", Toast.LENGTH_SHORT).show();
                }
        );

        VolleySingleton.getInstance(requireContext()).addToRequestQueue(req);
    }

    private void handleResponse(JSONArray arr) {
        tickets.clear();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            if (o == null) continue;

            int issueId           = o.optInt("issue_id", -1);
            String type           = o.optString("type", "UNKNOWN");
            String status         = o.optString("status", "UNKNOWN");
            boolean resolved      = o.optBoolean("resolved", true);
            String description    = o.optString("description", "");
            int proposedUserId    = o.optInt("proposedUserId", -1);
            String proposedUser   = o.optString("proposedUsername", "Unknown");

            AdminTicket ticket = new AdminTicket(
                    issueId,
                    type,
                    status,
                    resolved,
                    description,
                    proposedUserId,
                    proposedUser
            );
            tickets.add(ticket);
        }

        adapter.notifyDataSetChanged();
        loading.setVisibility(View.GONE);

        if (tickets.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("No closed tickets.");
            ticketRecycler.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            ticketRecycler.setVisibility(View.VISIBLE);
        }
    }
}



