package sleepless_nights.location_alarm.alarm.ui.map_fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.util.LongSparseArray;
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

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.ui.IMapFragmentActivity;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSetUpdate;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String MODE = "MODE";
    private static final String ID = "ID";
    private static final String LAT = "LAT";
    private static final String LON = "LON";

    private static final String LOG_TAG = "MapFragment";

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
    private IMapFragmentActivity iMapFragmentActivity;
    private AlarmViewModel alarmViewModel;
    private AddressProvider addressProvider;

    private AlarmDataSet alarmDataSet;
    private Alarm showAlarm;
    private LatLng editLatLng;
    private Mode mode;
    private boolean modeChanged;
    private LongSparseArray<Marker> markers;
    private Marker staticMarker;
    private Marker dynamicMarker;

    private GoogleMap googleMap;

    /**
     * factory methods
     * */

    public static MapFragment newCurrentLoc() {
        return createWithArgs(Mode.CURRENT_LOC, null);
    }

    public static MapFragment newShowAll() {
        return createWithArgs(Mode.SHOW_ALL, null);
    }

    public static MapFragment newShow(Alarm alarm) {
        return createWithArgs(Mode.SHOW, alarm);
    }

    public static MapFragment newEdit() {
        return createWithArgs(Mode.EDIT, null);
    }

    private static MapFragment createWithArgs(Mode mode, Alarm alarm) {
        Bundle args = new Bundle();
        args.putString(MODE, mode.toString());
        if (alarm != null) args.putLong(ID, alarm.getId());
        MapFragment res = new MapFragment();
        res.setArguments(args);
        return res;
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

    public void show(Alarm alarm) {
        this.showAlarm = alarm;
        switchMode(Mode.SHOW);
    }

    public void edit() {
        switchMode(Mode.EDIT);
    }

    public void setAddress(String addressString) {
        addressProvider.getLatLong(addressString, (lat, lon) -> {
            if (mode != Mode.EDIT || lat == null || lon == null || activity == null) return;
            activity.runOnUiThread(() -> {
                setStaticMarker(new LatLng(lat, lon));
                zoomAt(staticMarker);
            });
        });
    }

    @Nullable
    public Double getLatitude() {
        if (editLatLng == null) {
            Log.wtf(LOG_TAG, "No LatLng found while getting latitude");
            return null;
        }
        return editLatLng.latitude;
    }

    @Nullable
    public Double getLongitude() {
        if (editLatLng == null) {
            Log.wtf(LOG_TAG, "No LatLng found while getting longitude");
            return null;
        }
        return editLatLng.longitude;
    }

    /**
     * lifecycle callbacks
     * */

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_map, container, false);

        this.activity = getActivity();
        if (activity instanceof IMapFragmentActivity) {
            this.iMapFragmentActivity = (IMapFragmentActivity) activity;
        }
        this.alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(getActivity()))
                .get(AlarmViewModel.class);
        this.addressProvider = new AddressProvider(getContext());

        this.alarmDataSet = alarmViewModel.getLiveData().getValue();
        if (alarmDataSet != null) {
            alarmDataSet = alarmDataSet.clone();
        } else {
            Log.wtf(LOG_TAG, "No alarm data set provided by view model");
        }
        alarmViewModel.getLiveData().observe(getViewLifecycleOwner(), alarmDataSet -> {
            if (mode == Mode.SHOW_ALL) {
                alarmDataSet.diffFrom(this.alarmDataSet).dispatchUpdatesTo(new AlarmDataSetUpdate(alarmDataSet) {
                    @Override
                    public void onInserted(Alarm alarm) {
                        addMarker(alarm);
                    }

                    @Override
                    public void onRemoved(Alarm alarm) {
                        removeMarker(alarm);
                    }

                    @Override
                    public void onChanged(Alarm alarm) {
                        changeMarker(alarm);
                    }
                });
            }
            this.alarmDataSet = alarmDataSet.clone();
        });

        this.mode = Mode.CURRENT_LOC;
        this.modeChanged = true;
        this.markers = new LongSparseArray<>();

        loadFromBundle(getArguments());
        loadFromBundle(savedInstanceState);

        SupportMapFragment googleMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.google_map);
        if (googleMapFragment == null) {
            Log.wtf(LOG_TAG, "Google map fragment not found");
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
                setDynamicMarker(googleMap.getCameraPosition().target);
            }
        });

        googleMap.setOnCameraIdleListener(() -> {
            removeDynamicMarker();
            if (mode == Mode.EDIT && iMapFragmentActivity != null) {
                editLatLng = googleMap.getCameraPosition().target;
                setStaticMarker(editLatLng);
                addressProvider.getAddress(
                        editLatLng.latitude,
                        editLatLng.longitude,
                        address -> activity.runOnUiThread(
                                () -> iMapFragmentActivity.onAddressGot(address)
                        )
                );
            }
        });

        refresh();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MODE, mode.toString());
        if (showAlarm != null) {
            outState.putLong(ID, showAlarm.getId());
        }
        if (editLatLng != null) {
            outState.putDouble(LAT, editLatLng.latitude);
            outState.putDouble(LON, editLatLng.longitude);
        }
    }

    private void loadFromBundle(Bundle bundle) {
        if (bundle == null) return;
        mode = Mode.valueOf(bundle.getString(MODE, Mode.CURRENT_LOC.toString()));
        long id = bundle.getLong(ID, -1);
        if (id != -1) {
            showAlarm = alarmViewModel.getAlarmLiveDataById(id);
        }
        double lat = bundle.getDouble(LAT, Double.MIN_VALUE);
        if (lat != Double.MIN_VALUE) {
            editLatLng = new LatLng(lat, bundle.getDouble(LON));
        }
    }

    /**
     * inner abstraction
     */

    private void switchMode(Mode mode) {
        this.mode = mode;
        modeChanged = true;
        refresh();
    }

    private void refresh() {
        if (activity == null) {
            Log.wtf(LOG_TAG, "Map created with no activity");
            return;
        }
        if (googleMap == null) {
            Log.wtf(LOG_TAG, "Map crated with no google map");
            return;
        }

        clearMarkers();
        if (mode == Mode.CURRENT_LOC || mode == Mode.EDIT) {
            if (mode == Mode.EDIT && !modeChanged) {
                setStaticMarker(editLatLng);
            } else {
                LocationProvider.getCurrentLocation(activity, location -> {
                    setStaticMarker(new LatLng(location.getLatitude(), location.getLongitude()));
                    zoomAt(staticMarker);
                }, () -> {
                    setStaticMarker(new LatLng(0.0, 0.0));
                    zoomAt(staticMarker);
                });
            }
        } else if (mode == Mode.SHOW_ALL) {
            if (alarmDataSet == null) {
                Log.wtf(LOG_TAG, "AlarmDataSet LiveData is empty");
                return;
            }
            for (Alarm alarm : alarmDataSet) {
                addMarker(alarm);
            }
            if (modeChanged) {
                zoomAtAll();
            }
        } else if (mode == Mode.SHOW) {
            if (showAlarm != null) {
                setStaticMarker(new LatLng(showAlarm.getLatitude(), showAlarm.getLongitude()));
                if (modeChanged) {
                    zoomAt(staticMarker);
                }
            } else {
                Log.wtf(LOG_TAG, "show mode with no alarm to show");
            }
        }
        modeChanged = false;
    }

    /**
     * camera
     * */

    private void zoomAtAll() {
        if (markers.size() == 0) return;
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (int i = 0; i < markers.size(); i++) {
            builder.include( markers.get(markers.keyAt(i)).getPosition() );
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), STD_PADDING));
    }

    private void zoomAt(Marker marker) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), STD_ZOOM));
    }

    /**
     * markers
     * */

    private void addMarker(Alarm alarm) {
        LatLng latLng = new LatLng(alarm.getLatitude(), alarm.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker( alarm.getIsActive() ? ON_HUE : OFF_HUE ));
        markers.put(alarm.getId(), googleMap.addMarker(markerOptions));
    }

    private void removeMarker(Alarm alarm) {
        Marker marker = markers.get(alarm.getId());
        if (marker != null) {
            marker.remove();
            markers.remove(alarm.getId());
        }
    }

    private void changeMarker(Alarm alarm) {
        Marker marker = markers.get(alarm.getId());
        if (marker != null) {
            marker.setPosition(new LatLng(alarm.getLatitude(), alarm.getLongitude()));
        } else {
            addMarker(alarm);
        }
    }

    private void setStaticMarker(LatLng latLng) {
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

    private void setDynamicMarker(LatLng latLng) {
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
        for (int i = 0; i < markers.size(); i++) {
            markers.get(markers.keyAt(i)).remove();
        }
        markers.clear();
    }

}
