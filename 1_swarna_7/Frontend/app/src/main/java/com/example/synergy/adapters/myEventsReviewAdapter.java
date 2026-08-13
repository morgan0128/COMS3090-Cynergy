package com.example.synergy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synergy.R;
import com.example.synergy.items.EventItem;
import com.example.synergy.sheets.myEventReviewsSheet;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;

public class myEventsReviewAdapter  extends RecyclerView.Adapter<myEventsReviewAdapter.myEventsReviewViewHolder>{

    private FragmentManager fragmentManger;;
    private String userDetails;
    private Context context;
    private List<EventItem> eventList;

    public myEventsReviewAdapter(Context context, String userDetails, List<EventItem> eventList, FragmentManager fragmentManager){
        this.context = context;
        this.eventList = eventList;
        this.userDetails = userDetails;
        this.fragmentManger = fragmentManager;
    }

    @NonNull
    @Override
    public myEventsReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(context).inflate(R.layout.item_button, parent, false);
        return new myEventsReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myEventsReviewViewHolder holder, int position){
        EventItem item = eventList.get(position);

        holder.eventTitle.setText(item.getEventName());
        holder.eventDetails.setText(item.getEventTime() + " | "+ item.getEventDate() + " | " +
                item.getEventLocation());


        holder.eventCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Start sheet to view all reviews of my event here
                myEventReviewsSheet sheet =
                        myEventReviewsSheet.newInstance(userDetails, item.getId(), item.getEventName());

                sheet.show(fragmentManger, "my_event_reviews");
            }
        });

    }
    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class myEventsReviewViewHolder extends RecyclerView.ViewHolder {
        LinearLayout eventCard;
        TextView eventTitle, eventDetails;


        public myEventsReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            eventCard = itemView.findViewById(R.id.eventCard);
            eventTitle = itemView.findViewById(R.id.textEventTitle);
            eventDetails = itemView.findViewById(R.id.textEventDetails);

        }
    }


}
