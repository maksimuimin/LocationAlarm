package sleepless_nights.location_alarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import sleepless_nights.location_alarm.R;

import sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment.AlarmListFragment;
import sleepless_nights.location_alarm.alarm.view_models.alarm_view_model.AlarmViewModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, AlarmListFragment.newInstance())
                    .commit();

            //TODO delete
            AlarmViewModel alarmViewModel = ViewModelProviders
                    .of(Objects.requireNonNull(this)) //Shared with MapFragment
                    .get(AlarmViewModel.class);
            alarmViewModel.addAlarm("MyAlarm1", "MyAddress", true);
            alarmViewModel.changeAlarm(0, "MyNewName", null, null);
        }
    }
}
