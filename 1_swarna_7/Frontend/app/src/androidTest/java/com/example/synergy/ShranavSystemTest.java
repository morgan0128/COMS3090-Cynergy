package com.example.synergy;

import static androidx.test.espresso.Espresso.onIdle;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.viewpager2.widget.ViewPager2;

import com.example.synergy.activities.EditActivity;
import com.example.synergy.activities.EventsActivity;
import com.example.synergy.activities.FriendsActivity;
import com.example.synergy.activities.HomeActivity;
import com.example.synergy.activities.MainActivity;
import com.example.synergy.adapters.EventNodeAdapter;
import com.example.synergy.adapters.UserAdapter;
import com.example.synergy.fragments.AllUsersFragment;
import com.example.synergy.fragments.eventMapFragment;
import com.example.synergy.fragments.myEventsFragment;
import com.example.synergy.fragments.myEventsReviewsFragment;
import com.example.synergy.fragments.reviewEventsAttendedFragment;
import com.example.synergy.fragments.sentRequestsFragment;
import com.example.synergy.items.EventItem;
import com.example.synergy.items.User;
import com.example.synergy.sheets.CreateEventSheet;
import com.example.synergy.sheets.InviteFriendToEventSheet;

import org.hamcrest.Matcher;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ShranavSystemTest {

    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<MainActivity> mainRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testLogin(){

        mainRule.getScenario().onActivity(activity -> {
        });
        String email = "a@gmail.com";
        String password = "a";
        onView(withId(R.id.button_login)).perform(click());

        onView(withId(R.id.login_sheet_root)).check(matches(isDisplayed()));
        onView(withId(R.id.etEmail)).perform(typeText(email), closeSoftKeyboard());
        onView(withId(R.id.etPassword)).perform(typeText(password), closeSoftKeyboard());

        onView(withId(R.id.btnLogin)).perform(click());

        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException ignored) {}

        onView(withId(R.id.home_root)).check(matches(isDisplayed()));

    }

    @Test
    public void testHomeAndNavigateToSettingsAndNotifications(){
        // Launch HomeActivity with mock response
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                HomeActivity.class
        );
        intent.putExtra("response", "{\"emailId\":\"a@gmail.com\",\"id\":4,\"userName\":\"a\"}");
        intent.putExtra("password", "a");

        ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(intent);

        // Set email directly if needed
        scenario.onActivity(activity -> activity.email = "a@gmail.com");

        // Test navigating to Settings
        onView(withId(R.id.settings)).perform(click());
        onView(withId(R.id.editButton)).perform(click());
        onView(withId(R.id.edit_root)).check(matches(isDisplayed()));
        onView(withId(R.id.backbuttonToSettings)).perform(click());

        // Back to Home
        onView(withId(R.id.backbuttonToHome)).perform(click());
        onView(withId(R.id.home_root)).check(matches(isDisplayed()));

        // Test navigating to Notifications
        onView(withId(R.id.notification)).perform(click());
        onView(withId(R.id.notification_root)).check(matches(isDisplayed()));
        onView(withId(R.id.clear_all)).perform(click());

        onView(withId(R.id.backbuttonToHome)).perform(click());

        // Test navigating to EventInviteActivity
        onView(withId(R.id.eventInviteButton)).perform(click());
        // You can assert that eventInviteActivity is displayed by checking a root view or RecyclerView
        onView(withId(R.id.event_invites))
                .check(matches(isDisplayed()));

    }

    @Test
    public void testFriendRequest(){
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                FriendsActivity.class
        );
        intent.putExtra("userDetails", "{\"emailId\":\"a@gmail.com\",\"id\":4,\"userName\":\"a\"}");

        ActivityScenario<FriendsActivity> scenario = ActivityScenario.launch(intent);
        onView(withId(R.id.friends_root)).check(matches(isDisplayed()));
        onView(withText("All Users")).check(matches(isDisplayed()));
        onView(withText("My Friends")).check(matches(isDisplayed()));

        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);

            // Cycle through all 4 fragments
            for (int i = 0; i < 4; i++) {
                viewPager.setCurrentItem(i, true); // smooth scroll = true
            }
        });

        // Switch to "All Users"
        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
            viewPager.setCurrentItem(0, false);
        });
        onView(withText("All Users")).check(matches(isDisplayed()));

// Switch to "My Friends"
        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
            viewPager.setCurrentItem(1, false);
        });
        onView(withText("My Friends")).check(matches(isDisplayed()));

// Switch to "Sent Requests"
        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
            viewPager.setCurrentItem(2, false);
        });
        onView(withText("Sent")).check(matches(isDisplayed()));

