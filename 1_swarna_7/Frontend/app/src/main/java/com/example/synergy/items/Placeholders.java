package com.example.synergy.items;

import com.example.synergy.R;

public class Placeholders {

    private static final int[] IMAGES = {
            R.drawable.profile_1,
            R.drawable.profile_2,
            R.drawable.profile_3,
            R.drawable.profile_4,
    };

    public static int pick(String seed) {
        int index = Math.abs(seed.hashCode()) % IMAGES.length;
        return IMAGES[index];
    }
}
