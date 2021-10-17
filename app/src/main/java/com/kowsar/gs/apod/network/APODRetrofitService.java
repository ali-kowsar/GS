package com.kowsar.gs.apod.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APODRetrofitService {

    public static String BASE_URL = "https://api.nasa.gov/planetary/";
    private static Retrofit retrofit = null;

    public static APODApi getApiService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient) // to print okhttp log on Logcat.
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(APODApi.class);
    }

}
