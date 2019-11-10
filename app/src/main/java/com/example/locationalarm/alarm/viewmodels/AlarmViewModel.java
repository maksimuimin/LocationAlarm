package com.example.locationalarm.alarm.viewmodels;

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
    private MutableLiveData<ArrayList<Alarm>> alarms = new MutableLiveData<>();

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        alarms.setValue(loadAlarms());
    }

    private @NonNull ArrayList<Alarm> loadAlarms() {
        Log.i(TAG, "on loadAlarms()");
        ArrayList<Alarm> alarmList = new ArrayList<>();
        alarmList.add(new Alarm("MyName", "MyAddress"));
        alarmList.add(new Alarm("MyName", "MyAddress", false));
        //TODO get data from repository
        return alarmList;
    }

    public LiveData<ArrayList<Alarm>> getData() {
        return alarms;
    }
}
