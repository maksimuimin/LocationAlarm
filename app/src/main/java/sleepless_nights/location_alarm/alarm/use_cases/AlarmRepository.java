package sleepless_nights.location_alarm.alarm.use_cases;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import sleepless_nights.location_alarm.alarm.Alarm;

import java.util.ArrayList;

public class AlarmRepository {
    private static final String TAG = "AlarmRepository";
    private static AlarmRepository instance = new AlarmRepository();
    private MutableLiveData<AlarmDataSet> dataSetLiveData = new MutableLiveData<>();

    private AlarmRepository() {
        AlarmDataSet alarms = loadDataSet();
        dataSetLiveData.setValue(alarms);
    }

    @NonNull
    public static AlarmRepository getInstance() { return instance; }

    @NonNull
    public LiveData<AlarmDataSet> getDataSetLiveData() { return dataSetLiveData; }

    public void newAlarm(String name, String address, boolean isActive) {
        Alarm alarm = new Alarm(getNewAlarmId(), name, address, isActive);
        AlarmDataSet dataSet = dataSetLiveData.getValue();
        if (dataSet == null) {
            dataSet = new AlarmDataSet();
        }
        dataSet.addAlarm(alarm);
        dataSetLiveData.postValue(dataSet);
    }

    public void deleteAlarm(int id) {
        AlarmDataSet dataSet = dataSetLiveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "dataSetLiveData contains LiveData with null DataSet");
            return;
        }

        dataSet.removeAlarm(id);
        dataSetLiveData.postValue(dataSet);
    }

    public void changeAlarm(int id, @Nullable String name,
                            @Nullable String address, @Nullable Boolean isActive) {
        AlarmDataSet dataSet = dataSetLiveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "dataSetLiveData contains LiveData with null DataSet");
            return;
        }

        dataSet.changeAlarm(id, name, address, isActive);
        // Since we are using array of LiveData in AlarmDataSet we don't need to update
        // whole dataSetLiveData directly, so we will not trigger heavy mechanism with diff utils
    }

    private int getNewAlarmId() {
        //TODO develop
        return 0;
    }

    private AlarmDataSet loadDataSet() {
        //TODO load data from DB
        ArrayList<Alarm> alarms = new ArrayList<>();
        return new AlarmDataSet(alarms);
    }
}
