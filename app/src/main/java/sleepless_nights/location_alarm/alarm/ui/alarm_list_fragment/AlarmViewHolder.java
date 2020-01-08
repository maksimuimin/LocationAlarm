package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.ui.IMainActivity;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

class AlarmViewHolder extends RecyclerView.ViewHolder {
    private final TextView alarmNameView;
    private final TextView addressView;
    private final Switch switchAlarmView;

    private Alarm alarm = null;
    private View alarmItemView;
    private AlarmListAdapter listAdapter;

    AlarmViewHolder(@NonNull View itemView,
                    @NonNull AlarmViewModel viewModel,
                    IMainActivity IMainActivity,
                    AlarmListAdapter adapter) {
        super(itemView);

        alarmNameView = itemView.findViewById(R.id.name);
        addressView = itemView.findViewById(R.id.address);
        switchAlarmView = itemView.findViewById(R.id.switch_alarm);
        alarmItemView = itemView;
        listAdapter = adapter;

        alarmItemView.setOnLongClickListener(v -> {
            ActionBarCallBack actionBarCallBack = new ActionBarCallBack(listAdapter, viewModel);
            ((AppCompatActivity)v.getContext()).startSupportActionMode(actionBarCallBack);
            listAdapter.multiSelect = true;
            selectItem(alarm.getId());

            return true;
        });

        alarmItemView.setOnClickListener(v -> selectItem(alarm.getId()));

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

    private void selectItem(Long item) {
        if (listAdapter.multiSelect) {
            if (listAdapter.selectedItems.contains(item)) {
                listAdapter.selectedItems.remove(item);
                alarmItemView.setBackgroundColor(Color.WHITE);
            } else {
                listAdapter.selectedItems.add(item);
                alarmItemView.setBackgroundColor(Color.LTGRAY);
            }
        }
    }

    void setAlarm(@NonNull Alarm alarm) {
        this.alarm = alarm;
        alarmNameView.setText(alarm.getName());
        addressView.setText(alarm.getAddress());
        switchAlarmView.setChecked(alarm.getIsActive());
    }
}
