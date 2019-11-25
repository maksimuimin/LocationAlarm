package sleepless_nights.location_alarm.alarm.use_cases;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;


import androidx.room.Room;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.use_cases.db.AlarmDao;
import sleepless_nights.location_alarm.alarm.use_cases.db.AlarmDb;
import sleepless_nights.location_alarm.alarm.use_cases.db.AlarmEntityAdapter;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AlarmRepository {
    private static final String TAG = "AlarmRepository";
    private static int ID_SOURCE = 0;
    private static AlarmRepository instance = new AlarmRepository();
    private MutableLiveData<AlarmDataSet> dataSetLiveData = new MutableLiveData<>();
    private MutableLiveData<AlarmDataSet> activeAlarmsDataSetLiveData = new MutableLiveData<>();
    
    private AlarmDao alarmDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    private AlarmRepository(Context applicationContext) {
        dataSetLiveData.setValue(new AlarmDataSet());
        activeAlarmsDataSetLiveData.setValue(new AlarmDataSet());
        loadDataSet();
        alarmDao = Room
                .databaseBuilder(applicationContext, AlarmDb.class, "alarm-database")
                .build()
                .alarmDao();
    }

    @NonNull
    public static AlarmRepository getInstance(Context applicationContext) {
        if (instance == null) {
            instance = new AlarmRepository(applicationContext);
            instance.loadTestData();
            instance.loadDataSet();
        }
        return instance;
    }

    @NonNull
    public LiveData<AlarmDataSet> getDataSetLiveData() {
        return dataSetLiveData;
    }

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
        Log.d(TAG, "creating new alarm");
        Alarm alarm = new Alarm(getNewAlarmId(), name, address, isActive,
                latitude, longitude, radius);
  
        executor.execute(() -> alarmDao.add(AlarmEntityAdapter.adapt(alarm)));
  
        AlarmDataSet dataSet = dataSetLiveData.getValue();
        if (dataSet == null) {
            Log.wtf(TAG, "dataSetLiveData contains null AlarmDataSet");
            dataSet = new AlarmDataSet();
        }
        dataSet.createAlarm(alarm);
        dataSetLiveData.postValue(dataSet);
        if (!alarm.getIsActive()) {
            Log.d(TAG, String.format(Locale.getDefault(),
                    "dataSet size: %d, activeAlarmsDataSet size: %d",
                    dataSetLiveData.getValue().size(),
                    Objects.requireNonNull(activeAlarmsDataSetLiveData.getValue()).size()));
            return;
        }

        AlarmDataSet activeAlarmsDataSet = activeAlarmsDataSetLiveData.getValue();
        if (activeAlarmsDataSet == null) {
            Log.wtf(TAG, "activeAlarmsDataSetLiveData contains null AlarmDataSet");
            activeAlarmsDataSet = new AlarmDataSet();
        }
        activeAlarmsDataSet.createAlarm(alarm);
        activeAlarmsDataSetLiveData.postValue(activeAlarmsDataSet);
        Log.d(TAG, String.format(Locale.getDefault(),
                "dataSet size: %d, activeAlarmsDataSet size: %d",
                dataSetLiveData.getValue().size(), activeAlarmsDataSetLiveData.getValue().size()));
    }

    public void deleteAlarm(int id) {
        executor.execute(() -> alarmDao.remove(AlarmEntityAdapter.adapt(alarm)));
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
        executor.execute(() -> alarmDao.update(AlarmEntityAdapter.adapt(alarm)));
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
        executor.execute(() -> {
            List<Alarm> alarms = AlarmEntityAdapter.adaptAlarmEntities(alarmDao.getAll());
            dataSetLiveData.postValue(new AlarmDataSet(alarms));
        });
    }

}
