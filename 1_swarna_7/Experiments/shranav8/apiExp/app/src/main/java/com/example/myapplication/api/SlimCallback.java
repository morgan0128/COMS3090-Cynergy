package com.example.myapplication.api;

import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SlimCallback<T> implements Callback<T>{

    LambdaInterface<T> lambdaInterface;

    String logTag;

    public SlimCallback(LambdaInterface<T> lambdainterface){
        this.lambdaInterface = lambdainterface;
    }

    public SlimCallback(LambdaInterface<T> lambdainterface, String customTag){
        this.lambdaInterface = lambdainterface;
        this.logTag = customTag;
    }



    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response.isSuccessful()){
            lambdaInterface.doSomething(response.body());

        } else {
            Log.d(logTag, "Code: " + response.code() + " Msg:" + response.message());

        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        Log.d(logTag, "Code: " + t.getMessage());
    }
}
