package com.example.synergy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.synergy.R;
import com.example.synergy.fragments.allEventsFragment;
import com.example.synergy.fragments.eventMapFragment;
import com.example.synergy.fragments.interestedEventsListFragment;
import com.example.synergy.fragments.myEventsFragment;
import com.example.synergy.fragments.myEventsReviewsFragment;
import com.example.synergy.fragments.reviewEventsAttendedFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

public class EventReviewActivity extends AppCompatActivity {
    private String userDetailString;
    private Button backbutton;

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event_review);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.event_review_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Bundle extras = getIntent().getExtras();
        assert extras != null;
        userDetailString = extras.getString("userDetails");

        backbutton = findViewById(R.id.backbuttonToHome);
        viewPager = findViewById(R.id.eventReviewViewPager);
        tabLayout = findViewById(R.id.eventReviewTabLayout);

        viewPager.setAdapter(new EventsReviewPagerAdapter(this, userDetailString));
        viewPager.setOffscreenPageLimit(4);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->{
            if (position == 0) tab.setText("My Events");
            else if (position == 1) tab.setText("Events I Attended");
        }).attach();


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public static class EventsReviewPagerAdapter extends FragmentStateAdapter {
        private final String userDetailString;

        public EventsReviewPagerAdapter(@NonNull AppCompatActivity activity, String userDetailString){
            super(activity);
            this.userDetailString = userDetailString;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position){
            Bundle args = new Bundle();


            args.putString("userDetails", userDetailString);

            if (position==0){
//                My Events Reviews
                myEventsReviewsFragment fragment = new myEventsReviewsFragment();
                fragment.setArguments(args);
                return fragment;
            } else{
                myEventsFragment myEvents = new myEventsFragment();
//                Other People's events that I have attended
                reviewEventsAttendedFragment fragment = new reviewEventsAttendedFragment();
                fragment.setArguments(args);
                return fragment;

            }

        }

        @Override
        public int getItemCount(){
            return 2;
        }

    }
}