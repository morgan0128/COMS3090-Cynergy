package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class BMIActivity extends AppCompatActivity {

   private TextView greeting; //
    private EditText height;
    private EditText weight;

    private Button calculate_bmi;

    private TextView result;

    private Button returnHome;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);            // link to Login activity XML

        greeting = findViewById(R.id.greeting_txt);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        calculate_bmi = findViewById(R.id.bmi_calculate);
        result = findViewById(R.id.result_txt);
        returnHome = findViewById(R.id.returnHome);


        Bundle extras = getIntent().getExtras();
        if(extras == null){
            greeting.setText("FitnessPal");
        }
         else {
             String text = "Hello " + extras.getString("USERNAME");
            greeting.setText(text); // this will come from LoginActivity
        }

         calculate_bmi.setOnClickListener(v -> {
             String user_height = height.getText().toString();
             String user_weight = weight.getText().toString();

             if (user_weight.isEmpty() || user_height.isEmpty()) {
                 result.setText("Please enter both height and weight");
                 return;
             }

             int heightCm = Integer.parseInt(user_height);
             int weightKg = Integer.parseInt(user_weight);

             double heightM = heightCm / 100.0;

             double calculated_bmi = weightKg / (heightM * heightM);

             String category;

             if (calculated_bmi < 18.5) {
                 category = "Underweight";
             }
             else if (calculated_bmi < 25) {
                 category = "Normal weight";
             }
             else if (calculated_bmi < 30) {
                 category = "Overweight";
             }
             else if (calculated_bmi < 40 ){
                 category = "Obese";
             }
             else {
                 category = "Morbidly Obese";
             }

             String result_text = "BMI: " + String.format("%.2f", calculated_bmi) + ", " + category;
             result.setText(result_text);

         });
        returnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BMIActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}