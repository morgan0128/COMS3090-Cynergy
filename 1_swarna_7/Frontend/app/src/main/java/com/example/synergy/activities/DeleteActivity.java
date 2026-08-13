package com.example.synergy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;

import org.json.JSONObject;



public class DeleteActivity extends AppCompatActivity {

    private TextView deleteHeader;
    private TextView deleteEmail;
    private TextView deletePassword;
    private Button deleteButton;
    private String email;
    private String password;
    private String response;

    private static final String DELETE_URL = "http://coms-3090-016.class.las.iastate.edu:8080/api/delete?emailId=";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete);

        deleteHeader = findViewById(R.id.deleteHeader);
        deleteEmail = findViewById(R.id.deleteEmail);
        deletePassword = findViewById(R.id.deletePassword);
        deleteButton = findViewById(R.id.deleteButton);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            response = extras.getString("response");
            try {
                if (response != null) {
                    response = response.replace("'", "\"");
                    JSONObject deleteJSON = new JSONObject(response);
                    email = deleteJSON.getString("emailId");
                    password = deleteJSON.getString("userPassword");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        deleteEmail.setText(email);
        deletePassword.setText(password);

        deleteButton.setOnClickListener(v -> makeServerReq(email));
    }

    private void makeServerReq(String email) {
        try {
            String delete_url = DELETE_URL + email;
            Log.d("DELETE_URL", delete_url);

            StringRequest deleteRequest = new StringRequest(
                    Request.Method.DELETE,
                    delete_url,
                    response -> {
                        showStatusDialog("Success", "Account deleted successfully!");
                        new android.os.Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(DeleteActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }, 2000);

                    },
                    error -> showStatusDialog("Error", "Failed to delete account.")
            );

            VolleySingleton.getInstance(this).addToRequestQueue(deleteRequest);

        } catch (Exception e) {
            e.printStackTrace();
            showStatusDialog("Error", "Invalid email format.");
        }
    }

    private void showStatusDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
