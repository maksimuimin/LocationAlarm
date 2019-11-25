package sleepless_nights;

import android.app.Application;
import android.content.Context;

import sleepless_nights.location_alarm.alarm.use_cases.AlarmRepository;
import sleepless_nights.location_alarm.geofence.use_cases.GeofenceRepository;

public class LocationAlarmApplication extends Application {
    private GeofenceRepository geofenceRepository;
    private AlarmRepository alarmRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        geofenceRepository = new GeofenceRepository(getApplicationContext());
        alarmRepository = new AlarmRepository(getApplicationContext());
    }

    public GeofenceRepository getGeofenceRepository() {
        return geofenceRepository;
    }

    public AlarmRepository getAlarmRepository() {
        return alarmRepository;
    }

    public static LocationAlarmApplication from(Context context) {
        return (LocationAlarmApplication) context.getApplicationContext();
    }
}
