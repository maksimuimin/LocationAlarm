package com.example.locationalarm.alarm.ui.alrmlistfragment;

import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.locationalarm.R;
import com.example.locationalarm.alarm.Alarm;
import com.example.locationalarm.alarm.view_models.alarm_view_model.AlarmViewModel;

class AlarmViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "AlarmViewHolder";

    private TextView alarmNameView;
    private TextView addressView;
    private Switch switchAlarmView;

    private LiveData<Alarm> alarmLiveData = null;
    private AlarmViewModel viewModel;
    private LifecycleOwner parentLifecycleOwner;
    private Observer<Alarm> alarmObserver;

    AlarmViewHolder(@NonNull View itemView,
                    @NonNull AlarmViewModel _viewModel,
                    @NonNull LifecycleOwner _parentLifecycleOwner) {
        super(itemView);
        viewModel = _viewModel;
        parentLifecycleOwner = _parentLifecycleOwner;

        alarmNameView = itemView.findViewById(R.id.name);
        addressView = itemView.findViewById(R.id.address);
        switchAlarmView = itemView.findViewById(R.id.switch_alarm);

        switchAlarmView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (alarmLiveData == null) {
                Log.e(TAG, "Checked change on empty ViewHolder");
                return;
            }

            Alarm alarm = alarmLiveData.getValue();
            if (alarm == null) {
                Log.wtf(TAG, "ViewHolder represents LiveData of null alarm");
                return;
            }

            viewModel.changeAlarm(alarm.getId(), null, null, isChecked);
        });

        alarmObserver = alarm -> {
            if (alarm == null) {
                return;
            }
            alarmNameView.setText(alarm.getName());
            addressView.setText(alarm.getAddress());
            switchAlarmView.setChecked(alarm.getIsActive());
        };
    }

    void setAlarmLiveData(@NonNull LiveData<Alarm> _alarmLiveData) {
        Alarm alarm = _alarmLiveData.getValue();
        if (alarm == null) {
            Log.wtf(TAG, "Trying to setAlarmLiveData with LiveData to null Alarm");
            return;
        }

        if (alarmLiveData != null) {
            alarmLiveData.removeObserver(alarmObserver);
        }
        alarmLiveData = _alarmLiveData;

        alarmNameView.setText(alarm.getName());
        addressView.setText(alarm.getAddress());
        switchAlarmView.setChecked(alarm.getIsActive());

        alarmLiveData.observe(parentLifecycleOwner, alarmObserver);
    }
}
