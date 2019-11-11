package com.example.locationalarm.alarm.viewmodels.alarm_view_model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.locationalarm.alarm.Alarm;

import java.util.ArrayList;

public class AlarmViewModel extends AndroidViewModel {
    private static final String TAG = "AlarmViewModel";
    private MutableLiveData<AlarmDataSet> dataSet = new MutableLiveData<>();

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        dataSet.setValue(loadAlarms());
    }

    private AlarmDataSet loadAlarms() {
        Log.i(TAG, "on loadAlarms()");
        ArrayList<Alarm> alarmList = new ArrayList<>();
        //TODO remove
        alarmList.add(new Alarm("MyName", "MyAddress"));
        alarmList.add(new Alarm("MyName", "MyAddress", false));
        //TODO get data from repository
        return new AlarmDataSet(alarmList);
    }

    public LiveData<AlarmDataSet> getData() {
        return dataSet;
    }
}
