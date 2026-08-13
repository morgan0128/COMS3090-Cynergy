package com.example.synergy;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static java.util.EnumSet.allOf;

import android.content.Context;


import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.viewpager2.widget.ViewPager2;

import com.example.synergy.activities.AdminDashboardActivity;
import com.example.synergy.activities.AdminTicketActivity;
import com.example.synergy.activities.AdminUserDetailActivity;
import com.example.synergy.activities.DeleteActivity;
import com.example.synergy.activities.EventAttendanceActivity;
import com.example.synergy.activities.EventChatActivity;
import com.example.synergy.activities.EventsActivity;
import com.example.synergy.activities.FriendsActivity;
import com.example.synergy.activities.HomeActivity;
import com.example.synergy.activities.MainActivity;
import com.example.synergy.activities.TicketRequestActivity;
import com.example.synergy.adapters.AdminTicketAdapter;
import com.example.synergy.adapters.AdminUserAdapter;
import com.example.synergy.adapters.AttendeeAdapter;
import com.example.synergy.adapters.EventAdapter;
import com.example.synergy.adapters.TicketPagerAdapter;
import com.example.synergy.fragments.AllTicketFragment;
import com.example.synergy.fragments.ClosedTicketFragment;
import com.example.synergy.fragments.DeleteUserIssueFragment;
import com.example.synergy.fragments.EventInfoFragment;
import com.example.synergy.fragments.EventIssueFragment;
import com.example.synergy.fragments.OpenTicketFragment;
import com.example.synergy.fragments.eventMapFragment;
import com.example.synergy.fragments.interestedEventsListFragment;
import com.example.synergy.fragments.sentRequestsFragment;
import com.example.synergy.items.AdminIssue;
import com.example.synergy.items.AdminTicket;
import com.example.synergy.items.AdminUser;
import com.example.synergy.items.Attendee;
import com.example.synergy.items.ChatMessage;
import com.example.synergy.items.EventItem;
import com.example.synergy.sheets.InviteFriendToEventSheet;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(AndroidJUnit4.class)
public class AshwinSystemTest {

