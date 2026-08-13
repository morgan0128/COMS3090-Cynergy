package com.example.synergy.sheets;

import android.app.AlertDialog;
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
import com.example.synergy.TestFlags;
import com.example.synergy.activities.HomeActivity;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;
import com.example.synergy.WebSocketService;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginBottomSheet extends BottomSheetDialogFragment {

    private Button LogInButton;

    private EditText email;
    private EditText password;


//    Creates the login sheet when app is loaded but doesn't display on screen yet
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.bottom_sheet_login, container, false);

//        Button code to switch to the Signup sheet
        Button btnGoToSignup = root.findViewById(R.id.go_to_signup);
        btnGoToSignup.setOnClickListener(v -> {
            dismiss(); // close login drawer
            new SignupBottomSheet().show(getParentFragmentManager(), "SignupSheet");
        });

        LogInButton = root.findViewById(R.id.btnLogin);
        email = (root.findViewById(R.id.etEmail));
        password = (root.findViewById(R.id.etPassword));


//        When clicked we should log in
        LogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//            We should either get back data from request and log in
//                or we should error out

                String emailString = email.getText().toString();
                String passwordString = password.getText().toString();
                LogInButton.setEnabled(false);
                try {
                    if (TestFlags.TEST_MODE) {
                        // Skip Volley and force navigation
                        Intent i = new Intent(requireContext(), HomeActivity.class);
                        startActivity(i);
                        return;
                    }

                    makeLoginReq(emailString, passwordString);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    LogInButton.setEnabled(true);
                }


            }
        });

        return root;
    }


    //    Function that makes the string response to the server
    private void makeLoginReq(String emailString, String passwordString)  {

        String URL_STRING_REQ = "http://coms-3090-016.class.las.iastate.edu:8080/api/login";
        JSONObject loginObject = new JSONObject();
        try{
            loginObject.put("emailId", emailString);
            loginObject.put("userPassword", passwordString);
        } catch (JSONException e){
            showStatusDialog("Error", "Invalid Login Data");
        }



        JsonObjectRequest loginRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL_STRING_REQ,
                loginObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject responseObject) {
                        if (!isAdded()) return;
                        handleLoginSuccess(responseObject, passwordString);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (!isAdded()) return;

                        handleLoginError(volleyError);
                    }
                }
        );


        // Adding request to the Volley request queue
//        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
//        For bottomSheetDialogFragment we need to do this:
        VolleySingleton.getInstance(requireContext()).addToRequestQueue(loginRequest);

    }

    private void handleLoginSuccess(JSONObject responseObject, String password){

        try {

            int userId = responseObject.getInt("id");
            Intent serviceIntent = new Intent(requireContext(), WebSocketService.class);
            serviceIntent.setAction("CONNECT");
            serviceIntent.putExtra("url", "ws://coms-3090-016.class.las.iastate.edu:8080/ws/notifications/" + userId);
            serviceIntent.putExtra("key", "notifications");
            requireContext().startService(serviceIntent);

//           We successfully log  in
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.putExtra("response", responseObject.toString());
            intent.putExtra("password", password);
            startActivity(intent);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleLoginError(VolleyError err){
        Log.e("Volley Error", err.toString());

//      If any error occurs during login attempt show this message

        if (email.getText().toString().isEmpty() & password.getText().toString().isEmpty()){
            showStatusDialog("Log In Error","Empty Field, Please enter email and password");
        }else if (password.getText().toString().isEmpty()){
            showStatusDialog("Log In Error","Empty Field, Please enter your Password");
            return;
        }else if (email.getText().toString().isEmpty()){
        showStatusDialog("Log In Error","Empty Field, Please enter your Email");
            return;
        }
        else {
            showStatusDialog("Log In Error",err.toString());
        }
        if (email != null) email.setText("");
        if (password!= null) password.setText("");
    }


    //    Function that helps to show a status dialog
    private void showStatusDialog(String title, String message) {
        requireActivity().runOnUiThread(() -> {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        });
    }
}
