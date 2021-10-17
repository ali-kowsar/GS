package com.kowsar.gs.apod.model.data;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.kowsar.gs.apod.model.db.FavouriteDB;
import com.kowsar.gs.apod.model.response.APODResponse;
import com.kowsar.gs.apod.network.APODApi;
import com.kowsar.gs.apod.network.APODRetrofitService;
import com.kowsar.gs.apod.view.APODItem;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class APODRepository {
    private final String TAG= this.getClass().getSimpleName();
    private static APODRepository apodRepo;
    private MutableLiveData<APODResponse> apodLiveData;
    private MutableLiveData<ResponseBody> apodErrorLiveData;
    private FavouriteDB db;
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
        apodLiveData = new MutableLiveData<>();
//        db= new FavouriteDB(mContext);
    }

    public void fetchNASAAPOD(String date){
        if (date == null) {
            aopdApi.getAPOD().enqueue(new Callback<APODResponse>() {
                @Override
                public void onResponse(Call<APODResponse> call, Response<APODResponse> response) {
                    Log.d(TAG, "onResponse(): getAPOD-> Response success. data=" + response.body());
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse(): getAPOD-> Response success. data=" + response.body());
                        apodLiveData.postValue(response.body());
                    }else {
                        Log.d(TAG, "onResponse():getAPOD-> Response success. data=" + response.message());
                    }
                }

                @Override
                public void onFailure(Call<APODResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure(): getAPOD-> Response failed.");
                    apodLiveData.postValue(null);
                }
            });
        }else {
            aopdApi.getAPODByDate(date).enqueue(new Callback<APODResponse>() {
                @Override
                public void onResponse(Call<APODResponse> call, Response<APODResponse> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse():getAPODByDate-> Response success. data=" + response.body());
                        apodLiveData.postValue(response.body());
                    }else {
                        Log.d(TAG, "onResponse():getAPODByDate-> Response success. data=" + response.message());
//                        apodErrorLiveData.postValue(response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<APODResponse> call, Throwable t) {
                    Log.d(TAG, "onFailure(): getAPODByDate->Response failed.");
                    apodLiveData.postValue(null);
                }
            });
        }

    }

    public MutableLiveData<APODResponse> getAPODMutableLiveData(){
        return apodLiveData;
    }

    public void insertToDB(APODItem fabItem) {
        Log.d(TAG, "insertToDB(): INSERT to DB");
    }

    public void removeFabItem(String date) {
        Log.d(TAG, "removeFabItem(): Remove from DB");
    }
}