    @Test
    public void checkInitialAdminState() {


        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                AdminUserDetailActivity.class
        );
        intent.putExtra("userId", 1);
        try (ActivityScenario<AdminUserDetailActivity> s = ActivityScenario.launch(intent)) {
            SystemClock.sleep(1500);

            onView(withId(R.id.tierText))
                    .check(matches(withText("Tier: 0")));

            onView(withId(R.id.grantTier1Btn))
                    .check(matches(isEnabled()));

            onView(withId(R.id.grantTier2Btn))
                    .check(matches(isEnabled()));

            onView(withId(R.id.revokeTier2Btn))
                    .check(matches(not(isEnabled())));

            onView(withId(R.id.deleteAdminBtn))
                    .check(matches(not(isEnabled())));

        }

    }

    @Test
    public void usersListAdmin() {

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                AdminDashboardActivity.class
        );

        try (ActivityScenario<AdminDashboardActivity> scenario =
                     ActivityScenario.launch(intent)) {


            SystemClock.sleep(2000);

            onView(withId(R.id.usersRecycler))
                    .check(matches(isDisplayed()));

            onView(withId(R.id.emptyView))
                    .check(matches(withText("Failed to load users")));
        }
    }

    @Test
    public void myEventsFragment_EventsRecycler() {

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                EventsActivity.class
        );

        String fakeUserDetails = "{\"id\":1,\"emailId\":\"test@example.com\"}";
        intent.putExtra("userDetails", fakeUserDetails);

        try (ActivityScenario<EventsActivity> scenario =
                     ActivityScenario.launch(intent)) {

            SystemClock.sleep(2000);

            onView(withText("My Events")).perform(click());

            SystemClock.sleep(1000);

            onView(withId(R.id.myRecyclerView))
                    .check(matches(isDisplayed()));
        }
    }


    @Test
    public void homeScreen_check() {

        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                HomeActivity.class
        );

        String fakeResponse = "{\"id\":1,\"emailId\":\"test@example.com\"}";
        intent.putExtra("response", fakeResponse);

        intent.putExtra("password", "dummyPassword");

        try (ActivityScenario<HomeActivity> ignored =
                     ActivityScenario.launch(intent)) {

            onView(withId(R.id.home_root))
                    .check(matches(isDisplayed()));
        }
    }

    //admin dashboard activity

    @Test
    public void parse_basic() {
        try (ActivityScenario<AdminDashboardActivity> s =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {

            s.onActivity(a -> {
                String j = "["
                        + "{\"id\":2,\"username\":\"a\",\"email\":\"a@a.com\"},"
                        + "{\"id\":3,\"username\":\"b\",\"email\":\"b@b.com\"}"
                        + "]";

                a.parseBasicUsers(j);

                RecyclerView rv = a.findViewById(R.id.usersRecycler);
                AdminUserAdapter ad = (AdminUserAdapter) rv.getAdapter();


                assertEquals(2, ad.getItemCount());
                assertEquals(View.GONE, a.findViewById(R.id.emptyView).getVisibility());
            });
        }
    }

    @Test
    public void skip_super() {
        try (ActivityScenario<AdminDashboardActivity> s =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {

            s.onActivity(a -> {
                String j = "["
                        + "{\"id\":1,\"username\":\"x\"},"
                        + "{\"id\":4}"
                        + "]";

                a.parseBasicUsers(j);

//                AdminUserAdapter ad =
//                        (AdminUserAdapter) a.findViewById(R.id.usersRecycler)
//
//                assertEquals(1, ad.getItemCount());
            });
        }
    }

    @Test
    public void parse_empty() {
        try (ActivityScenario<AdminDashboardActivity> s =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {

            s.onActivity(a -> {
                a.parseBasicUsers("[]");

                RecyclerView rv = a.findViewById(R.id.usersRecycler);
                AdminUserAdapter ad = (AdminUserAdapter) rv.getAdapter();


                assertEquals(0, ad.getItemCount());
                assertEquals(View.VISIBLE, a.findViewById(R.id.emptyView).getVisibility());
            });
        }
    }

    @Test
    public void click_user() {
        try (ActivityScenario<AdminDashboardActivity> s =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {

            s.onActivity(a -> {
                AdminUser u = new AdminUser(42, "x", "x@x.com", 1);
                a.onUserClicked(u);
            });
        }
    }

    // adminticketactivity

    public void launch_basic() {
        try (ActivityScenario<AdminTicketActivity> s =
                     ActivityScenario.launch(AdminTicketActivity.class)) {

            s.onActivity(a -> {
                assertNotNull(a.findViewById(R.id.viewPager));
                assertNotNull(a.findViewById(R.id.tabLayout));
            });
        }
    }

    @Test
    public void open_admins() {
        Intents.init();

        try (ActivityScenario<AdminTicketActivity> s =
                     ActivityScenario.launch(AdminTicketActivity.class)) {

            s.onActivity(a ->
                    a.findViewById(R.id.manageAdminsBtn).performClick()
            );

            intended(hasComponent(AdminDashboardActivity.class.getName()));
        }

        Intents.release();
    }


    @Test
    public void ui_t0() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                AdminUserDetailActivity.class
        );
        i.putExtra("userId", 5);

        try (ActivityScenario<AdminUserDetailActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                try {
                    Field f = AdminUserDetailActivity.class.getDeclaredField("tier");
                    f.setAccessible(true);
                    f.setInt(a, 0);

                    Method m = AdminUserDetailActivity.class.getDeclaredMethod("updateUI");
                    m.setAccessible(true);
                    m.invoke(a);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                TextView t = a.findViewById(R.id.tierText);
                assertEquals("Tier: 0", t.getText().toString());

                assertTrue(a.findViewById(R.id.grantTier1Btn).isEnabled());
                assertTrue(a.findViewById(R.id.grantTier2Btn).isEnabled());
                assertFalse(a.findViewById(R.id.revokeTier2Btn).isEnabled());
                assertFalse(a.findViewById(R.id.deleteAdminBtn).isEnabled());
            });
        }
    }

    @Test
    public void ui_t2() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                AdminUserDetailActivity.class
        );
        i.putExtra("userId", 5);

        try (ActivityScenario<AdminUserDetailActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                try {
                    Field f = AdminUserDetailActivity.class.getDeclaredField("tier");
                    f.setAccessible(true);
                    f.setInt(a, 2);

                    Method m = AdminUserDetailActivity.class.getDeclaredMethod("updateUI");
                    m.setAccessible(true);
                    m.invoke(a);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                TextView t = a.findViewById(R.id.tierText);
                assertEquals("Tier: 2", t.getText().toString());

                assertFalse(a.findViewById(R.id.grantTier1Btn).isEnabled());
                assertFalse(a.findViewById(R.id.grantTier2Btn).isEnabled());
                assertTrue(a.findViewById(R.id.revokeTier2Btn).isEnabled());
                assertTrue(a.findViewById(R.id.deleteAdminBtn).isEnabled());
            });
        }
    }

    @Test
    public void handle_t0_and_t2() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                AdminUserDetailActivity.class
        );
        i.putExtra("userId", 5);

        try (ActivityScenario<AdminUserDetailActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                try {
                    Field f = AdminUserDetailActivity.class.getDeclaredField("tier");
                    f.setAccessible(true);

                    f.setInt(a, 0);
                    Method m1 = AdminUserDetailActivity.class.getDeclaredMethod("handleTier2");
                    m1.setAccessible(true);
                    m1.invoke(a);

                    f.setInt(a, 2);
                    Method m2 = AdminUserDetailActivity.class.getDeclaredMethod("handleTier2");
                    m2.setAccessible(true);
                    m2.invoke(a);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Test
    public void revoke_not2() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                AdminUserDetailActivity.class
        );
        i.putExtra("userId", 5);

        try (ActivityScenario<AdminUserDetailActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                try {
                    Field f = AdminUserDetailActivity.class.getDeclaredField("tier");
                    f.setAccessible(true);
                    f.setInt(a, 1);

                    Method m = AdminUserDetailActivity.class.getDeclaredMethod("revokeTier2");
                    m.setAccessible(true);
                    m.invoke(a);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    // delete

    @Test
    public void delete_onCreate_setsFields() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                DeleteActivity.class
        );
        String resp = "{'emailId':'a@a.com','userPassword':'p123'}";
        i.putExtra("response", resp);

        try (ActivityScenario<DeleteActivity> s = ActivityScenario.launch(i)) {
            s.onActivity(a -> {
                TextView e = a.findViewById(R.id.deleteEmail);
                TextView p = a.findViewById(R.id.deletePassword);

                assertEquals("a@a.com", e.getText().toString());
                assertEquals("p123", p.getText().toString());
            });
        }
    }

    @Test
    public void delete_showDialog_runs() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                DeleteActivity.class
        );

        try (ActivityScenario<DeleteActivity> s = ActivityScenario.launch(i)) {
            s.onActivity(a -> {
                try {
                    Method m = DeleteActivity.class.getDeclaredMethod(
                            "showStatusDialog",
                            String.class,
                            String.class
                    );
                    m.setAccessible(true);
                    m.invoke(a, "Title", "Msg");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    // event attendance


    @Test
    public void attendance_handle_basic() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                EventAttendanceActivity.class
        );
        i.putExtra("event_id", 10);
        i.putExtra("user_id", 3);
        i.putExtra("emailId", "a@a.com");

        try (ActivityScenario<EventAttendanceActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                try {
                    JSONObject json = new JSONObject();
                    json.put("eventName", "Party");
                    json.put("attendeeCount", 2);

                    JSONArray arr = new JSONArray();
                    JSONObject o1 = new JSONObject();
                    o1.put("id", 1);
                    o1.put("name", "x");
                    arr.put(o1);
                    json.put("attendees", arr);

                    Method m = EventAttendanceActivity.class
                            .getDeclaredMethod("handleAttendanceResponse", JSONObject.class);
                    m.setAccessible(true);
                    m.invoke(a, json);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                TextView name = a.findViewById(R.id.event_name);
                TextView cnt = a.findViewById(R.id.attendee_count);

                assertEquals("Party", name.getText().toString());
                assertEquals("2 going", cnt.getText().toString());
            });
        }
    }

    @Test
    public void attendance_chat_click() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                EventAttendanceActivity.class
        );
        i.putExtra("event_id", 11);
        i.putExtra("user_id", 4);
        i.putExtra("emailId", "b@b.com");

        Intents.init();

        try (ActivityScenario<EventAttendanceActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                Button b = a.findViewById(R.id.chat_event_button);
                b.performClick();
            });

            intended(hasComponent(EventChatActivity.class.getName()));
        }

        Intents.release();
    }

    @Test
    public void attendance_dialog_runs() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                EventAttendanceActivity.class
        );
        i.putExtra("event_id", 12);
        i.putExtra("user_id", 5);
        i.putExtra("emailId", "c@c.com");

        try (ActivityScenario<EventAttendanceActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                try {
                    Method m = EventAttendanceActivity.class
                            .getDeclaredMethod("showStatusDialog", String.class, String.class);
                    m.setAccessible(true);
                    m.invoke(a, "Title", "Body");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    //homeactivity

    public void home_onCreate_parses() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                HomeActivity.class
        );

        JSONObject obj = new JSONObject();
        try {
            obj.put("id", 12);
            obj.put("emailId", "user@test.com");
            obj.put("password", "x");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        i.putExtra("response", obj.toString());
        i.putExtra("password", "x");

        try (ActivityScenario<HomeActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                try {
                    Field fEmail = HomeActivity.class.getDeclaredField("email");
                    fEmail.setAccessible(true);
                    String e = (String) fEmail.get(a);

                    Field fId = HomeActivity.class.getDeclaredField("userId");
                    fId.setAccessible(true);
                    int id = fId.getInt(a);

                    assertEquals("user@test.com", e);
                    assertEquals(12, id);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    @Test
    public void home_dialog_runs() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                HomeActivity.class
        );

        JSONObject obj = new JSONObject();
        try {
            obj.put("id", 13);
            obj.put("emailId", "x@y.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        i.putExtra("response", obj.toString());
        i.putExtra("password", "y");

        try (ActivityScenario<HomeActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                try {
                    Method m = HomeActivity.class
                            .getDeclaredMethod("showStatusDialog", boolean.class);
                    m.setAccessible(true);
                    m.invoke(a, false);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    @Test
    public void ticket_basic() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                TicketRequestActivity.class
        );
        i.putExtra("userId", 44);

        try (ActivityScenario<TicketRequestActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                assertNotNull(a.findViewById(R.id.ticketTabLayout));
                assertNotNull(a.findViewById(R.id.ticketViewPager));
                assertEquals("File a Request",
                        ((android.widget.TextView) a.findViewById(R.id.titleText))
                                .getText().toString());
            });
        }
    }

    @Test
    public void ticket_adapter() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                TicketRequestActivity.class
        );
        i.putExtra("userId", 55);

        try (ActivityScenario<TicketRequestActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                TicketPagerAdapter ad =
                        new TicketPagerAdapter(a, 55);
                assertEquals(2, ad.getItemCount());
                assertNotNull(ad.createFragment(0));
                assertNotNull(ad.createFragment(1));
            });
        }
    }

    @Test
    public void ticket_back() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                TicketRequestActivity.class
        );
        try (ActivityScenario<TicketRequestActivity> s =
                     ActivityScenario.launch(i)) {
            s.onActivity(a ->
                    a.findViewById(R.id.back_button).performClick()
            );
        }
    }

    @Test
    public void home_receiver_runs() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                HomeActivity.class
        );

        JSONObject obj = new JSONObject();
        try {
            obj.put("id", 14);
            obj.put("emailId", "n@n.com");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        i.putExtra("response", obj.toString());
        i.putExtra("password", "z");

        try (ActivityScenario<HomeActivity> s =
                     ActivityScenario.launch(i)) {

            s.onActivity(a -> {
                try {
                    Field f = HomeActivity.class.getDeclaredField("webSocketReceiver");
                    f.setAccessible(true);
                    Object r = f.get(a);

                    Intent msg = new Intent("WebSocketMessageReceived");
                    msg.putExtra("key", "notifications");
                    msg.putExtra("message", "hi");

                    Method m = r.getClass()
                            .getDeclaredMethod("onReceive", Context.class, Intent.class);
                    m.setAccessible(true);
                    m.invoke(r, a, msg);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            });


            //ticketrequest

        }
    }


    @Test
    public void user_count() {
        List<AdminUser> list = new ArrayList<>();
        list.add(new AdminUser(1, "a", "a@a.com", 1));
        list.add(new AdminUser(2, "b", "b@b.com", 2));

        AdminUserAdapter ad = new AdminUserAdapter(list, u -> {
        });
        assertEquals(2, ad.getItemCount());
    }

    @Test
    public void count_ok() {
        List<AdminUser> list = new ArrayList<>();
        list.add(new AdminUser(1, "a", "a@a.com", 1));
        list.add(new AdminUser(2, "b", "b@b.com", 2));

        AdminUserAdapter ad = new AdminUserAdapter(list, u -> {
        });
        assertEquals(2, ad.getItemCount());
    }


    @Test
    public void count_ok_attendee() {
        List<Attendee> list = new ArrayList<>();
        list.add(new Attendee(1, "a", 0));
        list.add(new Attendee(2, "b", 0));

        AttendeeAdapter ad = new AttendeeAdapter(list, ApplicationProvider.getApplicationContext());
        assertEquals(2, ad.getItemCount());
    }


    @Test
    public void issue_basic() {
        AdminIssue i = new AdminIssue(1, "T", "A", "D", true);

        assertEquals(1, i.getId());
        assertEquals("T", i.getType());
        assertEquals("A", i.getTitle());
        assertEquals("D", i.getDescription());
        assertTrue(i.isOpen());
    }

    @Test
    public void issue_nulls() {
        AdminIssue i = new AdminIssue(2, null, null, null, false);

        assertEquals("", i.getType());
        assertEquals("", i.getTitle());
        assertEquals("", i.getDescription());
        assertFalse(i.isOpen());
    }

    @Test
    public void issue_set_open() {
        AdminIssue i = new AdminIssue(3, "X", "Y", "Z", true);
        i.setOpen(false);
        assertFalse(i.isOpen());
    }

    @Test
    public void issue_from_json_main() throws Exception {
        JSONObject o = new JSONObject();
        o.put("adminIssueId", 10);
        o.put("type", "EVENT");
        o.put("title", "Bad Event");
        o.put("description", "Details");

        AdminIssue i = AdminIssue.fromJson(o, true);

        assertEquals(10, i.getId());
        assertEquals("EVENT", i.getType());
        assertEquals("Bad Event", i.getTitle());
        assertEquals("Details", i.getDescription());
        assertTrue(i.isOpen());
    }

    @Test
    public void issue_from_json_fallback() throws Exception {
        JSONObject o = new JSONObject();
        o.put("id", 11);
        o.put("issueType", "USER");
        o.put("summary", "User Issue");
        o.put("details", "More");

        AdminIssue i = AdminIssue.fromJson(o, false);

        assertEquals(11, i.getId());
        assertEquals("USER", i.getType());
        assertEquals("User Issue", i.getTitle());
        assertEquals("More", i.getDescription());
        assertFalse(i.isOpen());
    }

    @Test
    public void ticket_basic1() {
        AdminTicket t = new AdminTicket(
                1,
                "EVENT",
                "PENDING",
                false,
                "desc",
                7,
                "user"
        );

        assertEquals(1, t.getIssueId());
        assertEquals("EVENT", t.getType());
        assertEquals("PENDING", t.getStatus());
        assertEquals("desc", t.getDescription());
        assertEquals(7, t.getProposedUserId());
        assertEquals("user", t.getProposedUsername());
        assertFalse(t.isResolved());
        assertTrue(t.isPending());
    }

    @Test
    public void ticket_nulls() {
        AdminTicket t = new AdminTicket(
                2,
                null,
                null,
                true,
                null,
                -1,
                null
        );

        assertEquals("", t.getStatus());
        assertEquals("", t.getDescription());
        assertEquals("", t.getProposedUsername());
        assertFalse(t.isPending());
        assertTrue(t.isResolved());
    }

    @Test
    public void ticket_set_status() {
        AdminTicket t = new AdminTicket(
                3,
                "USER",
                "PENDING",
                false,
                "x",
                1,
                "u"
        );

        t.setStatus("APPROVED");

        assertEquals("APPROVED", t.getStatus());
        assertFalse(t.isPending());
    }

    @Test
    public void ticket_pending_case() {
        AdminTicket t = new AdminTicket(
                4,
                "EVENT",
                "pending",
                false,
                "y",
                2,
                "v"
        );

        assertTrue(t.isPending());
    }

    @Test
    public void basic() {
        AdminUser u = new AdminUser(5, "Ashwin", "a@x.com", 1);

        assertEquals(5, u.getId());
        assertEquals("Ashwin", u.getName());
        assertEquals("a@x.com", u.getEmail());
        assertEquals(1, u.getTier());
    }

    @Test
    public void nulls() {
        AdminUser u = new AdminUser(10, null, null, 0);

        assertEquals(10, u.getId());
        assertEquals("", u.getName());
        assertEquals("", u.getEmail());
        assertEquals(0, u.getTier());
    }

    @Test
    public void tierSet() {
        AdminUser u = new AdminUser(2, "n", "e", 1);
        u.setTier(3);
        assertEquals(3, u.getTier());
    }

    @Test
    public void basic1() {
        Attendee a = new Attendee(1, "User", 5);

        assertEquals(1, a.getId());
        assertEquals("User", a.getName());
        assertEquals(5, a.getAvatarResId());
    }

    @Test
    public void from_json_ok() throws Exception {
        JSONObject o = new JSONObject();
        o.put("id", 3);
        o.put("name", "Alex");

        Attendee a = Attendee.fromJson(o);

        assertNotNull(a);
        assertEquals(3, a.getId());
        assertEquals("Alex", a.getName());
        assertNotNull(a.getAvatarResId());
    }

    @Test
    public void from_json_null() {
        Attendee a = Attendee.fromJson(null);
        assertEquals(null, a);
    }

    @Test
    public void from_json_defaults() throws Exception {
        JSONObject o = new JSONObject();

        Attendee a = Attendee.fromJson(o);

        assertNotNull(a);
        assertEquals(-1, a.getId());
        assertEquals("Unknown", a.getName());
    }

    @Test
    public void home_admin_flow() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                HomeActivity.class
        );
        i.putExtra("response",
                "{\"emailId\":\"admin@test.com\",\"id\":1,\"userName\":\"Admin\"}");
        i.putExtra("password", "pw");

        try (ActivityScenario<HomeActivity> s = ActivityScenario.launch(i)) {
            onView(withId(R.id.home_root)).check(matches(isDisplayed()));
            onView(withId(R.id.adminButton)).perform(click());
        }

        try (ActivityScenario<AdminTicketActivity> s2 =
                     ActivityScenario.launch(AdminTicketActivity.class)) {

            onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
            onView(withId(R.id.manageAdminsBtn)).perform(click());
        }
    }


    @Test
    public void home_ticket_flow() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                HomeActivity.class
        );
        i.putExtra("response",
                "{\"emailId\":\"user@test.com\",\"id\":2,\"userName\":\"User\"}");
        i.putExtra("password", "pw");

        try (ActivityScenario<HomeActivity> s = ActivityScenario.launch(i)) {
            onView(withId(R.id.home_root)).check(matches(isDisplayed()));
            onView(withId(R.id.fileTicketButton)).perform(click());
        }

        Intent t = new Intent(
                ApplicationProvider.getApplicationContext(),
                TicketRequestActivity.class
        );
        t.putExtra("userId", 2);

        try (ActivityScenario<TicketRequestActivity> s2 =
                     ActivityScenario.launch(t)) {

            onView(withId(R.id.ticketTabLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.ticketViewPager)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void event_attend_flow() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                EventAttendanceActivity.class
        );
        i.putExtra("event_id", 1);
        i.putExtra("user_id", 3);
        i.putExtra("emailId", "user@test.com");

        try (ActivityScenario<EventAttendanceActivity> s =
                     ActivityScenario.launch(i)) {

            onView(withId(R.id.event_name)).check(matches(isDisplayed()));
            onView(withId(R.id.attendee_count)).check(matches(isDisplayed()));
            onView(withId(R.id.attendeesRecyclerView)).check(matches(isDisplayed()));
            onView(withId(R.id.chat_event_button)).perform(click());
        }
    }

    @Test
    public void home_profile_sheet_shows() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                HomeActivity.class
        );
        i.putExtra("response",
                "{\"emailId\":\"user@test.com\",\"id\":2,\"userName\":\"User\"}");
        i.putExtra("password", "pw");

        try (ActivityScenario<HomeActivity> scenario = ActivityScenario.launch(i)) {
            onView(withId(R.id.home_root)).check(matches(isDisplayed()));

            // Open create profile bottom sheet
            onView(withId(R.id.create_profile_button)).perform(click());

            // Just verify the sheet's title, not profileButton
            onView(withId(R.id.createProfileTitle)).check(matches(isDisplayed()));
        }
    }


    @Test
    public void ticket_request_tabs_visible() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                TicketRequestActivity.class
        );
        i.putExtra("userId", 2);

        try (ActivityScenario<TicketRequestActivity> scenario =
                     ActivityScenario.launch(i)) {

            onView(withId(R.id.ticketTabLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.ticketViewPager)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void admin_ticket_activity_launches() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                AdminTicketActivity.class
        );

        try (ActivityScenario<AdminTicketActivity> scenario =
                     ActivityScenario.launch(i)) {
            onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
        }
    }

    // --- 5) Event attendance basic flow ---
    @Test
    public void event_attendance_launch_and_chat_button() {
        Intent i = new Intent(
                ApplicationProvider.getApplicationContext(),
                EventAttendanceActivity.class
        );
        i.putExtra("event_id", 1);
        i.putExtra("user_id", 3);
        i.putExtra("emailId", "user@test.com");

        try (ActivityScenario<EventAttendanceActivity> scenario =
                     ActivityScenario.launch(i)) {

            onView(withId(R.id.event_name)).check(matches(isDisplayed()));
            onView(withId(R.id.attendee_count)).check(matches(isDisplayed()));
            onView(withId(R.id.attendeesRecyclerView)).check(matches(isDisplayed()));

            onView(withId(R.id.chat_event_button)).perform(click());
        }
    }

    private static final String USER_JSON =
            "{\"id\":1,\"emailId\":\"user@test.com\",\"username\":\"Ashwin\"}";

    private Context ctx() {
        return ApplicationProvider.getApplicationContext();
    }

    private Intent homeIntent() {
        Intent i = new Intent(ctx(), HomeActivity.class);
        i.putExtra("response", USER_JSON);
        i.putExtra("password", "pw");
        return i;
    }

    private Intent eventsIntent() {
        Intent i = new Intent(ctx(), EventsActivity.class);
        i.putExtra("userDetails", USER_JSON);
        return i;
    }


    @Test
    public void events_tabs() {
        try (ActivityScenario<EventsActivity> sc = ActivityScenario.launch(eventsIntent())) {
            onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
            onView(withText("All Events")).check(matches(isDisplayed()));
        }
    }

    @Test
    public void admin_dash_basic() {
        try (ActivityScenario<AdminDashboardActivity> sc =
                     ActivityScenario.launch(AdminDashboardActivity.class)) {
            onView(withId(R.id.usersRecycler)).check(matches(isDisplayed()));
            onView(withId(R.id.back_button)).perform(click());
        }
    }

    @Test
    public void admin_ticket_tabs() {
        try (ActivityScenario<AdminTicketActivity> sc =
                     ActivityScenario.launch(AdminTicketActivity.class)) {
            onView(withId(R.id.tabLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.viewPager)).check(matches(isDisplayed()));
            onView(withId(R.id.manageAdminsBtn)).perform(click());
        }
    }

    @Test
    public void ticket_req_tabs() {
        Intent i = new Intent(ctx(), TicketRequestActivity.class);
        i.putExtra("userId", 1);
        try (ActivityScenario<TicketRequestActivity> sc = ActivityScenario.launch(i)) {
            onView(withId(R.id.ticketTabLayout)).check(matches(isDisplayed()));
            onView(withId(R.id.ticketViewPager)).check(matches(isDisplayed()));
            onView(withId(R.id.back_button)).perform(click());
        }
    }

    @Test
    public void delete_prefill() throws Exception {
        JSONObject body = new JSONObject();
        body.put("emailId", "delete@test.com");
        body.put("userPassword", "secret");

        Intent i = new Intent(ctx(), DeleteActivity.class);
        i.putExtra("response", body.toString());

        try (ActivityScenario<DeleteActivity> sc = ActivityScenario.launch(i)) {
            onView(withId(R.id.deleteEmail)).check(matches(withText("delete@test.com")));
            onView(withId(R.id.deletePassword)).check(matches(withText("secret")));
        }
    }

    @Test
    public void admin_detail_start() {
        Intent i = new Intent(ctx(), AdminUserDetailActivity.class);
        i.putExtra("userId", 2);
        try (ActivityScenario<AdminUserDetailActivity> sc =
                     ActivityScenario.launch(i)) {
            onView(withId(R.id.grantTier1Btn)).check(matches(isDisplayed()));
            onView(withId(R.id.grantTier2Btn)).check(matches(isDisplayed()));
            onView(withId(R.id.deleteAdminBtn)).check(matches(isDisplayed()));

            onView(withId(R.id.grantTier1Btn)).perform(click());
            onView(withId(R.id.grantTier2Btn)).perform(click());
        }
    }

    @Test
    public void attendance_screen() {
        Intent i = new Intent(ctx(), EventAttendanceActivity.class);
        i.putExtra("event_id", 1);
        i.putExtra("user_id", 1);
        i.putExtra("emailId", "user@test.com");

        try (ActivityScenario<EventAttendanceActivity> sc =
                     ActivityScenario.launch(i)) {
            onView(withId(R.id.event_name)).check(matches(isDisplayed()));
            onView(withId(R.id.attendee_count)).check(matches(isDisplayed()));
            onView(withId(R.id.attendeesRecyclerView)).check(matches(isDisplayed()));
            onView(withId(R.id.chat_event_button)).perform(click());
        }
    }

    @Test
    public void admin_user_model() {
        AdminUser u = new AdminUser(5, "Name", "n@test.com", 1);
        assertThat(u.getId(), is(5));
        assertThat(u.getName(), is("Name"));
        assertThat(u.getEmail(), is("n@test.com"));
        assertThat(u.getTier(), is(1));
        u.setTier(2);
        assertThat(u.getTier(), is(2));
    }

    @Test
    public void admin_ticket_model() {
        AdminTicket t = new AdminTicket(10, "USERISSUE", "PENDING",
                false, "desc", 7, "bob");
        assertThat(t.getIssueId(), is(10));
        assertThat(t.getType(), is("USERISSUE"));
        assertThat(t.getStatus(), is("PENDING"));
        assertThat(t.isResolved(), is(false));
        assertThat(t.getDescription(), is("desc"));
        assertThat(t.getProposedUserId(), is(7));
        assertThat(t.getProposedUsername(), is("bob"));
        assertThat(t.isPending(), is(true));
        t.setStatus("APPROVED");
        assertThat(t.isPending(), is(false));
    }

    @Test
    public void admin_issue_model() throws Exception {
        JSONObject o = new JSONObject();
        o.put("adminIssueId", 3);
        o.put("type", "EVENTSISSUE");
        o.put("title", "t");
        o.put("description", "d");

        AdminIssue issue = AdminIssue.fromJson(o, true);
        assertThat(issue.getId(), is(3));
        assertThat(issue.getType(), is("EVENTSISSUE"));
        assertThat(issue.getTitle(), is("t"));
        assertThat(issue.getDescription(), is("d"));
        assertThat(issue.isOpen(), is(true));
        issue.setOpen(false);
        assertThat(issue.isOpen(), is(false));
    }

    @Test
    public void attendee_model() throws Exception {
        JSONObject o = new JSONObject();
        o.put("id", 9);
        o.put("name", "Person");
        Attendee a = Attendee.fromJson(o);
        assertThat(a.getId(), is(9));
        assertThat(a.getName(), is("Person"));
    }

    @Test
    public void chat_model() {
        ChatMessage m = new ChatMessage("hi", "u1", 123L);
        assertThat(m.text, is("hi"));
        assertThat(m.senderId, is("u1"));
        assertThat(m.timestamp, is(123L));
    }

    @Test
    public void event_item_model() {
        EventItem e = new EventItem(4, "Party", "7pm", "Campus", "2024-12-10","short hangout");
        assertThat(e.getId(), is(4));
        assertThat(e.getEventName(), is("Party"));
        assertThat(e.getEventTime(), is("7pm"));
        assertThat(e.getEventLocation(), is("Campus"));
        assertThat(e.getEventDate(), is("2024-12-10"));
    }

    @Test
    public void admin_user_adapter() {
        List<AdminUser> list = Arrays.asList(
                new AdminUser(1, "A", "a@test.com", 1),
                new AdminUser(2, "B", "b@test.com", 2)
        );
        AdminUserAdapter.OnUserClick click = u -> {};
        AdminUserAdapter ad = new AdminUserAdapter(list, click);

        FrameLayout parent = new FrameLayout(ctx());
        RecyclerView.ViewHolder vh = ad.onCreateViewHolder(parent, 0);

        assertThat(ad.getItemCount(), is(2));
    }

    @Test
    public void attendee_adapter() {
        List<Attendee> list = new ArrayList<>();
        list.add(new Attendee(1, "X", 0));
        list.add(new Attendee(2, "Y", 0));

        AttendeeAdapter ad = new AttendeeAdapter(list, ctx());
        FrameLayout parent = new FrameLayout(ctx());
        RecyclerView.ViewHolder vh = ad.onCreateViewHolder(parent, 0);
        ad.onBindViewHolder((AttendeeAdapter.VH) vh, 1);
        assertThat(ad.getItemCount(), is(2));
    }

    @Test
    public void admin_ticket_adapter() {
        List<AdminTicket> list = Arrays.asList(
                new AdminTicket(1, "USERISSUE", "PENDING",
                        false, "one", 3, "u"),
                new AdminTicket(2, "EVENTSISSUE", "APPROVED",
                        true, "two", 4, "v")
        );
        AdminTicketAdapter.TicketActionListener l =
                new AdminTicketAdapter.TicketActionListener() {
                    @Override
                    public void onApprove(AdminTicket ticket) { }
                    @Override
                    public void onDeny(AdminTicket ticket) { }
                };

        AdminTicketAdapter ad = new AdminTicketAdapter(list, l);
        FrameLayout parent = new FrameLayout(ctx());
        RecyclerView.ViewHolder vh = ad.onCreateViewHolder(parent, 0);
        assertThat(ad.getItemCount(), is(2));
    }
    

}


