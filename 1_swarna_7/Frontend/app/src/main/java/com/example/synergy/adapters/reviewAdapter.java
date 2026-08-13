package com.example.synergy.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.synergy.R;
import com.example.synergy.items.review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class reviewAdapter extends RecyclerView.Adapter<reviewAdapter.reviewViewHolder> {
    private JSONArray reviewList;
    private Context context;
    private String userDetails;


    public reviewAdapter(Context context, String userDetails, JSONArray reviews){
        this.context = context;
        this.userDetails = userDetails;
        this.reviewList = reviews;
    }

    @NonNull
    @Override
    public reviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new reviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull reviewViewHolder holder, int position) {
        review review = null;
        try {
            JSONObject reviewObj = (JSONObject) reviewList.get(position);
            Log.d("REVIEW", reviewObj.toString());
            int rating = reviewObj.getInt("rating");
            String userName = reviewObj.getString("userName");
            String comment = reviewObj.getString("comment");
            review = new review(userName, comment, rating);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        holder.reviewerName.setText(review.getUserName());
        holder.comment.setText(review.getComment());
        holder.rating.setText(review.getRating() + "/5");




    }

    @Override
    public int getItemCount() {
        return reviewList.length();
    }

    public static class reviewViewHolder extends RecyclerView.ViewHolder {
        LinearLayout review;
        TextView reviewerName, comment, rating;

        public reviewViewHolder(@NonNull View itemView){
            super(itemView);
            review = itemView.findViewById(R.id.reviewCard);
            reviewerName = itemView.findViewById(R.id.userNameTv);
            comment = itemView.findViewById(R.id.CommentTv);
            rating = itemView.findViewById(R.id.ratingTv);

        }
    }
}
