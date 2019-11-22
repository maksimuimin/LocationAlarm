package sleepless_nights.location_alarm.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

import java.util.Locale;

import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.geofence.use_cases.GeofenceBroadcastReceiver;

public class CustomGeofence {
    public static final int GEOFENCE_TRANSITION_TYPES = Geofence.GEOFENCE_TRANSITION_ENTER;

    private int alarmId;
    private Geofence geofence;
    private PendingIntent geofencePendingIntent;
    private Context context;

    public CustomGeofence(Context context, @NonNull Alarm alarm) {
        String key = String.format(Locale.getDefault(),
                "LocationAlarm-geofence-%d", alarm.getId());
        this.alarmId = alarm.getId();
        this.geofence = new Geofence.Builder()
                .setRequestId(key)
                .setCircularRegion(
                        alarm.getLatitude(),
                        alarm.getLongitude(),
                        alarm.getRadius()
                )
                .setTransitionTypes(GEOFENCE_TRANSITION_TYPES)
                .build();
        this.context = context;
    }

    public GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    public PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }
        Intent intent = new Intent(context, GeofenceBroadcastReceiver.class);
        // We use FLAG_UPDATE_CURRENT so we get the same pending intent back when
        // starting and stopping Monitoring
        geofencePendingIntent = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;
    }

    @NonNull
    public String getGeofenceId() { return geofence.getRequestId(); }

    public int getAlarmId() { return alarmId; }
}
