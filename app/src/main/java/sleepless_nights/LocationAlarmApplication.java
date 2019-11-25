package sleepless_nights;

import android.app.Application;
import android.content.Context;

import sleepless_nights.location_alarm.geofence.use_cases.GeofenceRepository;

public class LocationAlarmApplication extends Application {
    private GeofenceRepository geofenceRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        geofenceRepository = new GeofenceRepository(getApplicationContext());
    }

    public GeofenceRepository getGeofenceRepository() {
        return geofenceRepository;
    }

    public static LocationAlarmApplication from(Context context) {
        return (LocationAlarmApplication) context.getApplicationContext();
    }
}
