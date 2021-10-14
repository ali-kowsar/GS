package com.kowsar.gs.apod.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kowsar.gs.apod.model.data.APODRepository;
import com.kowsar.gs.apod.model.response.APODResponse;

public class APODViewModel extends ViewModel {
    private final String TAG= this.getClass().getSimpleName();
    private MutableLiveData<APODResponse> mutableLiveData;
    private APODRepository apodRepository;

    public void init(){
        if (mutableLiveData != null){
            return;
        }
        apodRepository = APODRepository.getInstance();
        mutableLiveData = apodRepository.getAPODMutableLiveData();

    }

    public LiveData<APODResponse> getNewsRepository() {
        return mutableLiveData;
    }
}
