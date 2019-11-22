package sleepless_nights.location_alarm.geofence.use_cases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.Locale;

import sleepless_nights.location_alarm.alarm.use_cases.AlarmRepository;
import sleepless_nights.location_alarm.geofence.CustomGeofence;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            switch (geofencingEvent.getErrorCode()) {
                case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES: {
                    //TODO notify AlarmService
                    break;
                }
                default: {
                    String errorMessage = getErrorString(geofencingEvent.getErrorCode());
                    Log.e(TAG, errorMessage);
                    //TODO handle error
                }
            }
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if (geofenceTransition == CustomGeofence.GEOFENCE_TRANSITION_TYPES) {
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            for (Geofence geofence : triggeringGeofences) {
                String geofenceId = geofence.getRequestId();
                if (geofenceId == null) {
                    Log.wtf(TAG, "Got geofence with null id");
                    continue;
                }
                Integer alarmId = GeofenceRepository.getInstance(context)
                        .getAlarmIdByGeofenceId(geofenceId);
                if (alarmId == null) {
                    continue; //Triggered unregistered geofence
                }
                doAlarm(context, alarmId);
            }
        } else {
            // Log the error.
            Log.e(TAG, String.format(Locale.getDefault(),
                    "Geofence transition has invalid type: %d", geofenceTransition));
        }
    }

    private String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GEOFENCE_NOT_AVAILABLE";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "GEOFENCE_TOO_MANY_GEOFENCES";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            default:
                return "Unknown geofence error";
        }
    }

    private void doAlarm(Context context, int alarmId) {
        //TODO develop
        //TODO send intent to service
        Toast.makeText(context,
                "Triggered alarm " + AlarmRepository.getInstance().getAlarmById(alarmId),
                Toast.LENGTH_SHORT).show();
    }
}
