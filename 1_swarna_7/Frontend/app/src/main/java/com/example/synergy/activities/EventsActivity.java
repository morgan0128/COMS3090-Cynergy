package com.example.synergy.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.synergy.R;
import com.example.synergy.fragments.allEventsFragment;
import com.example.synergy.fragments.eventMapFragment;
import com.example.synergy.fragments.interestedEventsListFragment;
import com.example.synergy.fragments.myEventsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

public class EventsActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private String userDetailString;
    private Button backbutton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        Bundle userDetails = getIntent().getExtras();
        if (userDetails != null){
            userDetailString = userDetails.getString("userDetails");
        }

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        backbutton = findViewById(R.id.backbuttonToHome);

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Go into edit activity to edit accounts
                finish();
            }

        });
        viewPager.setAdapter(new EventsPagerAdapter(this, userDetailString));
        viewPager.setOffscreenPageLimit(4);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->{
            if (position == 0) tab.setText("All Events");
            else if (position == 1) tab.setText("My Events");
            else if (position ==2) tab.setText("Event Map");
            else tab.setText("Interested Events");
        }).attach();
    }


    public static class EventsPagerAdapter extends FragmentStateAdapter{
        private final String userDetailString;

        public EventsPagerAdapter(@NonNull AppCompatActivity activity, String userDetailString){
            super(activity);
            this.userDetailString = userDetailString;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position){
            Bundle args = new Bundle();
            String userDetailStringReduced = "";
            try {
                JSONObject userDetail = new JSONObject(userDetailString);
                JSONObject reduced = new JSONObject();
                reduced.put("id", userDetail.getInt("id"));
                reduced.put("emailId", userDetail.getString("emailId"));
                userDetailStringReduced = reduced.toString();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            args.putString("userDetails", userDetailStringReduced);

            if (position==0){
                allEventsFragment allEvents = new allEventsFragment();
                allEvents.setArguments(args);
                return  allEvents;
            } else if (position == 1){
                myEventsFragment myEvents = new myEventsFragment();
                myEvents.setArguments(args);
                return myEvents;

            } else if (position == 2){
                eventMapFragment eventMap = new eventMapFragment();
                eventMap.setArguments(args);
                return eventMap;
            } else {
                interestedEventsListFragment interestedEvents = new interestedEventsListFragment();
                interestedEvents.setArguments(args);
                return interestedEvents;
            }

        }

        @Override
        public int getItemCount(){
            return 4;
        }

    }

}
