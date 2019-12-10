package sleepless_nights.location_alarm.alarm.ui;

import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.LinearLayout;

import sleepless_nights.location_alarm.R;

public class NewAlarmActivity extends AppCompatActivity {

    private LinearLayout layout;
    private BottomSheetBehavior behavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layout = (LinearLayout) findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(layout);

        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
//                       if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                           sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                       } else {
//                           sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                       }

        FloatingActionButton fab = findViewById(R.id.button_ok);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
