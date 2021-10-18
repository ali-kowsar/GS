package com.kowsar.gs.apod.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kowsar.gs.apod.model.data.APODRepository;
import com.kowsar.gs.apod.model.data.LastLoadedItem;
import com.kowsar.gs.apod.model.db.FavouriteDB;
import com.kowsar.gs.apod.model.response.APODResponse;
import com.kowsar.gs.apod.view.APODItem;

public class APODViewModel extends ViewModel {
    private final String TAG= this.getClass().getSimpleName();
    private MutableLiveData<APODResponse> mutableLiveData;
    private APODRepository apodRepository;
    private MutableLiveData<String> errorMessage;
    private FavouriteDB apodDB;

    public void init(FavouriteDB db){
        Log.d(TAG, "init(): Enter");
        if (mutableLiveData != null){
            return;
        }
        apodDB = db;
        apodRepository = APODRepository.getInstance(db);
        mutableLiveData = apodRepository.getAPODMutableLiveData();
    }

    public void getAPODByDate(String date){
        apodRepository.fetchNASAAPOD(date);
    }

    public LiveData<APODResponse> getAPODLiveData() {
        return mutableLiveData;
    }

    public void addToFabDB(APODResponse currentItem) {
        Log.d(TAG, "addToFabDB():currentItem="+currentItem);
        APODItem fabItem= new APODItem(currentItem.getDate(),currentItem.getTitle(), currentItem.getThumbURL());
        apodRepository.insertToDB(fabItem);
    }

    public void removeFromFabDB(String date) {
        Log.d(TAG, "addToFabDB():date="+date);
        apodRepository.removeFabItem(date);
    }

    public void addToFabDB(String date, String title, String thumbURL) {
        APODItem fabItem= new APODItem(date,title, thumbURL);
        apodRepository.insertToDB(fabItem);
    }

    public LastLoadedItem getLastItemInfo(){
        return apodRepository.fetchLastDataFromDB();
    }

    public void insertLastDate(String title, String date, String url, String explanation, byte[] imageData) {
        apodRepository.insertLastInfoData(title,date,url,explanation,imageData);
    }
}
