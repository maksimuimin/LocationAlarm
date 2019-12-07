package sleepless_nights.location_alarm.alarm.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment.AlarmListFragment;
import sleepless_nights.location_alarm.alarm.ui.alarm_service.AlarmService;
import sleepless_nights.location_alarm.alarm.ui.map_fragment.MapFragment;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int GEO_LOC_PERMISSION_REQUEST = 1;
    private MenuTabState tabState = MenuTabState.TAB_ALARM_LIST;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkGeoLocPermission();
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, AlarmListFragment.newInstance())
                    .commit();

            Toolbar customToolBar = findViewById(R.id.toolbar);
            setSupportActionBar(customToolBar);

            View alarmListTabBtn = findViewById(R.id.app_bar_alarm_list_tab);
            alarmListTabBtn.setOnClickListener(v -> {
                if (tabState == MenuTabState.TAB_ALARM_LIST) return;
                tabState = MenuTabState.TAB_ALARM_LIST;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, AlarmListFragment.newInstance())
                        .commit();
                Toast.makeText(this, "switched to alarm list tab", Toast.LENGTH_SHORT).show();
            });

            View mapTabBtn = findViewById(R.id.app_bar_map_tab);
            mapTabBtn.setOnClickListener(v -> {
                if (tabState == MenuTabState.TAB_MAP) return;
                tabState = MenuTabState.TAB_MAP;
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, MapFragment.newShowAll())
                        .commit();
                Toast.makeText(this, "switched to map tab", Toast.LENGTH_SHORT).show();
            });

            AlarmViewModel alarmViewModel = ViewModelProviders
                    .of(Objects.requireNonNull(this)) //Shared with MapFragment
                    .get(AlarmViewModel.class);
            FloatingActionButton fab = findViewById(R.id.floating_button);
            fab.setOnClickListener(v -> {
               switch (tabState) {
                   case TAB_ALARM_LIST: {
                       alarmViewModel.createAlarm("MyAlarm", "MyAddress", true, 0, 0,2000);
                       break;
                   }
                   case TAB_MAP: {
                       Toast.makeText(this, "MapTab's FAB is on click", Toast.LENGTH_SHORT).show();
                       break;
                   }
                   default: {
                       Log.wtf(TAG, "Got unknown tabState: " + tabState.toString());
                   }
               }
            });
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settingsBtn) {
            Toast.makeText(this, "settingsBtn is on click", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private enum MenuTabState {
        TAB_ALARM_LIST,
        TAB_MAP
    }
}
