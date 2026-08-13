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
import com.example.synergy.fragments.AllUsersFragment;
import com.example.synergy.fragments.FriendsListFragment;
import com.example.synergy.fragments.receivedRequestsFragment;
import com.example.synergy.fragments.sentRequestsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class FriendsActivity extends AppCompatActivity {

    /**
     *  Creation of the FriendsActivity View when activity starts. Assigns views
     *  from the layout file to variables and provides functionality.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_friends);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.friends_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            setUpUI(extras);
        }
    }

    private void setUpUI(Bundle extras){
        String userDetailString = extras.getString("userDetails");

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new FriendsActivity.FriendsPagerAdapter(this, userDetailString));
        viewPager.setOffscreenPageLimit(3);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->{
            if (position == 0) tab.setText("All Users");
            else if (position == 1) tab.setText("My Friends");
            else if (position == 2) tab.setText("Sent");
            else tab.setText("Received");
        }).attach();

        Button backButton = findViewById(R.id.backbuttonToHome);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    public static class FriendsPagerAdapter extends FragmentStateAdapter {
        private final String userDetailString;

        /**
         *  Initializes FriendsPagerAdapter that allows switching between different
         *  fragments like a pager
         * @param activity : The current activity which would be FriendsActivity
         * @param userDetailString : The user String that was passed during login
         */
        public FriendsPagerAdapter(@NonNull AppCompatActivity activity, String userDetailString){
            super(activity);
            this.userDetailString = userDetailString;
        }

        /**
         *  Creates fragments that the pager can switch to with position
         *  of each fragment being ordered on the pager
         * @param position : position of the fragment on the pager
         * @return Fragment : A fragment based on which tab was clicked on pager
         */
        @NonNull
        @Override
        public Fragment createFragment(int position){
            Bundle args = new Bundle();
            args.putString("userDetails", userDetailString);

            if (position==0){
                AllUsersFragment usersList = new AllUsersFragment();
                usersList.setArguments(args);
                return  usersList;
            } else if (position == 1){
                FriendsListFragment friendsList = new FriendsListFragment();
                friendsList.setArguments(args);
                return  friendsList;

            } else if (position == 2){
                sentRequestsFragment sentList = new sentRequestsFragment();
                sentList.setArguments(args);
                return  sentList;
            } else {
                receivedRequestsFragment receivedList = new receivedRequestsFragment();
                receivedList.setArguments(args);
                return  receivedList;
            }

        }

        /**
         *  Number of fragments on the pager
         * @return 4 : 4 Fragments for this activity.
         */
        @Override
        public int getItemCount(){
            return 4;
        }

    }
}