package com.example.locationalarm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class AlarmListFragment extends Fragment {
    private AlarmListAdapter adapter = null;
    private static final String LIST_DATA_BUNDLE = "listDataBundle";

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
        ArrayList<Alarm> alarms;

        if (savedInstanceState == null) {

            if (adapter == null) {
                alarms = new ArrayList<>();

                // TO-DO delete test items
                alarms.add(new Alarm("Test Alarm 1", "Moscow"));
                alarms.add(new Alarm("Test Alarm 2", "Izmaylovskiy metro"));
                alarms.add(new Alarm("Test Alarm 3", "Baumanskiy metro"));
            } else {
                alarms = adapter.getAlarms();
            }

        } else {
            alarms = savedInstanceState.getParcelableArrayList(LIST_DATA_BUNDLE);
        }

        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        adapter = new AlarmListAdapter(alarms);

        RecyclerView listView = view.findViewById(R.id.alarm_list);
        listView.setAdapter(adapter);
        listView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_DATA_BUNDLE, adapter.getAlarms());
    }
}
