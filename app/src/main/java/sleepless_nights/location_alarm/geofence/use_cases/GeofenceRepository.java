package sleepless_nights.location_alarm.geofence.use_cases;

import android.content.Context;
import android.util.Log;
import android.util.LongSparseArray;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import sleepless_nights.location_alarm.LocationAlarmApplication;
import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmRepository;
import sleepless_nights.location_alarm.geofence.CustomGeofence;

public class GeofenceRepository {
    private static final String TAG = "GeofenceRepository";
    private HashMap<String, CustomGeofence> geofenceMap;
    private LongSparseArray<String> alarmIdToGeofenceIdMap;
    private Context context;
    private GeofencingClient geofencingClient;
    private Queue<CustomGeofence> zombieGeofenceQueue = new ArrayDeque<>();

    public GeofenceRepository(Context context) {
        geofenceMap = new HashMap<>();
        alarmIdToGeofenceIdMap = new LongSparseArray<>();
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            if (zombieGeofenceQueue.isEmpty()) return;
            Log.w(TAG, String.format(Locale.getDefault(), "zombieGeofenceQueue size: %d",
                    zombieGeofenceQueue.size()));
            for (int i = 0; i < zombieGeofenceQueue.size(); i++) {
                CustomGeofence geofence = zombieGeofenceQueue.poll();
                if (geofence == null) {
                    Log.wtf(TAG, "got null zombie geofence");
                    continue;
                }
                stopMonitoring(geofence);
            }
        }, 10, TimeUnit.SECONDS);
    }

    @NonNull
    public static GeofenceRepository getInstance(Context context) {
        return LocationAlarmApplication.from(context).getGeofenceRepository();
    }

    @Nullable
    Long getAlarmIdByGeofenceId(String geofenceId) {
        CustomGeofence geofence = geofenceMap.get(geofenceId);
        if (geofence == null) return null;
        return geofence.getAlarmId();
    }

    public void createGeofence(@NonNull Alarm alarm) {
        CustomGeofence geofence = new CustomGeofence(context, alarm);
        geofenceMap.put(geofence.getGeofenceId(), geofence);
        alarmIdToGeofenceIdMap.put(alarm.getId(), geofence.getGeofenceId());
        startMonitoring(geofence);
    }

    public void deleteGeofence(@NonNull Alarm alarm) {
        String geofenceId = alarmIdToGeofenceIdMap.get(alarm.getId(), null);
        if (geofenceId == null) {
            Log.wtf(TAG, "Trying to delete geofence by unregistered Alarm.Id");
            return;
        }
        alarmIdToGeofenceIdMap.remove(alarm.getId());
        CustomGeofence geofence = geofenceMap.get(geofenceId);
        if (geofence == null) {
            Log.wtf(TAG, "Trying to delete not existing geofence");
            return;
        }
        geofenceMap.remove(geofenceId);
        stopMonitoring(geofence);
    }

    private void startMonitoring(@NonNull CustomGeofence geofence) {
        final GeofencingRequest req = geofence.getGeofencingRequest();
        geofencingClient.addGeofences(req, geofence.getGeofencePendingIntent())
                .addOnFailureListener(e -> {
                    String logMsg = String.format(Locale.getDefault(),
                            "Got error [%s] on startMonitoring geofence with id=%s, alarmId=%d",
                            e.getMessage(),
                            geofence.getGeofenceId(),
                            geofence.getAlarmId());
                    Log.e(TAG,logMsg);
                    Alarm alarm = AlarmRepository.getInstance(context).getAlarmById(geofence.getAlarmId());
                    if (alarm == null) {
                        Log.wtf(TAG, "Alarm not found in repository");
                        alarmIdToGeofenceIdMap.remove(geofence.getAlarmId());
                        geofenceMap.remove(geofence.getGeofenceId());
                        return;
                    }
                    alarm.setIsActive(false);
                    AlarmRepository.getInstance(context).updateAlarm(alarm);
                    Toast.makeText(context,
                            context.getString(R.string.start_geofence_monitoring_error_msg)
                                    + " " + alarm.getName(), Toast.LENGTH_LONG).show(); // TODO - normal error message
                });
    }

    private void stopMonitoring(@NonNull CustomGeofence geofence) {
        geofencingClient.removeGeofences(geofence.getGeofencePendingIntent())
                .addOnFailureListener(e -> {
                    String logMsg = String.format(Locale.getDefault(),
                            "Got error [%s] on stopMonitoring geofence with id=%s, alarmId=%d",
                            e.getMessage(),
                            geofence.getGeofenceId(),
                            geofence.getAlarmId());
                    Log.e(TAG,logMsg);
                    zombieGeofenceQueue.add(geofence);
                });
    }
}
