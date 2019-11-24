package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

class AlarmViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "AlarmViewHolder";

    private final TextView alarmNameView;
    private final TextView addressView;
    private final Switch switchAlarmView;

    private final LifecycleOwner parentLifecycleOwner;
    private final Observer<Alarm> alarmObserver;
    private LiveData<Alarm> alarmLiveData = null;

    AlarmViewHolder(@NonNull View itemView,
                    @NonNull AlarmViewModel viewModel,
                    @NonNull LifecycleOwner parentLifecycleOwner) {
        super(itemView);
        this.parentLifecycleOwner = parentLifecycleOwner;

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

            alarm.setActive(isChecked);
            viewModel.updateAlarm(alarm);
        });

        alarmObserver = alarm -> {
            if (alarm == null) {
                return;
            }
            alarmNameView.setText(alarm.getName());
            addressView.setText(alarm.getAddress());
            switchAlarmView.setChecked(alarm.isActive());
        };
    }

    void setAlarmLiveData(@NonNull LiveData<Alarm> alarmLiveData) {
        Alarm alarm = alarmLiveData.getValue();
        if (alarm == null) {
            Log.wtf(TAG, "Trying to setAlarmLiveData with LiveData to null Alarm");
            return;
        }

        if (this.alarmLiveData != null) {
            alarmLiveData.removeObserver(alarmObserver);
        }
        this.alarmLiveData = alarmLiveData;

        alarmNameView.setText(alarm.getName());
        addressView.setText(alarm.getAddress());
        switchAlarmView.setChecked(alarm.isActive());

        alarmLiveData.observe(parentLifecycleOwner, alarmObserver);
    }
}
