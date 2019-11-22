package sleepless_nights.location_alarm.alarm.ui.alarm_service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListUpdateCallback;

import java.util.Locale;

import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmRepository;
import sleepless_nights.location_alarm.geofence.use_cases.GeofenceRepository;

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
                    for (int i = position; i < count; i++) {
                        Alarm alarm = updAlarmDataSet.getAlarmByPosition(i);
                        if (alarm == null) {
                            Log.wtf(TAG, "updAlarmDataSet doesn't contain inserted alarm");
                            continue;
                        }
                        GeofenceRepository.getInstance(getApplicationContext())
                                .createGeofence(alarm);
                    }
                }

                @Override
                public void onRemoved(int position, int count) {
                    for (int i = position; i < count; i++) {
                        Alarm alarm = activeAlarmsDataSet.getAlarmByPosition(position);
                        if (alarm == null) {
                            Log.wtf(TAG, "activeAlarmsDataSet doesn't contain inserted alarm");
                            continue;
                        }
                        GeofenceRepository.getInstance(getApplicationContext())
                                .deleteGeofence(alarm);
                    }
                }

                @Override
                public void onMoved(int fromPosition, int toPosition) {}

                @Override
                public void onChanged(int position, int count, @Nullable Object payload) {
                    //TODO fix notification
                }
            });

            if (activeAlarmsDataSet.isEmpty() && !updAlarmDataSet.isEmpty()) {
                //TODO start foreground
            }
            if (!activeAlarmsDataSet.isEmpty() && updAlarmDataSet.isEmpty()) {
                //TODO stop foreground
            }
            activeAlarmsDataSet = updAlarmDataSet;
            // We can do it in ListUpdateCallback if copy will be too slow
        });
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_STICKY;
        //We don't need to restart service if it get killed in the background
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //TODO handle intents from GeofenceBroadcastReceiver
        Log.wtf(TAG, "onHandleIntent: " + (intent == null ? "null" : intent.toString()));
    }
}
