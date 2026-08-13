package com.example.myapplication.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientFactory {

    static Retrofit apiClientSeed = null;

    //        Retrofit API client instance creation
    static Retrofit GetApiClientSeed(){
        if (apiClientSeed == null){
            apiClientSeed = new Retrofit.Builder()
                    .baseUrl("https://jsonplaceholder.typicode.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return apiClientSeed;


    }

// Uses the GetAPIClientSeed to get information from the URL
    public static PostAPI GetPostApi(){
        return GetApiClientSeed().create(PostAPI.class);
    }
}
