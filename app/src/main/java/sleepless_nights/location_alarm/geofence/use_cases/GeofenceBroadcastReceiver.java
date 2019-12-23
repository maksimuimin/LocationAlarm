package sleepless_nights.location_alarm.geofence.use_cases;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;
import java.util.Locale;

import sleepless_nights.location_alarm.alarm.ui.alarm_service.AlarmService;
import sleepless_nights.location_alarm.geofence.CustomGeofence;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "got error onReceive: " +
                    GeofenceStatusCodes.getStatusCodeString(geofencingEvent.getErrorCode()));
            return;
        }

        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        if ((geofenceTransition & CustomGeofence.GEOFENCE_TRANSITION_TYPES) != 0) {
            // Get the geofences that were triggered. A single event can trigger
            // multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            for (Geofence geofence : triggeringGeofences) {
                String geofenceId = geofence.getRequestId();
                if (geofenceId == null) {
                    Log.wtf(TAG, "Got geofence with null id");
                    continue;
                }
                Long alarmId = GeofenceRepository.getInstance(context)
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

    private void doAlarm(Context context, long alarmId) {
        Intent intent = new Intent(context, AlarmService.class);
        intent.setAction(AlarmService.ACTION_DO_ALARM);
        intent.putExtra(AlarmService.INTENT_EXTRA_ALARM_ID, alarmId);
        context.startService(intent);
    }
}
