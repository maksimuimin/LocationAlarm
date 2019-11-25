package sleepless_nights.location_alarm.alarm.use_cases;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import sleepless_nights.location_alarm.alarm.Alarm;

public class AlarmRepository {
    private static final String TAG = "AlarmRepository";
    private static int ID_SOURCE = 0;
    private static AlarmRepository instance = new AlarmRepository();
    private MutableLiveData<AlarmDataSet> dataSetLiveData = new MutableLiveData<>();
    private MutableLiveData<AlarmDataSet> activeAlarmsDataSetLiveData = new MutableLiveData<>();

    private AlarmRepository() {
        dataSetLiveData.setValue(new AlarmDataSet());
        activeAlarmsDataSetLiveData.setValue(new AlarmDataSet());
        loadDataSet();
    }

    @NonNull
    public static AlarmRepository getInstance() { return instance; }

    @NonNull
    public LiveData<AlarmDataSet> getDataSetLiveData() { return dataSetLiveData; }

    @NonNull
    public LiveData<AlarmDataSet> getActiveAlarmsDataSetLiveData() { return activeAlarmsDataSetLiveData; }

    @Nullable
    public Alarm getAlarmById(int id) {
        AlarmDataSet dataSet = dataSetLiveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "AlarmRepository contains dataSetLiveData with null AlarmDataSet");
            return null;
        }
        return dataSet.getAlarmById(id);
    }

    @Nullable
    public Alarm getAlarmByPosition(int pos) {
        AlarmDataSet dataSet = dataSetLiveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "AlarmRepository contains dataSetLiveData with null AlarmDataSet");
            return null;
        }
        return dataSet.getAlarmByPosition(pos);
    }

    public void createAlarm(String name, String address, boolean isActive,
                            double latitude, double longitude, float radius) {
        Alarm alarm = new Alarm(getNewAlarmId(), name, address, isActive,
                latitude, longitude, radius);

        AlarmDataSet dataSet = dataSetLiveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "dataSetLiveData contains null AlarmDataSet");
            dataSet = new AlarmDataSet();
        }
        dataSet.createAlarm(alarm);
        dataSetLiveData.postValue(dataSet);
        if (!alarm.getIsActive()) return;

        AlarmDataSet activeAlarmsDataSet = activeAlarmsDataSetLiveData.getValue();
        if (activeAlarmsDataSet == null) {
            Log.wtf(TAG, "activeAlarmsDataSetLiveData contains null AlarmDataSet");
            activeAlarmsDataSet = new AlarmDataSet();
        }
        activeAlarmsDataSet.createAlarm(alarm);
        activeAlarmsDataSetLiveData.postValue(activeAlarmsDataSet);
    }

    public void deleteAlarm(int id) {
        AlarmDataSet dataSet = dataSetLiveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "dataSetLiveData contains LiveData with null DataSet");
            return;
        }
        dataSet.deleteAlarm(id);
        dataSetLiveData.postValue(dataSet);

        AlarmDataSet activeAlarmDataSet = activeAlarmsDataSetLiveData.getValue();
        if (activeAlarmDataSet == null) {
            Log.wtf(TAG, "activeAlarmsDataSetLiveData contains LiveData with null DataSet");
            return;
        }
        activeAlarmDataSet.deleteAlarm(id);
        activeAlarmsDataSetLiveData.postValue(activeAlarmDataSet);
    }

    public void updateAlarm(Alarm alarm) {
        AlarmDataSet dataSet = dataSetLiveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "dataSetLiveData contains LiveData with null DataSet");
            return;
        }
        dataSet.updateAlarm(alarm);
        dataSetLiveData.postValue(dataSet);

        AlarmDataSet activeAlarmDataSet = activeAlarmsDataSetLiveData.getValue();
        if (activeAlarmDataSet == null) {
            Log.wtf(TAG, "activeAlarmsDataSetLiveData contains LiveData with null DataSet");
            return;
        }
        if (alarm.getIsActive()) {
            activeAlarmDataSet.createAlarm(alarm);
        } else {
            activeAlarmDataSet.deleteAlarm(alarm.getId());
        }
        activeAlarmsDataSetLiveData.postValue(activeAlarmDataSet);
    }

    private int getNewAlarmId() {
        //TODO develop
        ID_SOURCE++;
        return ID_SOURCE;
    }

    private void loadDataSet() {
        //TODO load data from DB
        ArrayList<Alarm> alarms = new ArrayList<>();
        dataSetLiveData.postValue(new AlarmDataSet(alarms));
    }
}
