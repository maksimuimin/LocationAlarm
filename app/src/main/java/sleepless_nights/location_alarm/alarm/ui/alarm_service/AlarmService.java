package sleepless_nights.location_alarm.alarm.ui.alarm_service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListUpdateCallback;

import java.util.Locale;

import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmRepository;

public class AlarmService extends IntentService {
    private static final String TAG = "AlarmService";
    private static int ID = 0;

    private AlarmDataSet activeAlarmsDataSet;

    public AlarmService() {
        super(String.format(Locale.getDefault(), "%s-%d", TAG, ID));
        ID++;
        Log.d(TAG, "instanceCreated");

        activeAlarmsDataSet = AlarmRepository.getInstance().getActiveAlarmsDataSetLiveData().getValue();
        if (activeAlarmsDataSet == null) {
            Log.wtf(TAG, "activeAlarmDataSetLiveData contains null AlarmDataSet");
            activeAlarmsDataSet = new AlarmDataSet();
        }

        AlarmRepository.getInstance().getActiveAlarmsDataSetLiveData().observeForever(updAlarmDataSet -> {
            updAlarmDataSet.diffFrom(activeAlarmsDataSet).dispatchUpdatesTo(new ListUpdateCallback() {
                @Override
                public void onInserted(int position, int count) {
                    //TODO create geofence
                }

                @Override
                public void onRemoved(int position, int count) {
                    //TODO delete geofence
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {}

                @Override
                public void onChanged(int position, int count, @Nullable Object payload) {
                    //TODO fix notification
                }
            });
            activeAlarmsDataSet = updAlarmDataSet;
            // We can do it in ListUpdateCallback if copy will be too slow

            //TODO subscribe on geofencing API events
        });
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_STICKY;
        //We don't need to restart service if it get killed in the background
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.wtf(TAG, "onHandleIntent: " + (intent == null ? "null" : intent.toString()));
    }
}
