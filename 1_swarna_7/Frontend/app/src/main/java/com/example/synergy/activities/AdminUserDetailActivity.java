package com.example.synergy.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;

import org.json.JSONObject;

public class AdminUserDetailActivity extends AppCompatActivity {

    private TextView nameText, emailText, tierText;
    private Button tier1Btn, tier2Btn, revokeBtn, deleteBtn;

    private int userId;
    private int tier = 0; // always recomputed

    private static final String BASE = "http://coms-3090-016.class.las.iastate.edu:8080";

    public static void start(Context ctx, int userId) {
        Intent i = new Intent(ctx, AdminUserDetailActivity.class);
        i.putExtra("userId", userId);
        ctx.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);

        userId = getIntent().getIntExtra("userId", -1);

        initViews();

        findViewById(R.id.back_button).setOnClickListener(v -> finish());

        tier1Btn.setOnClickListener(v -> postTier(1));
        tier2Btn.setOnClickListener(v -> handleTier2());
        revokeBtn.setOnClickListener(v -> revokeTier2());
        deleteBtn.setOnClickListener(v -> deleteAdmin());

        loadUserAccountInfo();   // fetch username/email
        loadPermissions();       // fetch admin + tier2 status
    }

    private void initViews() {
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText);
        tierText = findViewById(R.id.tierText);

        tier1Btn = findViewById(R.id.grantTier1Btn);
        tier2Btn = findViewById(R.id.grantTier2Btn);
        revokeBtn = findViewById(R.id.revokeTier2Btn);
        deleteBtn = findViewById(R.id.deleteAdminBtn);
    }

    private void loadUserAccountInfo() {
        String url = BASE + "/api/accounts/" + userId;

        StringRequest req = new StringRequest(Request.Method.GET, url,
                resp -> {
                    try {
                        JSONObject o = new JSONObject(resp);

                        String name  = o.optString("userName", "(no name)");
                        String email = o.optString("emailId", "(no email)");

                        nameText.setText(name);
                        emailText.setText(email);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                err -> toast("Failed to load user info")
        );

        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

    private void loadPermissions() {
        String url1 = BASE + "/api/is/admin/" + userId;
        String url2 = BASE + "/api/is/adminT2/" + userId;

        StringRequest req1 = new StringRequest(Request.Method.GET, url1,
                resp -> {
                    boolean isAdmin = Boolean.parseBoolean(resp.trim());
                    if (!isAdmin) {
                        tier = 0;
                        updateUI();
                    } else {
                        tier = 1;
                        loadTier2Check(url2);
                    }
                },
                err -> {
                    toast("Failed to load admin status");
                    updateUI();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(req1);
    }

    private void loadTier2Check(String url) {
        StringRequest req2 = new StringRequest(Request.Method.GET, url,
                resp -> {
                    boolean isT2 = Boolean.parseBoolean(resp.trim());
                    if (isT2) tier = 2;
                    updateUI();
                },
                err -> {
                    toast("Failed tier2 check");
                    updateUI();
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(req2);
    }

    private void updateUI() {
        tierText.setText("Tier: " + tier);

        tier1Btn.setEnabled(tier == 0);
        tier2Btn.setEnabled(tier < 2);
        revokeBtn.setEnabled(tier == 2);
        deleteBtn.setEnabled(tier > 0);
    }

    private void postTier(int newTier) {
        String url = BASE + "/api/grant/admin/" + userId + "/tier/" + newTier;

        StringRequest req = new StringRequest(Request.Method.POST, url,
                resp -> {
                    toast("Granted Tier " + newTier);
                    loadPermissions(); // refresh state from backend
                },
                err -> logError("Grant Tier " + newTier, err)
        );

        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

    private void handleTier2() {
        if (tier == 1) {
            putTier("/api/admin/grantTier2/" + userId);
        } else if (tier == 0) {
            postTier(2);
        } else {
            toast("Already Tier 2");
        }
    }

    private void revokeTier2() {
        if (tier != 2) {
            toast("Not Tier 2");
            return;
        }
        putTier("/api/admin/revokeTier2/" + userId);
    }

    private void putTier(String path) {
        StringRequest req = new StringRequest(Request.Method.PUT, BASE + path,
                resp -> {
                    toast("Updated");
                    loadPermissions(); // reload correct state
                },
                err -> logError("PUT", err)
        );

        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

    private void deleteAdmin() {
        String url = BASE + "/api/delete/admin/" + userId;

        StringRequest req = new StringRequest(Request.Method.DELETE, url,
                resp -> {
                    toast("Admin removed");
                    loadPermissions();
                },
                err -> logError("DELETE", err)
        );

        VolleySingleton.getInstance(this).addToRequestQueue(req);
    }

    private void logError(String action, com.android.volley.VolleyError err) {
        try {
            String body = err.networkResponse != null ?
                    new String(err.networkResponse.data) : "no response";
            Log.e("Admin", action + ": " + body);
        } catch (Exception e) {
            Log.e("Admin", action + ": " + err.toString());
        }
        toast("Server error");
    }

    private void toast(String t) {
        Toast.makeText(this, t, Toast.LENGTH_SHORT).show();
    }
}

