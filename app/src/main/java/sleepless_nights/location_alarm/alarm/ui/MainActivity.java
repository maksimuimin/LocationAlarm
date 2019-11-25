package sleepless_nights.location_alarm.alarm.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import java.util.Objects;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment.AlarmListFragment;
import sleepless_nights.location_alarm.alarm.ui.alarm_service.AlarmService;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

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

            Toolbar customToolBar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(customToolBar);

            //TODO delete
            AlarmViewModel alarmViewModel = ViewModelProviders
                    .of(Objects.requireNonNull(this)) //Shared with MapFragment
                    .get(AlarmViewModel.class);
            alarmViewModel.createAlarm("MyAlarm1", "MyAddress", true, 0, 0,2000);
            //alarmViewModel.createAlarm("MyAlarm2", "MyAddress", true, 0, 0,2000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(getApplicationContext(), AlarmService.class);
        startService(intent); //starting service on process creation
        //Alarm service handles its background/foreground state on its own
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

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
        //TODO request ACCESS_BACKGROUND_LOCATION for API 29+
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
