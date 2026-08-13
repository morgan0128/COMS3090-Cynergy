package com.example.androidexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        /**
         * Setting up variables for Buttons as well as the extras we got from the previous
         * activity
         */
        Button logout_btn = findViewById(R.id.button2);
        Intent intent = getIntent();
        String receivedText = intent.getStringExtra("email");
        TextView welcome = findViewById(R.id.textView4);
        Button continue_btn = findViewById(R.id.continue_btn);
        String WelcomeMsg = "Welcome " + receivedText + "!";
        /**
         * Display welcome message based on extra
         */
        welcome.setText(WelcomeMsg);




        /**
         * Event Listener for the Log Out Button
         */
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
            }
        });


        /**
         * Event listener for the Continue button
         */
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(MainActivity2.this, MainActivity3.class);
                intent.putExtra("name", receivedText);
                startActivity(intent1);
            }
        });
    }
}