package com.kowsar.gs.apod.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kowsar.gs.apod.model.data.APODRepository;
import com.kowsar.gs.apod.model.response.APODResponse;
import com.kowsar.gs.apod.view.APODItem;

public class APODViewModel extends ViewModel {
    private final String TAG= this.getClass().getSimpleName();
    private MutableLiveData<APODResponse> mutableLiveData;
    private APODRepository apodRepository;
    private MutableLiveData<String> errorMessage;

    public void init(){
        Log.d(TAG, "init(): Enter");
        if (mutableLiveData != null){
            return;
        }
        apodRepository = APODRepository.getInstance();
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

    public void removeFromFabDB(APODResponse currentItem) {
        Log.d(TAG, "addToFabDB():currentItem="+currentItem);
        APODItem fabItem= new APODItem(currentItem.getDate(),currentItem.getTitle(), currentItem.getThumbURL());
        apodRepository.removeFabItem(currentItem.getDate());
    }
}
