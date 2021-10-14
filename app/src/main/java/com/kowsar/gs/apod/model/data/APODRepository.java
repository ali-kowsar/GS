package com.kowsar.gs.apod.model.data;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.kowsar.gs.apod.model.response.APODResponse;
import com.kowsar.gs.apod.network.APODApi;
import com.kowsar.gs.apod.network.APODRetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APODRepository {
    private final String TAG= this.getClass().getSimpleName();
    private static APODRepository apodRepo;
    public static APODRepository getInstance(){
        if(apodRepo == null){
            return new APODRepository();
        }
        return apodRepo;
    }

    private APODApi aopdApi;

    private APODRepository(){
        Log.d(TAG,"APODRepository(): New instance created");
        aopdApi = APODRetrofitService.getApiService();
    }

    public MutableLiveData<APODResponse> getAPODMutableLiveData(){
        MutableLiveData<APODResponse> apodLiveData = new MutableLiveData<>();
        aopdApi.getAPOD().enqueue(new Callback<APODResponse>() {
            @Override
            public void onResponse(Call<APODResponse> call, Response<APODResponse> response) {
                if (response.isSuccessful()){
                    Log.d(TAG,"onResponse(): Response success. data="+response.body());
                    apodLiveData.postValue(response.body());
                }

            }

            @Override
            public void onFailure(Call<APODResponse> call, Throwable t) {
                Log.d(TAG,"onFailure(): Response failed.");
                apodLiveData.postValue(null);

            }
        });

        return apodLiveData;
    }

}
