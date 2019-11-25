package sleepless_nights.location_alarm.geofence.use_cases;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Locale;

import sleepless_nights.LocationAlarmApplication;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.geofence.CustomGeofence;

public class GeofenceRepository {
    private static final String TAG = "GeofenceRepository";
    private HashMap<String, CustomGeofence> geofenceMap;
    private SparseArray<String> alarmIdToGeofenceIdMap;
    private Context context;
    private GeofencingClient geofencingClient;

    public GeofenceRepository(Context context) {
        geofenceMap = new HashMap<>();
        alarmIdToGeofenceIdMap = new SparseArray<>();
        this.context = context;
        geofencingClient = LocationServices.getGeofencingClient(context);
    }

    @NonNull
    public static GeofenceRepository getInstance(Context context) {
        return LocationAlarmApplication.from(context).getGeofenceRepository();
    }

    @Nullable
    Integer getAlarmIdByGeofenceId(String geofenceId) {
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
        stopMonitoring(geofence);
        geofenceMap.remove(geofenceId);
    }

    private void startMonitoring(@NonNull CustomGeofence geofence) {
        final GeofencingRequest req = geofence.getGeofencingRequest();
        geofencingClient.addGeofences(req, geofence.getGeofencePendingIntent())
                .addOnFailureListener(e -> {
                    String logMsg = String.format(Locale.getDefault(),
                            "Got error on startMonitoring geofence with id=%s, alarmId=%d",
                            geofence.getGeofenceId(),
                            geofence.getAlarmId());
                    Log.e(TAG,logMsg);
                    //TODO handle the error
                });
    }

    private void stopMonitoring(@NonNull CustomGeofence geofence) {
        geofencingClient.removeGeofences(geofence.getGeofencePendingIntent())
                .addOnFailureListener(e -> {
                    String logMsg = String.format(Locale.getDefault(),
                            "Got error on stopMonitoring geofence with id=%s, alarmId=%d",
                            geofence.getGeofenceId(),
                            geofence.getAlarmId());
                    Log.e(TAG,logMsg);
                    //TODO handle the error
                });
    }
}
