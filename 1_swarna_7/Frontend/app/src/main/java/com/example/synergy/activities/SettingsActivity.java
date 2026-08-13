package com.example.synergy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.synergy.R;

public class SettingsActivity extends AppCompatActivity {
    private String response;

    private Button backButton;
    private Button editButton;

    private Button deleteButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);


        Bundle extras = getIntent().getExtras();

        if (extras == null){
//            Login credentials didnt pass through, this is not supposed to happen
//            critical error
        } else {
//            Testing for passed extras
            response = extras.getString("response");
        }

        editButton = findViewById(R.id.editButton);
        deleteButton = findViewById(R.id.deleteButton);
        backButton = findViewById(R.id.backbuttonToHome);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                On click of back button the activity is finished
                finish();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Go into edit activity to edit accounts
                Intent intent = new Intent(SettingsActivity.this, EditActivity.class);
                intent.putExtra("response", response);
                assert extras != null;
                intent.putExtra("password", extras.getString("password"));
                startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, DeleteActivity.class);
            Log.d("Response: " , response);
            intent.putExtra("response", response);
            startActivity(intent);
        });



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}