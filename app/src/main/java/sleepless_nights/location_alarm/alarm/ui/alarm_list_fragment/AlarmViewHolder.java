package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

class AlarmViewHolder extends RecyclerView.ViewHolder {
    private final TextView alarmNameView;
    private final TextView addressView;
    private final Switch switchAlarmView;

    private Alarm alarm = null;

    AlarmViewHolder(@NonNull View itemView,
                    @NonNull AlarmViewModel viewModel) {
        super(itemView);

        alarmNameView = itemView.findViewById(R.id.name);
        addressView = itemView.findViewById(R.id.address);
        switchAlarmView = itemView.findViewById(R.id.switch_alarm);

        switchAlarmView.setOnCheckedChangeListener((buttonView, isChecked) -> {

            alarm.setIsActive(isChecked);
            viewModel.updateAlarm(alarm);
        });
    }

    void setAlarm(@NonNull Alarm alarm) {
        this.alarm = alarm;
        alarmNameView.setText(alarm.getName());
        addressView.setText(alarm.getAddress());
        switchAlarmView.setChecked(alarm.getIsActive());
    }
}
