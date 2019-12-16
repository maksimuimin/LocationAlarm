package sleepless_nights.location_alarm.alarm.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class NewAlarmActivity extends AppCompatActivity {

    private LinearLayout layout;
    private BottomSheetBehavior behavior;

    private EditText nameInput;
    private EditText addressInput;

    private TextView nameHeader;

    private String name = "";
    private String address = "";

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

        toolbar.setNavigationOnClickListener(onNavigationClick);

        layout = (LinearLayout) findViewById(R.id.bottom_sheet_layout);
        behavior = BottomSheetBehavior.from(layout);

        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        behavior.setBottomSheetCallback(bottomSheetCallback);

        TextInputLayout nameInputLayout = findViewById(R.id.name);
        TextInputLayout addressInputLayout = findViewById(R.id.address);

        nameInput = (EditText) nameInputLayout.getEditText();
        addressInput = (EditText) addressInputLayout.getEditText();

        if (nameInput == null || addressInput == null) {
            Log.wtf("ERROR", "could not find Edit text of name or address");
            return;
        }

        nameHeader = findViewById(R.id.name_header);

        nameHeader.setOnClickListener(view -> headerClickListener(nameInput));

        nameInput.setOnKeyListener(onNameInput);

        alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(this)) //Shared with MapFragment
                .get(AlarmViewModel.class);

        fab = findViewById(R.id.button_ok);
        fab.setOnClickListener(onAddButtonClickListener);
    }

    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View view, int newState) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();

            if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                lp.anchorGravity = Gravity.BOTTOM | GravityCompat.END; // TODO fix doesn't work
                fab.setLayoutParams(lp); // TODO fix doesn't work

//                actionBar.hide();
                return;
            }

            lp.anchorGravity = Gravity.TOP | GravityCompat.END; // TODO fix doesn't work
            fab.setLayoutParams(lp); // TODO fix doesn't work

//            actionBar.show();
        }

        @Override
        public void onSlide(@NonNull View view, float v) {

        }
    };

    private View.OnClickListener onAddButtonClickListener = view -> {
        int state = behavior.getState();

        if (state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }

        name = nameInput.getText().toString();
        address = addressInput.getText().toString();

        if (name.isEmpty() || address.isEmpty()) {
            Snackbar.make(view, "Please fill required fields: Name and Destination", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            return;
        }

        alarmViewModel.createAlarm(name, address, true, 0, 0,2000);
    };

    private View.OnClickListener onNavigationClick = view -> finish();

    private View.OnKeyListener onNameInput = (v, keyCode, event) -> {
        String name = nameInput.getText().toString();
        nameHeader.setText(name);

        return false;
    };

    private void headerClickListener (EditText editText) {
        int state = behavior.getState();

        if (state == BottomSheetBehavior.STATE_COLLAPSED) {
            behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        }

        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm == null) {
            Log.wtf("ERROR", "Error happened in getSystemService");
            return;
        }

        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
