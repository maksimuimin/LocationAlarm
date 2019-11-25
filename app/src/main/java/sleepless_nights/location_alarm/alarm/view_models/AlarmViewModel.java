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

    public AlarmViewModel(@NonNull Application application) {
        super(application);
        final LiveData<AlarmDataSet> repoLiveData =
                AlarmRepository.getInstance(getApplication().getApplicationContext()).getDataSetLiveData();
        liveData.setValue(repoLiveData.getValue());
        liveData.addSource(repoLiveData, alarmDataSet -> {
            Log.d(TAG, "dataSet updated from repository");
            liveData.postValue(alarmDataSet);
        });
    }

    private AlarmRepository getAlarmRepository() {
        return AlarmRepository.getInstance(getApplication().getApplicationContext());
    }

    @NonNull
    public LiveData<AlarmDataSet> getLiveData() { return liveData; }

    @Nullable
    public LiveData<Alarm> getAlarmLiveDataByPosition(int pos) {
        return getAlarmRepository().getAlarmLiveDataByPosition(pos);
    }

    @Nullable
    public LiveData<Alarm> getAlarmLiveDataById(int id) {
        return getAlarmRepository().getAlarmLiveDataById(id);
    }

    public LiveData<Alarm> newAlarm() {
        Alarm alarm = AlarmRepository.getInstance(getApplication().getApplicationContext()).newAlarm();
        return getAlarmRepository().getAlarmLiveDataById(alarm.getId());
    }

    public void removeAlarm(LiveData<Alarm> alarmLiveData) {
        Alarm alarm;
        if (alarmLiveData !=null && (alarm = alarmLiveData.getValue()) != null) {
            getAlarmRepository().removeAlarm(alarm);
        }
    }

    public void updateAlarm(Alarm alarm) {
        getAlarmRepository().updateAlarm(alarm);
    }
}
