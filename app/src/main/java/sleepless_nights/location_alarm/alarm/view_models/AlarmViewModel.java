package sleepless_nights.location_alarm.alarm.view_models;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmRepository;

public class AlarmViewModel extends AndroidViewModel {
    private static final String TAG = "AlarmViewModel";
    private MediatorLiveData<AlarmDataSet> liveData = new MediatorLiveData<>();
    private AlarmRepository alarmRepository;

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        alarmRepository = AlarmRepository.getInstance(application.getApplicationContext());
        final LiveData<AlarmDataSet> repoLiveData = alarmRepository.getDataSetLiveData();
        liveData.setValue(repoLiveData.getValue());
        liveData.addSource(repoLiveData, alarmDataSet -> {
            Log.d(TAG, "dataSet updated from repository");
            liveData.postValue(alarmDataSet);
        });
    }

    @NonNull
    public LiveData<AlarmDataSet> getLiveData() { return liveData; }

    @Nullable
    public Alarm getAlarmByPosition(int pos) {
        return alarmRepository.getAlarmByPosition(pos);
    }

    @Nullable
    public Alarm getAlarmLiveDataById(long id) {
        return alarmRepository.getAlarmById(id);
    }

    /*
    * TODO #4 make not read-only repository operations not void
    *  we should return some structure which can accept onSuccess and onFailure callbacks
    */

    public void createAlarm(String name, String address, boolean isActive,
                            double latitude, double longitude, float radius) {
        alarmRepository.createAlarm(name, address, isActive,
                latitude, longitude, radius);
    }

    public void deleteAlarm(Alarm alarm) {
        alarmRepository.deleteAlarm(alarm);
    }

    public void updateAlarm(Alarm alarm) {
        alarmRepository.updateAlarm(alarm);
    }
}
