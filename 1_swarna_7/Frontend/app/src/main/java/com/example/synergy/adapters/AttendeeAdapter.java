package com.example.synergy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synergy.items.Attendee;
import com.example.synergy.R;

import java.util.List;

/**
 * Adapter used to display a list of Attendee objects inside a RecyclerView.
 * Each row shows the attendee's username and their user image.
 * uses VH (ViewHolder)
 */
public class AttendeeAdapter extends RecyclerView.Adapter<AttendeeAdapter.VH> {

    /** The list of attendee data that is to be displayed */
    private final List<Attendee> data;

    /** Context used to inflate layouts (to create the row view). */
    private final Context context;

    /**
     * Creates a new AttendeeAdapter.
     * expects data and context from the activity
     * constructor
     *
     * @param data list of attendees
     * @param context activity or fragment context
     */
    public AttendeeAdapter(List<Attendee> data, Context context) {
        this.data = data;
        this.context = context;
    }

    /**
     * ViewHolder representing a single row in the attendee list.
     * Holds the username and image views.
     */
    public static class VH extends RecyclerView.ViewHolder {

        /** TextView showing the attendee's username. */
        TextView username;

        /** ImageView showing the attendee's user image. */
        ImageView image;

        /**
         * Creates a ViewHolder for one attendee row.
         *
         * @param v the inflated row layout
         */
        VH(View v) {
            super(v);
            username = v.findViewById(R.id.username); //the user name
            image = v.findViewById(R.id.attendeeImage); // user image
        }
    }

    /**
     * Called when RecyclerView needs a new row (ViewHolder).
     *
     * @param parent container for the row
     * @param viewType type of view
     * @return a new ViewHolder for the row
     */
    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context) //gets inflator which is needed to turn xml into view
                .inflate(R.layout.activity_attendance_row, parent, false); //creating a view using that inflator

        return new VH(v); // wrapping the inflated row inside VH
    }

    /**
     * fills row with data.
     * specific position to identify the row to fill.
     *
     * @param holder ViewHolder for the row
     * @param pos position of the attendee in the list
     */
    @Override
    public void onBindViewHolder(@NonNull VH holder, int pos) {

        // Get attendee object at this position
        Attendee a = data.get(pos);

        // Set the name and image
        holder.username.setText(a.getName());
        holder.image.setImageResource(a.getAvatarResId());
    }

    /**
     * Returns the total number of attendees in the list.
     *
     * @return item count
     */
    @Override
    public int getItemCount() {
        return data.size(); // attendee size
    }
}
