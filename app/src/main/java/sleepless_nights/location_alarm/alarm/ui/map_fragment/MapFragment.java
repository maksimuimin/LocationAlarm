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
import com.google.android.gms.maps.model.LatLngBounds;
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
    private static final int STD_PADDING = 80;

    private static final float OFF_HUE = BitmapDescriptorFactory.HUE_AZURE;
    private static final float ON_HUE = BitmapDescriptorFactory.HUE_RED;
    private static final float STATIC_HUE = BitmapDescriptorFactory.HUE_VIOLET;
    private static final float DYNAMIC_HUE = BitmapDescriptorFactory.HUE_VIOLET;

    public enum Mode {
        CURRENT_LOC, SHOW_ALL, SHOW, EDIT
    }

    /**
     * fields
     */

    private Activity activity;
    private AlarmViewModel alarmViewModel;

    private Mode mode;
    private List<Marker> markers;
    private Marker staticMarker;
    private Marker dynamicMarker;

    private GoogleMap googleMap;

    /**
     * factory methods
     * */

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    /**
     * API
     */

    public void currentLoc() {
        switchMode(Mode.CURRENT_LOC);
    }

    public void showAll() {
        switchMode(Mode.SHOW);
    }

    @Deprecated
    //fixme
    public void show(long id) {
//        this.id = id;
//        switchMode(Mode.SHOW);
    }

    public void edit() {
        switchMode(Mode.EDIT);
    }

    public double getLatitude() {
        return googleMap.getCameraPosition().target.latitude;
    }

    public double getLongitude() {
        return googleMap.getCameraPosition().target.longitude;
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
            if (mode == Mode.SHOW_ALL) {
                refresh();
            }
        });

        this.mode = Mode.CURRENT_LOC;
        this.markers = new ArrayList<>();

        SupportMapFragment googleMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
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

        googleMap.setOnCameraMoveListener(() -> {
            if (mode == Mode.EDIT) {
                setDynamicMarker(googleMap, googleMap.getCameraPosition().target);
            }
        });

        googleMap.setOnCameraIdleListener(() -> {
            removeDynamicMarker();
            if (mode == Mode.EDIT) {
                setStaticMarker(googleMap, googleMap.getCameraPosition().target);
            }
        });

//        googleMap.setOnMarkerClickListener(marker -> {
//            if (marker != null) {
//                Long id = markerToId.get(marker);
//                if (id != null) {
//                    Alarm alarm = alarmViewModel.getAlarmLiveDataById(id);
//                    if (alarm != null) {
//                        alarm.setIsActive(!alarm.getIsActive());
//                        alarmViewModel.updateAlarm(alarm);
//                        return true;
//                    }
//                }
//            }
//            return false;
//        });

        refresh();
    }

    /**
     * inner abstraction
     */

    private void switchMode(Mode mode) {
        this.mode = mode;
        refresh();
    }

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
        if (mode == Mode.CURRENT_LOC || mode == Mode.EDIT) {
            LocationRepo.getCurrentLocation(activity, location -> {
                setStaticMarker(googleMap, new LatLng(location.getLatitude(), location.getLongitude()));
                zoomAt(staticMarker);
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
            zoomAtAll();
        } else if (mode == Mode.SHOW) {
            //fixme implement
        }
    }

    /**
     * camera
     * */

    private void zoomAtAll() {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), STD_PADDING));
        Log.d("MAP", "moving camera around all alarms");
    }

    private void zoomAt(Marker marker) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), STD_ZOOM));
    }

    /**
     * markers
     * */

    private void addMarker(GoogleMap googleMap, Alarm alarm) {
        LatLng latLng = new LatLng(alarm.getLatitude(), alarm.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker( alarm.getIsActive() ? ON_HUE : OFF_HUE ));
        markers.add(googleMap.addMarker(markerOptions));
    }

    private void setStaticMarker(GoogleMap googleMap, LatLng latLng) {
        if (staticMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(STATIC_HUE));
            staticMarker = googleMap.addMarker(markerOptions);
        } else {
            staticMarker.setPosition(latLng);
        }
    }

    private void removeStaticMarker() {
        if (staticMarker != null) {
            staticMarker.remove();
        }
    }

    private void setDynamicMarker(GoogleMap googleMap, LatLng latLng) {
        if (dynamicMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .alpha(0.5f)
                    .icon(BitmapDescriptorFactory.defaultMarker(DYNAMIC_HUE));
            dynamicMarker = googleMap.addMarker(markerOptions);
        } else {
            dynamicMarker.setPosition(latLng);
        }
    }

    private void removeDynamicMarker() {
        if (dynamicMarker != null) {
            dynamicMarker.remove();
            dynamicMarker = null;
        }
    }

    private void clearMarkers() {
        removeStaticMarker();
        removeDynamicMarker();
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

}
