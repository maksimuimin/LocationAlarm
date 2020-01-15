package sleepless_nights.location_alarm.alarm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.ui.map_fragment.MapFragment;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class NewAlarmActivity extends AppCompatActivity implements IMapFragmentActivity {
    private static final String TAG = "NewAlarmActivity";

    private EditText nameInput;
    private EditText addressInput;

    private AlarmViewModel alarmViewModel;

    Toolbar toolbar;
    ActionBar actionBar;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        linearLayout = findViewById(R.id.activity_new_alarm);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }
        toolbar.setNavigationOnClickListener(view -> finish());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, MapFragment.newEdit())
                    .commitNow();
        }

        LinearLayout layout = findViewById(R.id.bottom_sheet_layout);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(layout);
        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        TextInputLayout nameInputLayout = findViewById(R.id.name);
        TextInputLayout addressInputLayout = findViewById(R.id.address);
        nameInput = nameInputLayout.getEditText();
        addressInput = addressInputLayout.getEditText();
        if (nameInput == null || addressInput == null) {
            Log.wtf(TAG, "could not find Edit text of name or address");
            return;
        }

        alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(this))
                .get(AlarmViewModel.class);

        MapFragment mapFragment =
                (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (mapFragment != null) {
            addressInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId != EditorInfo.IME_ACTION_DONE) return false;
                mapFragment.setAddress(v.getText().toString());
                return false;
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_new_alarm, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.confirm_button) {
            confirmAddingNewAlarm();
        }

        return true;
    }

    private void confirmAddingNewAlarm() {
        hideKeyboard();

        String name = nameInput.getText().toString();
        String address = addressInput.getText().toString();

        if (name.isEmpty() || address.isEmpty()) {
            Snackbar.make(linearLayout, "Please fill required fields: Name and Address", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        MapFragment mapFragment =
                (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (mapFragment != null) {
            //TODO editable radius
            Double lat = mapFragment.getLatitude();
            Double lon = mapFragment.getLongitude();
            if (lat == null || lon == null) {
                Log.wtf(TAG, "No latitude or longitude got from map fragment");
                return;
            }
            alarmViewModel.createAlarm(name, address, true, lat, lon, 2000);
        } else {
            //perhaps showing user something went wrong?
            Log.wtf(TAG, "No map fragment found");
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) {
            Log.wtf(TAG, "unable to get INPUT_METHOD_SERVICE");
            return;
        }

        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onAddressGot(String address) {
        addressInput.setText(address);
    }

}
