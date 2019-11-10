package com.example.locationalarm.alarm.ui.alrmlistfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.locationalarm.R;
import com.example.locationalarm.alarm.Alarm;
import com.example.locationalarm.alarm.viewmodels.alarm_view_model.AlarmDataSet;
import com.example.locationalarm.alarm.viewmodels.alarm_view_model.AlarmViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;

public class AlarmListFragment extends Fragment {
    private static final double MAJOR_UPDATE_PERCENTAGE = 0.80;
    private static final String TAG = "AlarmListFragment";

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
        return inflater.inflate(R.layout.fragment_alarm_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AlarmViewModel alarmViewModel = ViewModelProviders
                .of(Objects.requireNonNull(getActivity())) //Shared with MapFragment
                .get(AlarmViewModel.class);
        AlarmDataSet alarmDataSet = alarmViewModel.getData().getValue();
        ArrayList<Alarm> alarms = alarmDataSet==null ? new ArrayList<>() : alarmDataSet.getAlarms();

        final LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        final AlarmListAdapter adapter = new AlarmListAdapter(alarms);
        final RecyclerView listView = view.findViewById(R.id.alarm_list);
        listView.setAdapter(adapter);
        listView.setLayoutManager(layoutManager);

        alarmViewModel.getData().observe(getViewLifecycleOwner(), updAlarmDataSet -> {
            adapter.setAlarms(updAlarmDataSet.getAlarms());

            ArrayList<AlarmDataSet.AlarmDataSetUpdate> updates = updAlarmDataSet.commitUpdates();
            if (updates.size() == 0) {
                Log.wtf(TAG, "alarmDataSet updated with empty updates array");
                return;
            }
            HashSet<Integer> updatedElements = new HashSet<>();
            for (AlarmDataSet.AlarmDataSetUpdate update : updates) {
                updatedElements.add(update.idx);
            }
            if (adapter.getItemCount() == 0 ||
                    (double)updatedElements.size() / adapter.getItemCount() > MAJOR_UPDATE_PERCENTAGE) {
                adapter.notifyDataSetChanged();
                return;
            }

            for (AlarmDataSet.AlarmDataSetUpdate update : updates) {
                switch (update.type) {
                    case CHANGE: {
                        adapter.notifyItemChanged(update.idx);
                        break;
                    }
                    case INSERT: {
                        adapter.notifyItemInserted(update.idx);
                        break;
                    }
                    case REMOVE: {
                        adapter.notifyItemRemoved(update.idx);
                        break;
                    }
                    default: {
                        Log.wtf(TAG, String.format(Locale.getDefault(),
                                "unknown update type %s", update.type));
                    }
                }
            }
        });
    }
}
