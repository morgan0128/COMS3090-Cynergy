package com.example.synergy.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.adapters.AdminUserAdapter;
import com.example.synergy.items.AdminUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity implements AdminUserAdapter.OnUserClick {

    private RecyclerView recycler;
    private ProgressBar loading;
    private TextView empty;

    private final ArrayList<AdminUser> users = new ArrayList<>();
    private AdminUserAdapter adapter;

    private static final String BASE = "http://coms-3090-016.class.las.iastate.edu:8080";

    private final int superAdminId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        recycler = findViewById(R.id.usersRecycler);
        loading = findViewById(R.id.loading);
        empty = findViewById(R.id.emptyView);

        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        adapter = new AdminUserAdapter(users, this);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchAllUsers();
    }

    private void fetchAllUsers() {
        loading.setVisibility(View.VISIBLE);

        String url = BASE + "/api/accounts";

        StringRequest req = new StringRequest(
                Request.Method.GET, url,
                this::parseBasicUsers,
                err -> {
                    loading.setVisibility(View.GONE);
                    empty.setText("Failed to load users");
                    empty.setVisibility(View.VISIBLE);
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

    public void parseBasicUsers(String resp) {
        users.clear();

        try {
            JSONArray arr = new JSONArray(resp);

            Log.d("response", resp);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                int id = o.getInt("id");
                if (id == superAdminId) continue;

                // use correct JSON keys
                String name = o.optString("userName", "(no name)");
                String email = o.optString("emailId", "");

                AdminUser u = new AdminUser(id, name, email, 0);
                users.add(u);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        adapter.notifyDataSetChanged();

        for (AdminUser u : users) {
            fetchRealPermissions(u);
        }

        loading.setVisibility(View.GONE);
        empty.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);
    }


    private void fetchRealPermissions(AdminUser u) {
        String url1 = BASE + "/api/is/admin/" + u.getId();

        StringRequest req1 = new StringRequest(Request.Method.GET, url1,
                resp -> {
                    boolean isAdmin = Boolean.parseBoolean(resp.trim());
                    if (!isAdmin) {
                        u.setTier(0);
                        adapter.notifyDataSetChanged();
                    } else {
                        u.setTier(1);
                        fetchTier2Status(u);
                    }
                },
                err -> Log.e("Admin", "isAdmin failed for " + u.getId())
        );

        VolleySingleton.getInstance(this).addToRequestQueue(req1);
    }

    private void fetchTier2Status(AdminUser u) {
        String url = BASE + "/api/is/adminT2/" + u.getId();

        StringRequest req2 = new StringRequest(Request.Method.GET, url,
                resp -> {
                    boolean t2 = Boolean.parseBoolean(resp.trim());
                    if (t2) u.setTier(2);
                    adapter.notifyDataSetChanged();
                },
                err -> Log.e("Admin", "tier2 check failed for id " + u.getId())
        );

        VolleySingleton.getInstance(this).addToRequestQueue(req2);
    }

    @Override
    public void onUserClicked(AdminUser user) {
        AdminUserDetailActivity.start(this, user.getId());
    }
}


