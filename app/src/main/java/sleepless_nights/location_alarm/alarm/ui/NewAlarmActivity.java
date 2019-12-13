package sleepless_nights.location_alarm.alarm.ui;

import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.widget.LinearLayout;

import java.util.Objects;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class NewAlarmActivity extends AppCompatActivity {

    private LinearLayout layout;
    private BottomSheetBehavior behavior;
    private TextInputLayout nameInput;
    private TextInputLayout destinatonInput;
    private String name;
    private String destinaton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layout = (LinearLayout) findViewById(R.id.bottom_sheet_layout);
        behavior = BottomSheetBehavior.from(layout);

        nameInput = findViewById(R.id.name);
        destinatonInput = findViewById(R.id.destination);

        AlarmViewModel alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(this)) //Shared with MapFragment
                .get(AlarmViewModel.class);

        FloatingActionButton fab = findViewById(R.id.button_ok);
        fab.setOnClickListener(view -> {
            name = nameInput.getEditText().getText().toString();
            destinaton = destinatonInput.getEditText().getText().toString();

            alarmViewModel.createAlarm(name, destinaton, true, 0, 0,2000);
        });
    }

}
