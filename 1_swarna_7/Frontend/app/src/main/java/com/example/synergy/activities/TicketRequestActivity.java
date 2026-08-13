package com.example.synergy.activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.synergy.R;
import com.example.synergy.adapters.TicketPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TicketRequestActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TicketPagerAdapter pagerAdapter;
    private ImageButton backButton;
    private TextView titleText;

    // Pass the logged-in userId into this Activity via Intent
    private int currentUserId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_request);

        backButton = findViewById(R.id.back_button);
        titleText  = findViewById(R.id.titleText);
        tabLayout  = findViewById(R.id.ticketTabLayout);
        viewPager  = findViewById(R.id.ticketViewPager);

        titleText.setText("File a Request");
        backButton.setOnClickListener(v -> finish());

        currentUserId = getIntent().getIntExtra("userId", -1);

        pagerAdapter = new TicketPagerAdapter(this, currentUserId);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Event Approval");
            else tab.setText("Delete User");
        }).attach();
    }
}

