package sleepless_nights.location_alarm.alarm.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;

import sleepless_nights.location_alarm.R;

import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment.AlarmListFragment;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    private static final int GEO_LOC_PERMISSION_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkGeoLocPermission();
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, AlarmListFragment.newInstance())
                    .commit();

            //TODO delete
            AlarmViewModel alarmViewModel = ViewModelProviders
                    .of(Objects.requireNonNull(this)) //Shared with MapFragment
                    .get(AlarmViewModel.class);
            LiveData<Alarm> alarmLiveData = alarmViewModel.newAlarm();  alarmViewModel.getAlarmLiveDataByPosition(0);
            if (alarmLiveData != null) {
                Alarm alarm = alarmLiveData.getValue();
                if (alarm == null) {
                    Log.wtf("MainActivity", "got LiveData of null Alarm");
                    return;
                }
                alarm.setName("MyNewName");
                alarm.setAddress("MyAddress");
                alarm.setLatitude(55.751244);
                alarm.setLongitude(37.618423);
                alarm.setRadius(2000);
                alarmViewModel.updateAlarm(alarm);
            }
        }
    }

    /**
     * permission
     * */

    private void checkGeoLocPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        GEO_LOC_PERMISSION_REQUEST
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GEO_LOC_PERMISSION_REQUEST) {
            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                //todo выходить из приложения требуя пермишон
                //или отобрабражать фрагмент с требованием пермишона и кнопкой на перезапрос
            }
        }
    }

}
