package sleepless_nights.location_alarm;

import android.app.Application;
import android.content.Context;

import sleepless_nights.location_alarm.alarm.ui.map_fragment.MapFragment;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmRepository;
import sleepless_nights.location_alarm.geofence.use_cases.GeofenceRepository;
import sleepless_nights.location_alarm.permission.use_cases.PermissionRepository;

public class LocationAlarmApplication extends Application {
    private GeofenceRepository geofenceRepository;
    private AlarmRepository alarmRepository;
    private PermissionRepository permissionRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        geofenceRepository = new GeofenceRepository(getApplicationContext());
        alarmRepository = new AlarmRepository(getApplicationContext());
        permissionRepository = new PermissionRepository(getApplicationContext());
    }

    public GeofenceRepository getGeofenceRepository() {
        return geofenceRepository;
    }

    public AlarmRepository getAlarmRepository() {
        return alarmRepository;
    }

    public PermissionRepository getPermissionRepository() { return permissionRepository; }

    public static LocationAlarmApplication from(Context context) {
        return (LocationAlarmApplication) context.getApplicationContext();
    }
}
