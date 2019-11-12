package com.example.locationalarm.alarm.use_cases;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.locationalarm.alarm.Alarm;

import java.util.ArrayList;

public class AlarmRepository {
    private static AlarmRepository instance = new AlarmRepository();
    private MutableLiveData<AlarmDataSet> dataSet = new MutableLiveData<>();

    private AlarmRepository() {
        dataSet.setValue(loadDataSet());
    }

    @NonNull
    public static AlarmRepository getInstance() {
        return instance;
    }

    @NonNull
    public LiveData<AlarmDataSet> getAlarms() {
        return dataSet;
    }

    public void registerAlarm(String name, String address, boolean isActive) {
        Alarm alarm = new Alarm(name, address, isActive);
        AlarmDataSet dataSetValue = dataSet.getValue();
        if (dataSetValue == null) {
            dataSetValue = loadDataSet();
        }
        dataSetValue.insertAlarm(alarm);
        dataSet.setValue(dataSetValue);
    }

    public void changeAlarmQuietly(int idx, @Nullable String newName,
                                   @Nullable String newAddress, @Nullable Boolean newIsActive) {
        AlarmDataSet dataSetValue = dataSet.getValue();
        if (dataSetValue == null) return;
        dataSetValue.changeAlarmQuietly(idx, newName, newAddress, newIsActive);
    }

    @NonNull
    private AlarmDataSet loadDataSet() {
        ArrayList<Alarm> alarmList = new ArrayList<>();
        //TODO load data from DB
        return new AlarmDataSet(alarmList);
    }
}
