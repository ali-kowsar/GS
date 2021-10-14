package com.kowsar.gs.apod.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APODRetrofitService {

    public static String BASE_URL = "https://api.nasa.gov/planetary/";
    private static Retrofit retrofit = null;

    public static APODApi getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(APODApi.class);
    }
}
