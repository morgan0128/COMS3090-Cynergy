package com.example.myapplication.api;

import com.example.myapplication.model.Post;

import retrofit2.Call;
import retrofit2.http.GET;

// Retrofit api needs an interface
public interface PostAPI {

    @GET("posts/1")
    Call<Post> getFirstPost();
}
