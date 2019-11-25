package sleepless_nights.location_alarm.alarm.use_cases;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.LocationServices;

public class LocationRepo {
    //TODO move to a different package?

    public interface Callback {
        void onLocationGot(Location location);
    }

    public static void getCurrentLocation(Activity activity, Callback callback) {
        LocationServices
                .getFusedLocationProviderClient(activity)
                .getLastLocation()
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            callback.onLocationGot(location);
                        }
                    }
                });
    }

}
