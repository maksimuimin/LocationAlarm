package sleepless_nights.location_alarm.alarm.ui.alarm_service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.ListUpdateCallback;

import java.util.Locale;

import sleepless_nights.location_alarm.BuildConfig;
import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmRepository;
import sleepless_nights.location_alarm.geofence.use_cases.GeofenceRepository;

public class AlarmService extends IntentService {
    public static final String ACTION_DO_ALARM = BuildConfig.APPLICATION_ID + ".do_alarm";
    public static final String ACTION_TOO_MANY_GEOFENCES = BuildConfig.APPLICATION_ID + ".too_many_geofences";
    public static final String INTENT_EXTRA_ALARM_ID = "AlarmID";

    private static final int NOTIFICATION_ID = 1;
    private static final int FOREGROUND_ID = 2;
    private static final String NOTIFICATION_CHANNEL_ID = "Active alarms";

    private static final String TAG = "AlarmService";
    private static int ID = 0;

    private AlarmDataSet activeAlarmsDataSet;
    private NotificationManager notificationManager;
    private boolean runningInForeground = false;
    private boolean started = false;

    public AlarmService() {
        super(String.format(Locale.getDefault(), "%s-%d", TAG, ID));
        ID++;
        Log.d(TAG, "instanceCreated");
        this.setIntentRedelivery(false); //We don't need to restart service if it get killed in background
    }

    @Override
    public void onCreate() {
        super.onCreate();

        activeAlarmsDataSet = AlarmRepository.getInstance(this).getActiveAlarmsDataSetLiveData().getValue();
        if (activeAlarmsDataSet == null) {
            Log.wtf(TAG, "activeAlarmDataSetLiveData contains null AlarmDataSet");
            activeAlarmsDataSet = new AlarmDataSet();
        }

        notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    getString(R.string.main_notification_channel),
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(getString(R.string.main_notification_channel_description));
            channel.enableVibration(false);
            notificationManager.createNotificationChannel(channel);
        }

        AlarmRepository.getInstance(this).getActiveAlarmsDataSetLiveData().observeForever(updAlarmDataSet -> {
            Log.d(TAG, String.format(Locale.getDefault(),
                    "activeAlarmsDataSet updated activeAlarmsDataSet.size(): %d, runningInForeground: %b",
                    activeAlarmsDataSet.size(), runningInForeground));
            if (!started) {
                activeAlarmsDataSet = updAlarmDataSet;
                return;
            }
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
                public void onChanged(int position, int count, @Nullable Object payload) {}
            });

            activeAlarmsDataSet = updAlarmDataSet;// We can do it in ListUpdateCallback if copy will be too slow

            if (!runningInForeground && !activeAlarmsDataSet.isEmpty()) {
                becomeForeground(buildNotification(updAlarmDataSet.size()));
            } else if (runningInForeground && activeAlarmsDataSet.isEmpty()) {
                becomeBackground();
            } else if (runningInForeground) {
                updateNotification();
            }
        });
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            Log.wtf(TAG, "handling null intent");
            return;
        }

        String action = intent.getAction();
        if (action ==null) {
            if (started) {
                Log.wtf(TAG, "handling intent with null action");
            } else {
                Log.d(TAG, "starting the service");
                onStart();
            }
            return;
        }

        switch (action) {
            case ACTION_DO_ALARM: {
                int alarmId = intent.getIntExtra(INTENT_EXTRA_ALARM_ID, -1);
                if (alarmId == -1) {
                    Log.wtf(TAG, "ACTION_DO_ALARM intent does not contain INTENT_EXTRA_ALARM_ID");
                    return;
                }
                handleActionDoAlarm(alarmId);
                break;
            }
            case ACTION_TOO_MANY_GEOFENCES: {
                handleActionTooManyGeofences();
                break;
            }
            default: {
                Log.wtf(TAG, "intent contains unknown action: " + action);
            }
        }
    }

    private void onStart() {
        if (!activeAlarmsDataSet.isEmpty()) {
            becomeForeground(buildNotification(activeAlarmsDataSet.size()));
        }
        started = true;
    }

    private Notification buildNotification(int alarmsCount) {
        //TODO develop
        String content = String.format(Locale.getDefault(), "Active alarms: %d", alarmsCount);
        return new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("LocationAlarm")
                .setContentText(content)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setOngoing(true)
                .build();
    }

    private void becomeForeground(@NonNull Notification notification) {
        Log.d(TAG, "becoming foreground");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(FOREGROUND_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        } else {
            startForeground(FOREGROUND_ID, notification);
        }
        notificationManager.notify(NOTIFICATION_ID, buildNotification(activeAlarmsDataSet.size()));
        runningInForeground = true;
    }

    private void becomeBackground() {
        Log.d(TAG, "becoming background");
        stopForeground(true); //Removing notification
        notificationManager.cancel(NOTIFICATION_ID);
        runningInForeground = false;
    }

    private void updateNotification() {
        Log.d(TAG, "updating notification");
        notificationManager.notify(NOTIFICATION_ID, buildNotification(activeAlarmsDataSet.size()));
    }

    private void handleActionDoAlarm(int alarmId) {
        //TODO start alarming activity
        Alarm triggeredAlarm = AlarmRepository.getInstance(this).getAlarmById(alarmId);
        if (triggeredAlarm == null) {
            Log.wtf(TAG, "Triggered null alarm");
            return;
        }
        Toast.makeText(getApplicationContext(),
                "Triggered alarm " + triggeredAlarm.getName(), Toast.LENGTH_SHORT).show();
        triggeredAlarm.setIsActive(false);
        AlarmRepository.getInstance(this).updateAlarm(triggeredAlarm);
    }

    private void handleActionTooManyGeofences() {
        //TODO develop
        Toast.makeText(getApplicationContext(),
                "Too many geofences", Toast.LENGTH_SHORT).show();
    }
}
