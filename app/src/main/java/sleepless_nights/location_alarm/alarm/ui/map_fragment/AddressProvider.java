package sleepless_nights.location_alarm.alarm.ui.map_fragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AddressProvider {

    public interface Callback {
        void onAddressGot(String address);
    }


    private static final String LOG_TAG = "ADDRESS PROVIDER";

    private Geocoder geocoder;
    private Executor executor;

    public AddressProvider(Context context) {
        geocoder = new Geocoder(context, Locale.getDefault());
        executor = Executors.newSingleThreadExecutor();
    }

    public void getAddress(double latitude, double longitude, Callback callback) {
        executor.execute(() -> {
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException e) {
                Log.e(LOG_TAG, e.toString());
            }
            if (addresses == null || addresses.size() == 0) {
                callback.onAddressGot("");
                return;
            }
            Address address = addresses.get(0);
            StringBuilder res = new StringBuilder();
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                res.append(address.getAddressLine(i)).append(" ");
            }
            callback.onAddressGot(res.toString());
        });
    }

}
