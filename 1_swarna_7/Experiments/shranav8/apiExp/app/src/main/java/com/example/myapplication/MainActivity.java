package com.example.myapplication;

import static com.example.myapplication.api.ApiClientFactory.GetPostApi;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.api.SlimCallback;
import com.example.myapplication.model.Post;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Attach variable to API text in xml
        TextView apiText1 = findViewById(R.id.actvity_main_text1);


//        From GetPostApi gets the URL and getFirstPost, finds the exact Post
//        of the API. THen calls SlimCallback to actually do something
//        with the response of the API. which it will return with String result
//        Finally sets text of apiText1
        GetPostApi().getFirstPost().enqueue(
                new SlimCallback<Post>(response ->{
                    String result = "ID:" + response.getId()
                            + "\n Title:" + response.getTitle()
                            + "\n Body:" + response.getBigText();
                    apiText1.setText(result);
                }, "CustomTagForFirstAPI"));
    }
}



