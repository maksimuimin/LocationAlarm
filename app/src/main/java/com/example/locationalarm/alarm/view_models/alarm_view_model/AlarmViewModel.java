package com.example.locationalarm.alarm.view_models.alarm_view_model;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.locationalarm.alarm.Alarm;
import com.example.locationalarm.alarm.use_cases.AlarmDataSet;
import com.example.locationalarm.alarm.use_cases.AlarmRepository;

public class AlarmViewModel extends AndroidViewModel {
    private static final String TAG = "AlarmViewModel";
    private MediatorLiveData<AlarmDataSet> liveData = new MediatorLiveData<>();

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        final LiveData<AlarmDataSet> repoLiveData = AlarmRepository.getInstance().getDataSetLiveData();
        liveData.setValue(repoLiveData.getValue());
        liveData.addSource(repoLiveData, alarmDataSet -> {
            Log.d(TAG, "dataSet updated from repository");
            liveData.postValue(alarmDataSet);
        });
    }

    @NonNull
    public LiveData<AlarmDataSet> getLiveData() { return liveData; }

    @Nullable
    public LiveData<Alarm> getAlarmLiveDataByPosition(int pos) {
        AlarmDataSet dataSet = liveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "liveData contains null DataSet");
            return null;
        }

        return dataSet.getAlarmLiveDataByPosition(pos);
    }

    @Nullable
    public LiveData<Alarm> getAlarmLiveDataById(int id) {
        AlarmDataSet dataSet = liveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "liveData contains null DataSet");
            return null;
        }

        return dataSet.getAlarmLiveDataById(id);
    }

    public void addAlarm(String name, String address, boolean isActive) {
        AlarmRepository.getInstance().newAlarm(name, address, isActive);
    }

    public void removeAlarm(int id) {
        AlarmRepository.getInstance().deleteAlarm(id);
    }

    public void changeAlarm(int id, @Nullable String name,
                            @Nullable String address, @Nullable Boolean isActive) {
        AlarmRepository.getInstance().changeAlarm(id, name, address, isActive);
    }
}
