package sleepless_nights.location_alarm.alarm.ui.map_fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.use_cases.LocationRepo;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final float STD_ZOOM = 10.0f;
    private static final float OFF_HUE = BitmapDescriptorFactory.HUE_AZURE;
    private static final float ON_HUE = BitmapDescriptorFactory.HUE_RED;
    private static final float SELF_HUE = BitmapDescriptorFactory.HUE_VIOLET;

    private static final String MODE = "mode";
    private static final String ID = "id";

    public enum Mode {
        CURRENT_LOC, SHOW_ALL, SHOW
    }

    /**
     * fields
     */

    private Activity activity;
    private AlarmViewModel alarmViewModel;
    private Alarm alarm;

    private Mode mode;
    private List<Marker> markers;

    private GoogleMap googleMap;

    /**
     * factory methods
     * */

    public static MapFragment newCurrentLoc() {
        Bundle arguments = new Bundle();
        arguments.putString(MODE, Mode.CURRENT_LOC.name());
        return createWithArguments(arguments);
    }

    public static MapFragment newShowAll() {
        Bundle arguments = new Bundle();
        arguments.putString(MODE, Mode.SHOW_ALL.name());
        return createWithArguments(arguments);
    }

    public static MapFragment newShow(long id) {
        Bundle arguments = new Bundle();
        arguments.putString(MODE, Mode.SHOW.name());
        arguments.putLong(ID, id);
        return createWithArguments(arguments);
    }

    private static MapFragment createWithArguments(Bundle arguments) {
        MapFragment res = new MapFragment();
        res.setArguments(arguments);
        return res;
    }

    /**
     * API
     */

    public void currentLoc() {
        mode = Mode.CURRENT_LOC;
        refresh();
    }

    public void showAll() {
        mode = Mode.SHOW_ALL;
        refresh();
    }

    public void show(long id) {
        alarm = alarmViewModel.getAlarmLiveDataById(id);
        mode = Mode.SHOW;
        refresh();
    }

    /**
     * lifecycle callbacks
     * */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_map, container, false);

        this.activity = getActivity();
        this.alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(getActivity()))
                .get(AlarmViewModel.class);
        //fixme можно оптимизировать
        alarmViewModel.getLiveData().observe(getViewLifecycleOwner(), alarmDataSet -> {
            if (mode != Mode.CURRENT_LOC) {
                refresh();
            }
        });

        Bundle arguments = getArguments();
        if (arguments != null && arguments.getString(MODE) != null) {
            this.mode = Mode.valueOf(arguments.getString(MODE));
            if (mode == Mode.SHOW) {
                if (arguments.getLong(ID, -1) != -1) {
                    alarm = alarmViewModel.getAlarmLiveDataById(arguments.getLong(ID));
                } else {
                    Log.wtf("MAP", "No alarm found by ID");
                }
            }
        } else {
            this.mode = Mode.CURRENT_LOC;
        }
        this.markers = new ArrayList<>();

        SupportMapFragment googleMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (googleMapFragment == null) {
            Log.wtf("MAP", "Google map fragment not found");
            return res;
        }
        googleMapFragment.getMapAsync(this);
        return res;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        refresh();
    }

    /**
     * inner abstraction
     */

    private void refresh() {
        if (activity == null) {
            Log.wtf("MAP", "Map created with no activity");
            return;
        }
        if (googleMap == null) {
            Log.wtf("MAP", "Map crated with no google map");
            return;
        }

        clearMarkers();
        if (mode == Mode.CURRENT_LOC) {
            LocationRepo.getCurrentLocation(activity, location -> {
                addMarker(googleMap, location.getLatitude(), location.getLongitude(), SELF_HUE);
                zoomAt(location.getLatitude(), location.getLongitude());
            });
        } else if (mode == Mode.SHOW_ALL) {
            AlarmDataSet alarmDataSet = alarmViewModel.getLiveData().getValue();
            if (alarmDataSet == null) {
                Log.wtf("MAP", "AlarmDataSet LiveData is empty");
                return;
            }
            for (Alarm alarm : alarmDataSet) {
                addMarker(googleMap, alarm);
            }
        } else if (mode == Mode.SHOW && alarm != null) {
            alarm = alarmViewModel.getAlarmLiveDataById(alarm.getId());
            if (alarm == null) {
                Log.wtf("MAP", "No alarm found while refreshing");
                return;
            }
            addMarker(googleMap, alarm);
            zoomAt(alarm.getLatitude(), alarm.getLongitude());
        }
    }

    private void zoomAt(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, STD_ZOOM));
        Log.d("MAP", "moving camera " + latitude + " : " + longitude);
    }

    private void addMarker(GoogleMap googleMap, Alarm alarm) {
        addMarker(googleMap, alarm.getLatitude(), alarm.getLongitude(), alarm.getIsActive() ? ON_HUE : OFF_HUE);
    }

    private void addMarker(GoogleMap googleMap, double latitude, double longitude, float hue) {
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(hue));
        markers.add(googleMap.addMarker(markerOptions));
    }

    private void clearMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

}
