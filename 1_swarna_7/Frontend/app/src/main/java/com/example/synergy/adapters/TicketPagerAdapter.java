package com.example.synergy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.synergy.fragments.DeleteUserIssueFragment;
import com.example.synergy.fragments.EventIssueFragment;

public class TicketPagerAdapter extends FragmentStateAdapter {

    private final int currentUserId;

    public TicketPagerAdapter(@NonNull FragmentActivity activity, int currentUserId) {
        super(activity);
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return EventIssueFragment.newInstance(currentUserId);
        } else {
            return DeleteUserIssueFragment.newInstance(currentUserId);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
