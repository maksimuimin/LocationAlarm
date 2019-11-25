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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AlarmRepository {
    private static final String TAG = "AlarmRepository";
    private static AlarmRepository instance;

    private MutableLiveData<AlarmDataSet> dataSetLiveData;
    private AlarmDao alarmDao;

    private final Executor executor = Executors.newSingleThreadExecutor();

    private AlarmRepository(Context applicationContext) {
        dataSetLiveData = new MutableLiveData<>(new AlarmDataSet(new ArrayList<>()));
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
        Alarm alarm = new Alarm();

        executor.execute(() -> alarmDao.add(AlarmEntityAdapter.adapt(alarm)));

        AlarmDataSet dataSet = getDataSet();
        dataSet.addAlarm(alarm);
        dataSetLiveData.postValue(dataSet);

        return alarm;
    }

    public void removeAlarm(Alarm alarm) {
        executor.execute(() -> alarmDao.remove(AlarmEntityAdapter.adapt(alarm)));

        AlarmDataSet dataSet = getDataSet();
        dataSet.removeAlarm(alarm);
        dataSetLiveData.postValue(dataSet);
    }

    public void updateAlarm(Alarm alarm) {
        executor.execute(() -> alarmDao.update(AlarmEntityAdapter.adapt(alarm)));

        AlarmDataSet dataSet = getDataSet();
        dataSet.updateAlarm(alarm);
        // Since we are using array of LiveData in AlarmDataSet we don't need to update
        // whole dataSetLiveData directly, so we will not trigger heavy mechanism with diff utils
    }

    private void loadDataSet() {
        executor.execute(() -> {
            List<Alarm> alarms = AlarmEntityAdapter.adaptAlarmEntities(alarmDao.getAll());
            dataSetLiveData.postValue(new AlarmDataSet(alarms));
        });
    }


    /**
     * todo delete this later
     * */
    @Deprecated
    private void loadTestData() {
        executor.execute(() -> {
            if (alarmDao.getAll().isEmpty()) {

                Alarm alarm1 = new Alarm()
                        .setName("Moscow").setAddress("0th kilometer")
                        .setLatitude(55.751244).setLongitude(37.618423)
                        .setRadius(2000)
                        .setActive(true);
                Alarm alarm2 = new Alarm()
                        .setName("Smolensk").setAddress("Smolensk center")
                        .setLatitude(54.7818).setLongitude(32.0401)
                        .setRadius(2000)
                        .setActive(true);
                Alarm alarm3 = new Alarm()
                        .setName("Vladimir").setAddress("Somewhere in Vladimir")
                        .setLatitude(56.143063).setLongitude(40.410934)
                        .setRadius(2000)
                        .setActive(true);
                Alarm alarm4 = new Alarm()
                        .setName("Vladivostok").setAddress("Kilometers away, but not many")
                        .setLatitude(43.10562).setLongitude(131.87353)
                        .setRadius(2000)
                        .setActive(false);
                Alarm alarm5 = new Alarm()
                        .setName("Los Angeles").setAddress("LA CA USA")
                        .setLatitude(34.052235).setLongitude(-118.243683)
                        .setRadius(2000)
                        .setActive(false);

                alarmDao.add(AlarmEntityAdapter.adapt(alarm1));
                alarmDao.add(AlarmEntityAdapter.adapt(alarm2));
                alarmDao.add(AlarmEntityAdapter.adapt(alarm3));
                alarmDao.add(AlarmEntityAdapter.adapt(alarm4));
                alarmDao.add(AlarmEntityAdapter.adapt(alarm5));
            }
        });
    }

}
