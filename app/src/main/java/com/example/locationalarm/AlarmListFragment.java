package com.example.locationalarm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AlarmListFragment extends Fragment {
    private AlarmListAdapter adapter = null;

    public static AlarmListFragment newInstance() {
        return new AlarmListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm_list, container, false);

        ArrayList<Alarm> alarms = new ArrayList<>();
        Alarm testAlarm = new Alarm("Test Alarm", "Moscow");
        alarms.add(testAlarm);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        adapter = new AlarmListAdapter(alarms);

        RecyclerView listView = view.findViewById(R.id.alarm_list);
        listView.setAdapter(adapter);
        listView.setLayoutManager(layoutManager);

        return view;
    }
}
