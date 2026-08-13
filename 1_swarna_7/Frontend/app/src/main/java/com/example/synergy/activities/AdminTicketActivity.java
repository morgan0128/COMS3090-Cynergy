
package com.example.synergy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.synergy.R;
import com.example.synergy.adapters.AdminTicketPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminTicketActivity extends AppCompatActivity {

    public static final String EXTRA_ADMIN_ID = "adminId";

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton backButton;
    private ImageButton manageAdminsButton;
    private TextView titleText;

    private int adminId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_ticket);

        backButton         = findViewById(R.id.back_button);
        manageAdminsButton = findViewById(R.id.manageAdminsBtn);
        titleText          = findViewById(R.id.titleText);
        tabLayout          = findViewById(R.id.tabLayout);
        viewPager          = findViewById(R.id.viewPager);

        titleText.setText("Admin Tickets");

        backButton.setOnClickListener(v -> finish());

        manageAdminsButton.setOnClickListener(v -> {
            Intent i = new Intent(this, AdminDashboardActivity.class);
            startActivity(i);
        });

        adminId = getIntent().getIntExtra(EXTRA_ADMIN_ID, -1);

        AdminTicketPagerAdapter adapter = new AdminTicketPagerAdapter(this, adminId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, pos) -> {
            if (pos == 0) tab.setText("Open");
            else if (pos == 1) tab.setText("Closed");
            else tab.setText("All");
        }).attach();
    }
}


