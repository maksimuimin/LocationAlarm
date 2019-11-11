package com.example.locationalarm.alarm.usecases;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.locationalarm.alarm.Alarm;

import java.util.ArrayList;

public class AlarmRepository {
    private static AlarmRepository instance = new AlarmRepository();
    private AlarmRepository() { }

    @NonNull
    public static AlarmRepository getInstance() {
        return instance;
    }

    private MutableLiveData<AlarmDataSet> dataSet;

    @NonNull
    public LiveData<AlarmDataSet> getAlarms() {
        if (dataSet == null) {
            dataSet = new MutableLiveData<>();
            dataSet.setValue(loadDataSet());
        }
        return dataSet;
    }

    private AlarmDataSet loadDataSet() {
        ArrayList<Alarm> alarmList = new ArrayList<>();
        //TODO remove
        alarmList.add(new Alarm("MyName", "MyAddress"));
        alarmList.add(new Alarm("MyName", "MyAddress", false));
        return new AlarmDataSet(alarmList);
    }
}
