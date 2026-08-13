package com.example.synergy.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.synergy.R;
import com.example.synergy.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    private String response;
    private String passwordString;
    private TextView testView;
    private EditText passwordEdit;
    private EditText usernameEdit;
    private EditText confirmEdit;
    private String putResponse;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit);

        //            Collecting edit view and buttons
        Button backButton = findViewById(R.id.backbuttonToSettings);
        testView = findViewById(R.id.test);
        usernameEdit = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.password);
        confirmEdit = findViewById(R.id.reenterPassword);
        Button saveButton = findViewById(R.id.saveButton);



        Bundle extras =  getIntent().getExtras();

        testView = findViewById(R.id.test);

        if (extras == null){
            //            Login credentials didn't pass through, this is not supposed to happen
            //            critical error
            testView.setText("Critical Error");
        } else {
            //            We pass in user's login data that we get when we initially log in
            //            this is used to populated the user data we are editing
            response = extras.getString("response");
            passwordString = extras.getString("password");
        }


        try {

            ArrayList<String> user = parseUser(response);

            populateFields(user);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            // On click listener when we hit save
            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    handleSave(user);
                }
            });
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private ArrayList<String> parseUser(String extra) throws JSONException {
        //        Putting edited values in request Json Body
        JSONObject object = new JSONObject(extra);
        int id = object.getInt("id");

        String username = object.getString("userName");
        String password = passwordString;


        ArrayList<String> user = new ArrayList<String>();
        user.add(username);
        user.add(password);
        user.add(String.valueOf(id));

        return user;
    }

    private void populateFields(ArrayList<String> user){
        // Setting default values based on our Login
        confirmEdit.setText(user.get(1));
        passwordEdit.setText(user.get(1));
        usernameEdit.setText(user.get(0));

    }

    private void handleSave(ArrayList<String> user){
        //        Need to create on click listener for save button
        //        Needs to handle password and confirm password logic
        if (usernameEdit.getText().toString().isEmpty()) {
            testView.setText("Username required");
            testView.setVisibility(View.VISIBLE);
            return;
        }

        if (passwordEdit.getText().toString().equals(confirmEdit.getText().toString())){
            makeRequest(Integer.parseInt(user.get(2)));
            testView.setVisibility(View.INVISIBLE);

        } else{
            //          If passwords don't match don't make the request
            //          and create an error tag
            testView.setText("Passwords don't match");
            testView.setVisibility(View.VISIBLE);
        }
    }
    private void makeRequest(int id){


        String server_url ="http://coms-3090-016.class.las.iastate.edu:8080/api/edit/" + id;

        //        Making JSON object to put in the server
        JSONObject jsonBody = new JSONObject();
        try{
            jsonBody.put("userName", usernameEdit.getText().toString());
            jsonBody.put("userPassword", passwordEdit.getText().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                server_url,
                jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        putResponse = "Successfully Updated Profile";
                        successEdit();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        // Error caused by network issues
                        showStatusDialog(volleyError.toString());
                    }
                }
        );

        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void successEdit(){
        // Custom ToastToast message
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_message, null);
        TextView text = layout.findViewById(R.id.toast_text);
        text.setText(putResponse);
        Toast toast = new Toast(EditActivity.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    private void showStatusDialog(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Error!")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}