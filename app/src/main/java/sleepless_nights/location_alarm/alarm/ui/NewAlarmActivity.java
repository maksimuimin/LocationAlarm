package sleepless_nights.location_alarm.alarm.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import sleepless_nights.location_alarm.LocationAlarmApplication;
import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.ui.map_fragment.MapFragment;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class NewAlarmActivity extends AppCompatActivity {
    private static final String TAG = "NewAlarmActivity";

    private MapFragment mapFragment;

    private EditText nameInput;
    private EditText addressInput;

    private TextView nameHeader;
    private AlarmViewModel alarmViewModel;

    Toolbar toolbar;
    ActionBar actionBar;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }
        toolbar.setNavigationOnClickListener(view -> finish());

        MapFragment mapFragment = LocationAlarmApplication.from(this).getMapFragment();
        //fixme mapFragmentEditNew();
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, mapFragment)
                    .commit();
            return;
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

        nameHeader = findViewById(R.id.name_header);
        nameInput.setOnKeyListener(onNameInput);

        alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(this))
                .get(AlarmViewModel.class);

        fab = findViewById(R.id.button_ok); //TODO #7 add FAB animation
        fab.setOnClickListener(onAddButtonClickListener);
    }

    private View.OnClickListener onAddButtonClickListener = view -> {
        hideKeyboard();

        String name = nameInput.getText().toString();
        String address = addressInput.getText().toString();

        if (name.isEmpty() || address.isEmpty()) {
            Snackbar.make(view, "Please fill required fields: Name and Address", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

        alarmViewModel.createAlarm(name, address, true, 0, 0,2000);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    };

    private View.OnKeyListener onNameInput = (v, keyCode, event) -> {
        String name = nameInput.getText().toString();
        nameHeader.setText(name);
        return false;
    };

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
}
