package sleepless_nights;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import sleepless_nights.location_alarm.alarm.ui.alarm_service.AlarmService;
import sleepless_nights.location_alarm.geofence.use_cases.GeofenceRepository;

public class LocationAlarmApplication extends Application {
    private GeofenceRepository geofenceRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        geofenceRepository = new GeofenceRepository(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), AlarmService.class);
        startService(intent); //starting service on process creation
        //Alarm service handles its background/foreground state on its own
    }

    public GeofenceRepository getGeofenceRepository() {
        return geofenceRepository;
    }

    public static LocationAlarmApplication from(Context context) {
        return (LocationAlarmApplication) context.getApplicationContext();
    }
}
