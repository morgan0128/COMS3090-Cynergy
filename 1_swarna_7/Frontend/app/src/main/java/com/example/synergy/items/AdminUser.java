package com.example.synergy.items;

import java.io.Serializable;

public class AdminUser implements Serializable {

    private final int id;
    private final String name;
    private final String email;
    private int tier;  // must NOT be final so we can update it

    public AdminUser(int id, String name, String email, int tier) {
        this.id = id;
        this.name = name == null ? "" : name;
        this.email = email == null ? "" : email;
        this.tier = tier;
    }
    // hello yml
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getTier() {
        return tier;
    }

    public void setTier(int tier) {
        this.tier = tier;
    }
}
