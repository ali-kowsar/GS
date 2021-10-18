package com.kowsar.gs.apod.model.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    private static FavouriteDB apodDb;
    public static APODRepository getInstance(FavouriteDB db){
        if(apodRepo == null){
            return new APODRepository(db);
        }
        return apodRepo;
    }

    private APODApi aopdApi;

    private APODRepository(FavouriteDB db){
        Log.d(TAG,"APODRepository(): New instance created");
        aopdApi = APODRetrofitService.getApiService();
        apodLiveData = new MutableLiveData<>();
        apodDb = db;
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
        SQLiteDatabase favDB = apodDb.getReadableDatabase();
        apodDb.insertFABToDB(fabItem.getId(), fabItem.getTitle(), fabItem.getThumbURL());
    }

    public LastLoadedItem fetchLastDataFromDB() {
        Log.d(TAG, "fetchLastDataFromDB(): Enter");
        Cursor cursor = apodDb.fetchLastData();
        try {
            while (cursor.moveToNext()) {
                String title = cursor.getString(cursor.getColumnIndex(FavouriteDB.KEY_TITLE));
                String date = cursor.getString(cursor.getColumnIndex(FavouriteDB.KEY_DATE));
                String url= cursor.getString(cursor.getColumnIndex(FavouriteDB.KEY_URL));
                String desc = cursor.getString(cursor.getColumnIndex(FavouriteDB.KEY_DESCRIPTION));
                byte[] imgaByte = cursor.getBlob(cursor.getColumnIndex(FavouriteDB.KEY_IMAGE));

                Log.d(TAG, "title=" + title + ",id=" + date+", url="+url);
                return new LastLoadedItem(date,title,url,desc,imgaByte);

            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    public void removeFabItem(String date) {
        Log.d(TAG, "removeFabItem(): Remove from DB");
        apodDb.removeFromFab(date);

    }

    public void insertLastInfoData(String title, String date, String url, String explanation, byte[] imageData) {
        apodDb.insertItem(title, date, url, explanation, imageData);
    }
}
