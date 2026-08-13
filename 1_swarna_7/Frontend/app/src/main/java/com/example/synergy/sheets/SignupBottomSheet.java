package com.example.synergy.sheets;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.activities.HomeActivity;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SignupBottomSheet extends BottomSheetDialogFragment {

    private EditText ETEmail;
    private EditText ETPassword;

    private static final String POSTMAN_URL =
            "http://coms-3090-016.class.las.iastate.edu:8080/api/signup";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.bottom_sheet_signup, container, false);

        Button btnGoToSignup = root.findViewById(R.id.go_to_login);
        Button signUpButton = root.findViewById(R.id.signUpButton);
        ETEmail = root.findViewById(R.id.etEmail);
        ETPassword = root.findViewById(R.id.etPassword);

        btnGoToSignup.setOnClickListener(v -> {
            dismiss();
            new LoginBottomSheet().show(getParentFragmentManager(), "LoginSheet");
        });

        signUpButton.setOnClickListener(v -> {
            String email = ETEmail.getText().toString().trim();
            String password = ETPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showStatusDialog("Missing Info", "Please enter both email and password.");
            } else {
                // 🔹 Only call the server. Do NOT start HomeActivity here.
                makeServerReq(email, password);
            }
        });

        return root;
    }

    private void makeServerReq(String email, String password) {
        try {
            JSONObject signupData = new JSONObject();
            signupData.put("emailId", email);
            signupData.put("userPassword", password);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    POSTMAN_URL,
                    signupData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Volley Response", response.toString());

                            // Optional: show dialog OR just go to Home
                            showStatusDialog("Success", "Signup successful!");

                            ETEmail.setText("");
                            ETPassword.setText("");

                            // 🔹 IMPORTANT: pass the REAL server response to HomeActivity
                            Intent intent = new Intent(getActivity(), HomeActivity.class);
                            intent.putExtra("response", response.toString());
                            startActivity(intent);

                            // Close the bottom sheet so user doesn't go back here
                            dismiss();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("Volley Error", error.toString());
                            showStatusDialog("Error", "Signup failed. Please try again.");
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            VolleySingleton.getInstance(requireContext()).addToRequestQueue(jsonRequest);

        } catch (JSONException e) {
            e.printStackTrace();
            showStatusDialog("Error", "Failed to create signup request.");
        }
    }

    private void showStatusDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
