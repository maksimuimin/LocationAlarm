package sleepless_nights.location_alarm.alarm.use_cases;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import sleepless_nights.location_alarm.alarm.Alarm;

import java.util.ArrayList;
import java.util.List;

public class AlarmRepository {
    private static final String TAG = "AlarmRepository";
    private static AlarmRepository instance = new AlarmRepository();
    private MutableLiveData<AlarmDataSet> dataSetLiveData = new MutableLiveData<>();

    private AlarmRepository() {
        loadDataSet();
    }

    @NonNull
    public static AlarmRepository getInstance() {
        return instance;
    }

    @NonNull
    public LiveData<AlarmDataSet> getDataSetLiveData() {
        return dataSetLiveData;
    }

    @NonNull
    AlarmDataSet getDataSet() {
        if (dataSetLiveData.getValue() == null) {
            Log.wtf(TAG, "AlarmRepository contains dataSetLiveData with null AlarmDataSet");
            loadDataSet();
        }
        return dataSetLiveData.getValue();
    }

    @Nullable
    public LiveData<Alarm> getAlarmLiveDataById(int id) {
        return getDataSet().getAlarmLiveDataById(id);
    }

    @Nullable
    public LiveData<Alarm> getAlarmLiveDataByPosition(int pos) {
        return getDataSet().getAlarmLiveDataByPosition(pos);
    }

    public Alarm newAlarm() {
        AlarmDataSet dataSet = getDataSet();
        Alarm alarm = new Alarm();
        dataSet.addAlarm(alarm);
        dataSetLiveData.postValue(dataSet);
        return alarm;
    }

    public void removeAlarm(Alarm alarm) {
        AlarmDataSet dataSet = getDataSet();
        dataSet.removeAlarm(alarm);
        dataSetLiveData.postValue(dataSet);
    }

    public void updateAlarm(Alarm alarm) {
        AlarmDataSet dataSet = getDataSet();
        dataSet.updateAlarm(alarm);
        // Since we are using array of LiveData in AlarmDataSet we don't need to update
        // whole dataSetLiveData directly, so we will not trigger heavy mechanism with diff utils
    }

    private int getNewAlarmId() {
        //TODO develop
        return 0;
    }

    private void loadDataSet() {
        List<Alarm> alarms = new ArrayList<>();
        dataSetLiveData.postValue(new AlarmDataSet(alarms));
    }
}
