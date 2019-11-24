package sleepless_nights.location_alarm.alarm.ui.map_fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.ui.Router;
import sleepless_nights.location_alarm.alarm.use_cases.LocationRepo;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String CREATION_MODE = "CREATION_MODE";
    private static final String ID = "ID";
    private static final float STD_ZOOM = 10.0f;

    private Activity activity;
    private volatile boolean creationMode;

    private GoogleMap googleMap;
    private Marker marker;
    private EditText nameET;
    private EditText addressET;
    private TextView latLonTw;
    private Button saveCreateButton;

    private AlarmViewModel alarmViewModel;
    private LiveData<Alarm> alarmLd;

    public static MapFragment create() {
        MapFragment res = new MapFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(CREATION_MODE, true);
        res.setArguments(arguments);
        return res;
    }

    public static MapFragment create(Alarm alarm) {
        MapFragment res = new MapFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(CREATION_MODE, false);
        arguments.putInt(ID, alarm.getId());
        res.setArguments(arguments);
        return res;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View res = inflater.inflate(R.layout.fragment_map, container, false);

        this.activity = getActivity();

        this.latLonTw = res.findViewById(R.id.latitude_longitude_text);
        this.nameET = res.findViewById(R.id.name_text);
        this.addressET = res.findViewById(R.id.address_text);
        this.saveCreateButton = res.findViewById(R.id.save_create_button);

        alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(getActivity()))
                .get(AlarmViewModel.class);

        Bundle arguments = getArguments();
        if (arguments != null) {
            creationMode = arguments.getBoolean(CREATION_MODE);
            if (creationMode) {
                saveCreateButton.setText(getResources().getText(R.string.create));
                alarmLd = alarmViewModel.newAlarm();
            } else {
                saveCreateButton.setText(getResources().getText(R.string.save));
                int id = arguments.getInt(ID);
                alarmLd = alarmViewModel.getAlarmLiveDataById(id);
            }
            //todo additional checks
        }

        Alarm alarm = getAlarm();
        if (alarm != null) {
            nameET.setText(alarm.getName());
            addressET.setText(alarm.getAddress());
        }

        saveCreateButton.setOnClickListener(v -> {
            LatLng latLng = marker.getPosition();
            Alarm updatedAlarm = getAlarm();
            if (updatedAlarm != null) {
                updatedAlarm.setName(nameET.getText().toString())
                        .setAddress(addressET.getText().toString())
                        .setLatitude(latLng.latitude)
                        .setLongitude(latLng.longitude);
                alarmViewModel.updateAlarm(updatedAlarm);
            }
            creationMode = false;
            Router.showAlarms();
        });

        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment googleMapFragment =
                (SupportMapFragment) fragmentManager.findFragmentById(R.id.google_map);
        if (googleMapFragment != null) {
            googleMapFragment.getMapAsync(this);
        }

        return res;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (creationMode) {
            alarmViewModel.removeAlarm(alarmLd);
        }
        activity = null;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        googleMap.setOnCameraIdleListener(() -> {
            if (marker != null) {
                marker.remove();
            }
            LatLng latLng = googleMap.getCameraPosition().target;
            marker = googleMap.addMarker(new MarkerOptions().position(latLng));
            String latLongText = latLng.latitude
                            + getResources().getText(R.string.latitude_longitude_separator).toString()
                            + latLng.longitude;
            latLonTw.setText(latLongText);
        });

        if (activity != null) {
            Alarm alarm = getAlarm();
            if (alarm != null) {
                if (creationMode) {
                    LocationRepo.getCurrentLocation(activity, location ->
                            zoomAt(location.getLatitude(), location.getLongitude())
                    );
                } else {
                    zoomAt(alarm.getLatitude(), alarm.getLongitude());
                }
            }
        }
    }

    private Alarm getAlarm() {
        if (alarmLd != null) {
            Alarm res = alarmLd.getValue();
            if (res != null) {
                return res;
            } else {
                Log.wtf("MAP", "alarm live data is empty");
            }
        } else {
            Log.wtf("MAP", "alarm live data is null");
        }
        return null;
    }

    private void zoomAt(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, STD_ZOOM));
        Log.d("MAP", "moving camera " + latitude + " : " + longitude);
    }
}
