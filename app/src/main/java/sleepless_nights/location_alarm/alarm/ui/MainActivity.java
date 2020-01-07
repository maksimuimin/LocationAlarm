package sleepless_nights.location_alarm.alarm.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment.AlarmListFragment;
import sleepless_nights.location_alarm.alarm.ui.alarm_service.AlarmService;
import sleepless_nights.location_alarm.alarm.ui.map_fragment.MapFragment;
import sleepless_nights.location_alarm.permission.Permission;
import sleepless_nights.location_alarm.permission.use_cases.PermissionRepository;

public class MainActivity extends AppCompatActivity implements IMainActivity {
    private static final String TAG = "MainActivity";
    private Integer MUST_HAVE_PERMISSIONS_REQUEST_ID = null;
    private AlertDialog permissionDialog;
    private final String TAB_STATE_NAME_BUNDLE_KEY = "tabState.name";
    //TODO #6 optimize AlarmListFragment creations by extracting it to a field of MainActivity

    BottomNavigationView bottomNavigationView;
    private int selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.not_enough_permissions_dialog_title)
                .setMessage(R.string.not_enough_permissions_dialog_message)
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_positive_button,
                        ((dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", this.getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }))
                .create();

        MUST_HAVE_PERMISSIONS_REQUEST_ID = PermissionRepository.getInstance(this)
                .requirePermissionsByGroup(this, Permission.Group.MUST_HAVE);
        setContentView(R.layout.activity_main);

        Toolbar customToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(customToolBar);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

        FloatingActionButton fab = findViewById(R.id.floating_button);
        fab.setOnClickListener(v -> newAlarm());

        if (savedInstanceState == null) {
            showAlarmList();
            return;
        }

        int selectedItem = savedInstanceState.getInt(TAB_STATE_NAME_BUNDLE_KEY);
        bottomNavigationView.setSelectedItemId(selectedItem);

    }

    BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = item -> {
        int id = item.getItemId();
        int ALARM_LIST_TAB_ID = R.id.app_bar_alarm_list_tab;
        int MAP_TAB_ID = R.id.app_bar_map_tab;

        if (id == ALARM_LIST_TAB_ID && selectedItem != ALARM_LIST_TAB_ID) {
            showAlarmList();
        }

        if (id == MAP_TAB_ID && selectedItem != MAP_TAB_ID) {
            showAllAlarms();
        }

        selectedItem = id;
        return true;
    };

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomNavigationView.getSelectedItemId();
        outState.putInt(TAB_STATE_NAME_BUNDLE_KEY, bottomNavigationView.getSelectedItemId());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(getApplicationContext(), AlarmService.class);
        startService(intent); //starting service on process creation
        //Alarm service handles its background/foreground state on its own
        boolean havePermissions = PermissionRepository.getInstance(this)
                .isPermissionGroupGranted(Permission.Group.MUST_HAVE);
        if (permissionDialog.isShowing() && havePermissions) {
            permissionDialog.hide();
            //This may happen when user changes permission in app background
        }
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
            //  TODO - change with SettingsActivity
            Toast.makeText(this, "settingsBtn is on click", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MUST_HAVE_PERMISSIONS_REQUEST_ID) {
            if (grantResults.length == 0) {
                Log.wtf(TAG, "got grantResults with 0 length");
            }
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "Not enough permissions, exiting");
                    permissionDialog.show();
                }
            }
        }
    }

    @Override
    public void showAlarmList() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, AlarmListFragment.newInstance())
                .commit();
    }

    @Override
    public void showAllAlarms() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, MapFragment.newShowAll())
                .commit();
    }

    @Override
    public void newAlarm() {
        Intent intent = new Intent(this, NewAlarmActivity.class);
        startActivity(intent);
    }

    @Override
    public void showAlarm(Alarm alarm) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, MapFragment.newShow(alarm))
                .commit();
    }

    @Override
    public void editAlarm(Alarm alarm) {
        //
    }
}
