package com.example.locationalarm.alarm.view_models.alarm_view_model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.locationalarm.alarm.use_cases.AlarmDataSet;
import com.example.locationalarm.alarm.use_cases.AlarmRepository;

public class AlarmViewModel extends AndroidViewModel {
    private static final String TAG = "AlarmViewModel";
    private MediatorLiveData<AlarmDataSet> dataSet = new MediatorLiveData<>();

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        final LiveData<AlarmDataSet> repoLiveData = AlarmRepository.getInstance().getAlarms();
        dataSet.setValue(repoLiveData.getValue());
        dataSet.addSource(repoLiveData, alarmDataSet -> {
            Log.d(TAG, "dataSet updated from repository");
            dataSet.postValue(alarmDataSet);
        });
    }

    public LiveData<AlarmDataSet> getData() {
        return dataSet;
    }
}
