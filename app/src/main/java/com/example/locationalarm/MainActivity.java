package com.example.locationalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.locationalarm.alarm.ui.alrmlistfragment.AlarmListFragment;
import com.example.locationalarm.alarm.use_cases.AlarmRepository;

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
            AlarmRepository.getInstance().registerAlarm("MyAlarm1", "MyAddress", true);
        }
    }
}
