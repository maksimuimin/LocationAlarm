package sleepless_nights.location_alarm.alarm.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment.AlarmListFragment;
import sleepless_nights.location_alarm.alarm.ui.alarm_service.AlarmService;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;
import sleepless_nights.location_alarm.permission.Permission;
import sleepless_nights.location_alarm.permission.use_cases.PermissionRepository;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Integer MUST_HAVE_PERMISSIONS_REQUEST_ID = null;
    private MenuTabState tabState;
    private final String TAB_STATE_NAME_BUNDLE_KEY = "tabState.name";
    //TODO #6 optimize AlarmListFragment creations by extracting it to a field of MainActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MUST_HAVE_PERMISSIONS_REQUEST_ID = PermissionRepository.getInstance(this)
                .requirePermissionsByGroup(this, Permission.Group.MUST_HAVE);
        setContentView(R.layout.activity_main);

        Toolbar customToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(customToolBar);

        View alarmListTabBtn = findViewById(R.id.app_bar_alarm_list_tab);
        alarmListTabBtn.setOnClickListener(v -> {
            if (tabState == MenuTabState.TAB_ALARM_LIST) return;
            tabState = MenuTabState.TAB_ALARM_LIST;
            Toast.makeText(this, "switched to alarm list tab", Toast.LENGTH_SHORT).show();
        });

        View mapTabBtn = findViewById(R.id.app_bar_map_tab);
        mapTabBtn.setOnClickListener(v -> {
            if (tabState == MenuTabState.TAB_MAP) return;
            tabState = MenuTabState.TAB_MAP;
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

        if (savedInstanceState == null) {
            tabState = MenuTabState.TAB_ALARM_LIST;
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, AlarmListFragment.newInstance())
                    .commit();
            return;
        }

        tabState = MenuTabState.valueOf(savedInstanceState.getString(TAB_STATE_NAME_BUNDLE_KEY));
        switch (tabState) {
            case TAB_ALARM_LIST: {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, AlarmListFragment.newInstance())
                        .commit();
                break;
            }
            case TAB_MAP: {
                //TODO #5 load map fragment
                break;
            }
            default: {
                Log.wtf(TAG, "got unknown tabState from savedInstanceState: " + tabState);
                tabState = MenuTabState.TAB_ALARM_LIST;
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, AlarmListFragment.newInstance())
                        .commit();
                break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TAB_STATE_NAME_BUNDLE_KEY, tabState.name());
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
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.not_enough_permissions_dialog_title)
                            .setMessage(R.string.not_enough_permissions_dialog_message)
                            .setCancelable(false)
                            .setPositiveButton(R.string.dialog_positive_button,
                                    ((dialog, which) -> {
                                        finishAndRemoveTask();
                                        System.exit(0);
                                    }))
                            .show();
                }
            }
        }
    }

    private enum MenuTabState {
        TAB_ALARM_LIST,
        TAB_MAP
    }
}
