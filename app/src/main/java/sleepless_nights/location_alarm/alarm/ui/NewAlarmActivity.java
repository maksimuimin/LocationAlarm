package sleepless_nights.location_alarm.alarm.ui;

import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.Objects;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class NewAlarmActivity extends AppCompatActivity {

    private LinearLayout layout;
    private BottomSheetBehavior behavior;
    private TextInputLayout nameInput;
    private TextInputLayout addressInput;
    private String name = "";
    private String address = "";

    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        toolbar.setNavigationOnClickListener(onNavigationClickListener);

        layout = (LinearLayout) findViewById(R.id.bottom_sheet_layout);
        behavior = BottomSheetBehavior.from(layout);

        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

        nameInput = findViewById(R.id.name);
        addressInput = findViewById(R.id.destination);

        alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(this)) //Shared with MapFragment
                .get(AlarmViewModel.class);

        FloatingActionButton fab = findViewById(R.id.button_ok);
        fab.setOnClickListener(onAddButtonClickListener);
    }

    private View.OnClickListener onAddButtonClickListener = view -> {
        int state = behavior.getState();

        if (state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        EditText nameInputEditText = nameInput.getEditText();
        EditText destinationInputEditText = addressInput.getEditText();

        if (nameInputEditText == null || destinationInputEditText == null) {
            Log.wtf("ERROR", "could not find Edit text of name or address");
            return;
        }

        name = nameInputEditText.getText().toString();
        address = destinationInputEditText.getText().toString();

        if (name.isEmpty() || address.isEmpty()) {
            Snackbar.make(view, "Please fill required fields: Name and Destination", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return;
        }

        alarmViewModel.createAlarm(name, address, true, 0, 0,2000);
    };

    private View.OnClickListener onNavigationClickListener = view -> finish();

}
