package com.example.synergy.activities;

import android.os.Bundle;
import android.widget.Button;
import android.os.Handler;

import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.synergy.R;
import com.example.synergy.sheets.LoginBottomSheet;
import com.example.synergy.sheets.SignupBottomSheet;

public class MainActivity extends AppCompatActivity {

    private boolean isAppReady = false; // flag that controls when to end splash screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // attaching splash theme to this activity, and controlling it
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        //decides when splashscreen should end displaying based on boolean
        splashScreen.setKeepOnScreenCondition(() ->
            !isAppReady
        );

        //listens when splashScreen is about to exit
        splashScreen.setOnExitAnimationListener(splashView -> {
            //gets the splash View object
            View view = splashView.getView();

            view.animate()
                    .translationY(-view.getHeight()) //slides the splash screen completely off the screen, slides upwards
                    .alpha(0f) //fades out
                    .setDuration(700L) //duration of 0.5s
                    .withEndAction(splashView::remove) //removes splash completely
                    .start(); //starts animation
        });




        //2 seconds timer for splash screen, placeholder for now
        new Handler().postDelayed(() -> {
            isAppReady = true;
        }, 1400);

//        Default Template
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


//        Assigning Login and Signup buttons to variables
        Button btnShowLogin = findViewById(R.id.button_login);
        Button btnShowSignup = findViewById(R.id.button_signup);

//        Setting onClick Listeners to buttons
        btnShowLogin.setOnClickListener(v ->{
//            The login sheet is already created during app init, on click displays the sheet
            new LoginBottomSheet().show(getSupportFragmentManager(), "LoginSheet");
        });

//        Same theory as the login button
        btnShowSignup.setOnClickListener(v -> {
            new SignupBottomSheet().show(getSupportFragmentManager(), "SignupSheet");
        });





    }
}