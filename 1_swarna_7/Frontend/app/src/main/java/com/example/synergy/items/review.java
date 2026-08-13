package com.example.synergy.items;

public class review {
    private int rating;
    private String userName;
    private String comment;

    public review(String userName, String comment, int rating){
        this.comment = comment;
        this.rating = rating;
        this.userName = userName;
    }

    public int getRating(){ return this.rating;}

    public String getUserName(){ return this.userName;}
    public String getComment(){ return this.comment;}
}
