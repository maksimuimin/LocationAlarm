package sleepless_nights.location_alarm.alarm.ui.map_fragment;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import sleepless_nights.location_alarm.R;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String INDEX = "INDEX";

    private Activity activity;
    private GoogleMap googleMap;

    public static MapFragment create() {
        return new MapFragment();
    }

    public static MapFragment create(int index) {
        MapFragment res = new MapFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(INDEX, index);
        res.setArguments(arguments);
        return res;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_map, container, false);

        this.activity = getActivity();

        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager != null) {
            SupportMapFragment googleMapFragment =
                    (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map);
            if (googleMapFragment != null) {
                googleMapFragment.getMapAsync(this);
            }
        }

        Bundle arguments = getArguments();
        if (arguments != null) {
            int index = arguments.getInt(INDEX, -1);
            //todo получение энтити из репо по индексу
        }

        return res;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);

        if (activity != null) {
            //todo если энтити нету - получаем текущее положение
            LocationServices
                    .getFusedLocationProviderClient(activity)
                    .getLastLocation()
                    .addOnCompleteListener(activity, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Location loc = (Location) task.getResult();
                                if (loc != null) {
                                    LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                }
                            }
                        }
                    });
            //todo если энтити есть - вытащить координаты из нее и зумить туда
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activity = null;
        googleMap = null;
    }

}
