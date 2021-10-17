package com.kowsar.gs.apod.network;

import com.kowsar.gs.apod.model.response.APODResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APODApi {

    String apod_key="Kd5K7JqQLWIraJ462ddK6jfm8vgCb6ach91Z6VQN";

    @GET("apod?api_key="+apod_key+"&thumbs=True")
    Call<APODResponse> getAPOD(/*@Query("date") String date */);

    @GET("apod?api_key="+apod_key+"&thumbs=True")
    Call<APODResponse> getAPODByDate(@Query("date") String date);
}
