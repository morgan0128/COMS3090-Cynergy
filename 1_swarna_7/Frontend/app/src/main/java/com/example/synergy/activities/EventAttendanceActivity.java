package com.example.synergy.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.synergy.R;
import com.example.synergy.adapters.AttendeeAdapter;
import com.example.synergy.items.Attendee;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Activity that displays attendance information for a specific event.
 *
 * <p>This screen shows:</p>
 * <ul>
 *     <li><b>Event name</b></li>
 *     <li><b>Total attendee count</b></li>
 *     <li><b>List of attendees in a clean format</b></li>
 * </ul>
 *
 * <p>
 * The activity fetches attendance details from our backend server using a Volley GET request.
 * Also, has a button to enter the event's chat room.
 * </p>
 */
public class EventAttendanceActivity extends AppCompatActivity {


    /** TextView displaying the event name. */
    private TextView eventNameView;

    /** TextView showing the total number of attendees */
    private TextView attendeeCountTv;

    /** RecyclerView listing all attendees. */
    private RecyclerView attendeesRv;

    /** Button that will lead to the chatroom of that event. */
    private Button chatButton;

    /** List containing attendee objects returned by the API. */
    private final ArrayList<Attendee> attendees = new ArrayList<>();

    /** Adapter used to populate the RecyclerView with attendee data. */
    private AttendeeAdapter adapter;

    /** Name of the event returned from the server. */
    private String eventName;

    /** Base URL for events. */
    private static final String BASE_URL =
            "http://coms-3090-016.class.las.iastate.edu:8080/api/events/";

    /** Event ID to be passed from previous activity */
    private int eventId;

    /** ID of the user. */
    private int userId;

    /** Email detail of the user. */
    private String emailId;

    /**
     * Called when the activity starts.
     * Initializes the UI, reads the data from the intent, sets up the RecyclerView,
     * and fetches attendance information from the server.
     *
     * @param savedInstanceState saved state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_attendance);

        initUI(); //initalize UI
        readIntentData(); // read intent that was sent
        setupRecycler();
        fetchAttendance(); // getting attendance data from server

        chatButton = findViewById(R.id.chat_event_button); // chat button that will lead to that event's chat

        chatButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Clicking on the button leads to the chat for this event.
             * We will send necessary intent data to the chat.
             * @param v the clicked view
             */
            @Override
            public void onClick(View v) {
                // all necessary data to be sent
                Intent intent = new Intent(EventAttendanceActivity.this, EventChatActivity.class);
                intent.putExtra("user_id", userId);
                intent.putExtra("event_name", eventName);
                intent.putExtra("event_id", eventId);
                intent.putExtra("email_id", emailId);
                startActivity(intent); // go to chat activity
            }
        });
    }

    /**
     * Initializes all UI components on the screen.
     * This is basically seperating my UI code from the rest.
     * Also sets up the back button which we need.
     */
    private void initUI() {
        eventNameView = findViewById(R.id.event_name);
        attendeeCountTv = findViewById(R.id.attendee_count);
        attendeesRv = findViewById(R.id.attendeesRecyclerView);

        ImageButton back = findViewById(R.id.back_button); //back button
        back.setOnClickListener(v -> finish()); // goes back
    }

    /**
     * Reads the intent data passed from the previous activity.
     * Gets event ID, user ID, and email ID from the bundle.
     * If the event ID is invalid, the activity closes.
     */
    private void readIntentData() {
        Intent i = getIntent(); //get intent

        eventId = i.getIntExtra("event_id", 1); //default value 1, just in case
        userId = i.getIntExtra("user_id", 1);
        emailId = i.getStringExtra("emailId");

        if (eventId < 0) { // check if event ID is valid, otherwise go back. basically no negative values.
            showToast("Invalid event"); //show toast on screen.
            finish();
        }
    }

    /**
     * Sets up the RecyclerView used to display all attendees.
     * Creates the adapter and attaches it to the layout manager.
     */
    private void setupRecycler() {
        adapter = new AttendeeAdapter(attendees, this);
        attendeesRv.setLayoutManager(new LinearLayoutManager(this));
        attendeesRv.setAdapter(adapter);
    }

    /**
     * Sends a GET request to our backend server to get the attendance information.
     * The response is handled by handleAttendanceResponse().
     */
    private void fetchAttendance() {
        String url = BASE_URL + eventId + "/attendees"; //url to be used for GET req

        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {

                    handleAttendanceResponse(response);
                },
                error -> showToast("Could not load attendance") //error handling
        );

        Volley.newRequestQueue(this).add(req); //adding to queue
    }

    /**
     * Handles the JSON response returned by the server.
     * Updates event name, attendee count, and the attendee list.
     *
     * @param json the server response
     */
    private void handleAttendanceResponse(JSONObject json) {

        attendees.clear(); // clearing previous data if any

        eventName = json.optString("eventName", "Event");
        eventNameView.setText(eventName); //setting eventname

        int attendeeCount = json.optInt("attendeeCount", 0); //default 0
        attendeeCountTv.setText(attendeeCount + " going"); //setting attendee count

        JSONArray arr = json.optJSONArray("attendees");   // get the attendees array from the response

        if (arr != null) {   // make sure the array exists
            for (int i = 0; i < arr.length(); i++) {   // loop through each attendee object
                JSONObject o = arr.optJSONObject(i);   // get the JSON object at index i
                if (o == null) continue;               // skip if it's null

                //otherwise
                Attendee a = Attendee.fromJson(o);     // convert JSON into an Attendee object
                if (a != null) attendees.add(a);       // add to the list if valid
            }
        }

        adapter.notifyDataSetChanged();   // refresh the RecyclerView to show the new data

    }

    /**
     * Displays a toast message on the screen. mostly for error handling and testing.
     *
     * @param msg the message text
     */
    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Shows a simple dialog displaying server response text. used for testing.
     * The text is placed inside a scrollable view.
     */
    private void showStatusDialog(String title, String content) {

        // Create dialog builder
        androidx.appcompat.app.AlertDialog.Builder builder =
                new androidx.appcompat.app.AlertDialog.Builder(this);

        // TextView for showing the content
        TextView tv = new TextView(this);
        tv.setText(content);
        tv.setPadding(40, 40, 40, 40);
        tv.setTextSize(16f);

        // Make content scrollable
        ScrollView scroll = new ScrollView(this);
        scroll.addView(tv);

        // Set title
        builder.setTitle(title);
        builder.setView(scroll);

        // Add a simple Close button
        builder.setPositiveButton("Close", null);

        // show the dialogue
        builder.show();
    }

}




