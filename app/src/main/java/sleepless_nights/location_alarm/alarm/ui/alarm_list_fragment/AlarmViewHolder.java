package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.ui.IMainActivity;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

class AlarmViewHolder extends RecyclerView.ViewHolder {
    private final TextView alarmNameView;
    private final TextView addressView;
    private final Switch switchAlarmView;

    private Alarm alarm = null;

    AlarmViewHolder(@NonNull View itemView,
                    @NonNull AlarmViewModel viewModel,
                    IMainActivity IMainActivity) {
        super(itemView);

        alarmNameView = itemView.findViewById(R.id.name);
        addressView = itemView.findViewById(R.id.address);
        switchAlarmView = itemView.findViewById(R.id.switch_alarm);

        itemView.setOnLongClickListener(v -> {
            ((AppCompatActivity)v.getContext()).startSupportActionMode(new ActionBarCallBack());
            Toast.makeText(v.getContext(), "settingsBtn is on click", Toast.LENGTH_SHORT).show();
            return true;
        });

        alarmNameView.setOnClickListener(v -> {
            if (alarm != null) {
                IMainActivity.showAlarm(alarm);
            }
        });

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
