package sleepless_nights.location_alarm.alarm.ui.map_fragment;

import android.app.Activity;
import android.location.Location;

import com.google.android.gms.location.LocationServices;

public class LocationProvider {

    public interface Callback {
        void onLocationGot(Location location);
    }

    public static void getCurrentLocation(Activity activity, Callback onSuccess, Runnable onFail) {
        LocationServices
                .getFusedLocationProviderClient(activity)
                .getLastLocation()
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            onSuccess.onLocationGot(location);
                        }
                    } else {
                        onFail.run();
                    }
                });
    }

}
