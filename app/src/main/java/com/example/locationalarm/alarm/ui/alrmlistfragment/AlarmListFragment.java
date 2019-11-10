package com.example.locationalarm.alarm.ui.alrmlistfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.locationalarm.R;
import com.example.locationalarm.alarm.viewmodels.AlarmViewModel;

import java.util.Objects;

public class AlarmListFragment extends Fragment {
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

        AlarmViewModel alarms = ViewModelProviders
                .of(Objects.requireNonNull(getActivity())) //Shared with MapFragment
                .get(AlarmViewModel.class);
        LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        AlarmListAdapter adapter = new AlarmListAdapter(alarms.getData());

        RecyclerView listView = view.findViewById(R.id.alarm_list);
        listView.setAdapter(adapter);
        listView.setLayoutManager(layoutManager);

    }
}
