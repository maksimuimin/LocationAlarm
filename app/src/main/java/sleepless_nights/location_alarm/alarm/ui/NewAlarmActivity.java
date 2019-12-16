package sleepless_nights.location_alarm.alarm.ui;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Objects;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class NewAlarmActivity extends AppCompatActivity {

    private LinearLayout layout;
    private BottomSheetBehavior behavior;

    private ImageView sheetSwitcher;

    private EditText nameInput;
    private EditText addressInput;

    private TextView nameHeader;
    private TextView addressHeader;

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

        toolbar.setNavigationOnClickListener(onNavigationClick);

        sheetSwitcher = findViewById(R.id.sheet_switcher);
        sheetSwitcher.setOnClickListener(onSwitchClick);

        layout = (LinearLayout) findViewById(R.id.bottom_sheet_layout);
        behavior = BottomSheetBehavior.from(layout);

        behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    sheetSwitcher.setImageResource(R.drawable.ic_expand_less_40);
                    return;
                }

                sheetSwitcher.setImageResource(R.drawable.ic_expand_more_40);
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        TextInputLayout nameInputLayout = findViewById(R.id.name);
        TextInputLayout addressInputLayout = findViewById(R.id.address);

        nameInput = (EditText) nameInputLayout.getEditText();
        addressInput = (EditText) addressInputLayout.getEditText();

        if (nameInput == null || addressInput == null) {
            Log.wtf("ERROR", "could not find Edit text of name or address");
            return;
        }

        nameHeader = findViewById(R.id.name_header);
        addressHeader = findViewById(R.id.address_header);

        nameHeader.setOnClickListener(view -> headerClickListener(nameInput));
        addressHeader.setOnClickListener(view -> headerClickListener(addressInput));

        nameInput.setOnKeyListener(onNameInput);
        addressInput.setOnKeyListener(onAddressInput);

        alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(this)) //Shared with MapFragment
                .get(AlarmViewModel.class);

        FloatingActionButton fab = findViewById(R.id.button_ok);
        fab.setOnClickListener(onAddButtonClickListener);
    }

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

    private View.OnClickListener onSwitchClick = view -> {
        int state = behavior.getState();

        switch (state) {
            case BottomSheetBehavior.STATE_COLLAPSED:
            case BottomSheetBehavior.STATE_EXPANDED: {
                behavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                break;
            }

            case BottomSheetBehavior.STATE_HALF_EXPANDED: {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            }
        }
    };
    
    private View.OnKeyListener onNameInput = (v, keyCode, event) -> {
        String name = nameInput.getText().toString();
        nameHeader.setText(name);

        return false;
    };

    private View.OnKeyListener onAddressInput = (v, keyCode, event) -> {
        String address = addressInput.getText().toString();
        addressHeader.setText(address);

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
