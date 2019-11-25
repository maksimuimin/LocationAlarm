package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;
import java.util.Objects;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class AlarmListFragment extends Fragment {
    private static final String TAG = "AlarmListFragment";
    private AlarmListAdapter alarmListAdapter;

    public static AlarmListFragment newInstance() { return new AlarmListFragment(); }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

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
        AlarmDataSet alarmDataSet = alarmViewModel.getLiveData().getValue();
        if (alarmDataSet == null) {
            Log.wtf(TAG, "alarmViewModel returned LiveData with null DataSet");
            return;
        }

        final LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        alarmListAdapter = new AlarmListAdapter(alarmDataSet, alarmViewModel);
        final RecyclerView listView = view.findViewById(R.id.alarm_list);
        listView.setAdapter(alarmListAdapter);
        listView.setLayoutManager(layoutManager);

        alarmViewModel.getLiveData().observe(getViewLifecycleOwner(), updAlarmDataSet -> {
            Log.d(TAG, String.format(Locale.getDefault(),
                    "dataSet updated, updAlarmDataSet size: %d", updAlarmDataSet.size()));
            AlarmDataSet oldDataSet = alarmListAdapter.getAlarmDataSet();
            alarmListAdapter.setAlarmDataSet(updAlarmDataSet);
            updAlarmDataSet.diffFrom(oldDataSet).dispatchUpdatesTo(alarmListAdapter);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
