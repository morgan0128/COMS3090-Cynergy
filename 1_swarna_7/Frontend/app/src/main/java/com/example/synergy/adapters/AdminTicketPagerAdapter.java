
package com.example.synergy.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.synergy.fragments.AllTicketFragment;
import com.example.synergy.fragments.ClosedTicketFragment;
import com.example.synergy.fragments.OpenTicketFragment;

public class AdminTicketPagerAdapter extends FragmentStateAdapter {

    private final int adminId;

    public AdminTicketPagerAdapter(@NonNull FragmentActivity fa, int adminId) {
        super(fa);
        this.adminId = adminId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return OpenTicketFragment.newInstance(adminId);
        if (position == 1) return ClosedTicketFragment.newInstance(adminId);
        return AllTicketFragment.newInstance(adminId);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}