// Switch to "Received Requests"
        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
            viewPager.setCurrentItem(3, false);
        });
        onView(withText("Received")).check(matches(isDisplayed()));


        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
            viewPager.setCurrentItem(1, false); // "My Friends"
        });

        onView(withText("My Friends")).check(matches(isDisplayed()));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {}

        onView(allOf(withId(R.id.friends_rv), isDescendantOfA(withId(R.id.friends_list_root))))
                .perform(RecyclerViewActions.actionOnItem(
                        hasDescendant(withText("bbb")),
                        click()
                ));





    }

    @Test
    public void testEventsNavigation(){
        // Launch EventsActivity with test user
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                EventsActivity.class
        );
        intent.putExtra("userDetails", "{\"emailId\":\"a@gmail.com\",\"id\":4,\"userName\":\"a\"}");

        ActivityScenario<EventsActivity> scenario = ActivityScenario.launch(intent);

        // Check main layout is displayed
        onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
        onView(withId(R.id.viewPager)).check(matches(isDisplayed()));

        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);

            // Cycle through all 4 fragments
            for (int i = 0; i < 4; i++) {
                viewPager.setCurrentItem(i, true);
            }
        });

        // Switch to each tab and check title
        scenario.onActivity(activity -> activity.findViewById(R.id.viewPager));

        onView(withText("All Events")).check(matches(isDisplayed()));
        onView(withText("My Events")).check(matches(isDisplayed()));
        onView(withText("Event Map")).check(matches(isDisplayed()));
        onView(withText("Interested Events")).check(matches(isDisplayed()));

        // Example: click an event in "All Events" RecyclerView
        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
            viewPager.setCurrentItem(0, false); // All Events
        });


        // Switch to My Events tab
        scenario.onActivity(activity -> {
            ViewPager2 viewPager = activity.findViewById(R.id.viewPager);
            viewPager.setCurrentItem(1, false);
        });


    }


    @Test
    public void testFragmentLoadsAndRvIsDisplayed() {

        // Launch hosting activity (no @Rule)
        ActivityScenario<MainActivity> scenario =
                ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            String fakeUser = "{\"id\": 7}";
            myEventsReviewsFragment fragment =
                    myEventsReviewsFragment.newInstance(fakeUser);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commitNow();
        });

        // Check RecyclerView exists
        onView(withId(R.id.myEventRv))
                .check(matches(isDisplayed()));

        // Scroll to ensure adapter attaches if items appear later
        onView(withId(R.id.myEventRv))
                .perform(RecyclerViewActions.scrollToPosition(0));
    }


    @Test
    public void testEventMapFragmentLoads() {

        // Launch activity manually
        ActivityScenario<MainActivity> scenario =
                ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            String fakeUser = "{\"id\": 3}";

            eventMapFragment fragment = new eventMapFragment();
            Bundle b = new Bundle();
            b.putString("userDetails", fakeUser);
            fragment.setArguments(b);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commitNow();
        });

        // Validate that the map view exists in the UI
        onView(withId(R.id.map))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testSentRequestsFragmentLoads() {

        // Launch hosting activity manually (no @Rule)
        ActivityScenario<MainActivity> scenario =
                ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            String fakeUser = "{\"id\": 5}";

            sentRequestsFragment fragment =
                    sentRequestsFragment.newInstance(fakeUser);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commitNow();
        });

        // Check recycler view exists
        onView(withId(R.id.recyclerView))
                .check(matches(isDisplayed()));

        // Check search view exists
        onView(withId(R.id.searchUsers))
                .check(matches(isDisplayed()));

        // Type into the search bar
        onView(withId(R.id.searchUsers))
                .perform(typeText("john"));
    }

    public void testInviteFriendToEventSheetLoads() {

        // Launch activity manually (no Rule)
        ActivityScenario<MainActivity> scenario =
                ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            String fakeUser = "{\"id\": 7}";
            int fakeFriendId = 44;

            InviteFriendToEventSheet sheet =
                    InviteFriendToEventSheet.newInstance(fakeUser, fakeFriendId);

            sheet.show(activity.getSupportFragmentManager(), "inviteSheet");
        });

        // Check the RecyclerView appears
        onView(withId(R.id.eventInviteRv))
                .check(matches(isDisplayed()));
    }


    @Test
    public void testFieldsPopulatedAndSaveButton() throws Exception {
        // Prepare JSON extras
        JSONObject userDetails = new JSONObject();
        userDetails.put("id", 1);
        userDetails.put("userName", "TestUser");
        String password = "password123";

        // Create fully qualified intent
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                com.example.synergy.activities.EditActivity.class
        );
        intent.putExtra("response", userDetails.toString());
        intent.putExtra("password", password);

        // Launch activity
        ActivityScenario<com.example.synergy.activities.EditActivity> scenario =
                ActivityScenario.launch(intent);

        // Check that fields are populated correctly
        Espresso.onView(ViewMatchers.withId(R.id.username))
                .check(ViewAssertions.matches(ViewMatchers.withText("TestUser")));

        Espresso.onView(ViewMatchers.withId(R.id.password))
                .check(ViewAssertions.matches(ViewMatchers.withText(password)));

        Espresso.onView(ViewMatchers.withId(R.id.reenterPassword))
                .check(ViewAssertions.matches(ViewMatchers.withText(password)));

        // Case 1: Passwords mismatch -> should show error
        Espresso.onView(ViewMatchers.withId(R.id.password))
                .perform(ViewActions.clearText(), ViewActions.typeText("newpass"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.saveButton))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.test))
                .check(ViewAssertions.matches(ViewMatchers.withText("Passwords don't match")));

        // Case 2: Passwords match -> error view disappears
        Espresso.onView(ViewMatchers.withId(R.id.reenterPassword))
                .perform(ViewActions.clearText(), ViewActions.typeText("newpass"), ViewActions.closeSoftKeyboard());

        Espresso.onView(ViewMatchers.withId(R.id.saveButton))
                .perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.test))
                .check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(
                        ViewMatchers.Visibility.INVISIBLE
                )));
    }

    @Test
    public void testUIInitializationAndSendButton() {
        // Create fully qualified intent with extras
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                com.example.synergy.activities.FriendChatActivity.class
        );
        intent.putExtra("friend_name", "Alice");
        intent.putExtra("chatroom_id", 123);
        intent.putExtra("userId", 1);

        // Launch activity
        ActivityScenario<com.example.synergy.activities.FriendChatActivity> scenario =
                ActivityScenario.launch(intent);

        // Check that title is set
        Espresso.onView(ViewMatchers.withId(R.id.titleTv))
                .check(ViewAssertions.matches(ViewMatchers.withText("Alice")));

        // Type a message in the EditText
        Espresso.onView(ViewMatchers.withId(R.id.msgEdt))
                .perform(ViewActions.typeText("Hello!"), ViewActions.closeSoftKeyboard());

        // Click send button
        Espresso.onView(ViewMatchers.withId(R.id.sendBtn))
                .perform(ViewActions.click());

        // Verify that EditText is cleared after sending
        Espresso.onView(ViewMatchers.withId(R.id.msgEdt))
                .check(ViewAssertions.matches(ViewMatchers.withText("")));
    }

    @Test
    public void testReviewEventsAttendedFragmentLoads() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            String fakeUser = "{\"id\": 7}";
            reviewEventsAttendedFragment fragment =
                    reviewEventsAttendedFragment.newInstance(fakeUser);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commitNow();
        });

        // Check RecyclerView is displayed
        onView(withId(R.id.attendedEventsRv)).check(matches(isDisplayed()));
    }


    @Test
    public void testLoginBottomSheetDisplays() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Click login button
        onView(withId(R.id.button_login)).perform(click());

        // Check that the login sheet root is displayed
        onView(withId(R.id.login_sheet_root)).check(matches(isDisplayed()));
    }

    @Test
    public void testSignupBottomSheetDisplays() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Click signup button
        onView(withId(R.id.button_signup)).perform(click());

        // Check that the signup sheet root is displayed
        onView(withId(R.id.signup_sheet_root)).check(matches(isDisplayed()));
    }

    @Test
    public void testSplashScreenHidesAfterDelay() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        // Wait for splash screen to finish (slightly longer than handler delay)
        try {
            Thread.sleep(1600);
        } catch (InterruptedException ignored) {}

        // The main layout should now be visible
        onView(withId(R.id.settings_root)).check(matches(isDisplayed()));
    }

    @Test
    public void testAllUsersFragmentLoads() {
        ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);

        scenario.onActivity(activity -> {
            String fakeUser = "{\"id\": 7}";
            AllUsersFragment fragment = AllUsersFragment.newInstance(fakeUser);

            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(android.R.id.content, fragment)
                    .commitNow();
        });

        // RecyclerView should be displayed
        onView(withId(R.id.recyclerView)).check(matches(isDisplayed()));

        // SearchView should be displayed
        onView(withId(R.id.searchUsers)).check(matches(isDisplayed()));
    }







}


