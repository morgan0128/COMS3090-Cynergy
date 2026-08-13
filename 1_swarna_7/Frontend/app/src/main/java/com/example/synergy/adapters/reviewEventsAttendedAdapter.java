package com.example.synergy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synergy.R;
import com.example.synergy.items.EventItem;
import com.example.synergy.sheets.sendReviewSheet;

import java.util.List;

public class reviewEventsAttendedAdapter  extends RecyclerView.Adapter<reviewEventsAttendedAdapter.reviewEventsAttendedViewHolder>{

    private FragmentManager fragmentManger;;
    private String userDetails;
    private Context context;
    private List<EventItem> eventList;

    public reviewEventsAttendedAdapter(Context context, String userDetails, List<EventItem> eventList, FragmentManager fragmentManager){
        this.context = context;
        this.eventList = eventList;
        this.userDetails = userDetails;
        this.fragmentManger = fragmentManager;
    }

    @NonNull
    @Override
    public reviewEventsAttendedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_button, parent, false);
        return new reviewEventsAttendedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reviewEventsAttendedViewHolder holder, int position){
        EventItem item = eventList.get(position);

        holder.eventTitle.setText(item.getEventName());
        holder.eventDetails.setText(item.getEventTime() + " | "+ item.getEventDate() + " | " +
                item.getEventLocation());

        int eventId = item.getId();

        holder.eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Start sheet review the clicked event
                sendReviewSheet sheet = sendReviewSheet.newInstance(userDetails, eventId);
                sheet.show(fragmentManger, "send_review_sheet");
            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class reviewEventsAttendedViewHolder extends RecyclerView.ViewHolder {
        LinearLayout eventCard;
        TextView eventTitle, eventDetails;


        public reviewEventsAttendedViewHolder(@NonNull View itemView) {
            super(itemView);
            eventCard = itemView.findViewById(R.id.eventCard);
            eventTitle = itemView.findViewById(R.id.textEventTitle);
            eventDetails = itemView.findViewById(R.id.textEventDetails);

        }
    }


}

