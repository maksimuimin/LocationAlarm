package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

class AlarmViewHolder extends RecyclerView.ViewHolder {
    private final TextView alarmNameView;
    private final TextView addressView;
    private final Switch switchAlarmView;
    private final CheckBox selectAlarmView;

    private Alarm alarm = null;
    private View alarmItemView;
    private AlarmListAdapter listAdapter;

    AlarmViewHolder(@NonNull View itemView,
                    @NonNull AlarmViewModel viewModel,
                    AlarmListAdapter adapter) {
        super(itemView);

        alarmItemView = itemView;
        listAdapter = adapter;

        alarmNameView = itemView.findViewById(R.id.name);
        addressView = itemView.findViewById(R.id.address);
        switchAlarmView = itemView.findViewById(R.id.switch_alarm);
        selectAlarmView = itemView.findViewById(R.id.select_alarm);

        alarmItemView.setOnLongClickListener(v -> {
            ActionBarCallBack actionBarCallBack = new ActionBarCallBack(listAdapter, viewModel);
            AppCompatActivity activity = (AppCompatActivity)v.getContext();
            activity.startSupportActionMode(actionBarCallBack);

            listAdapter.selectMode = true;
            listAdapter.notifyDataSetChanged();
            selectItem(alarm.getId());

            return true;
        });

        alarmItemView.setOnClickListener(v -> selectItem(alarm.getId()));
        selectAlarmView.setOnClickListener(v -> selectItem(alarm.getId()));

        switchAlarmView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setIsActive(isChecked);
            viewModel.updateAlarm(alarm);
        });
    }

    private void selectItem(Long item) {
        if (!listAdapter.selectMode) {
            return;
        }

        if (listAdapter.selectedItems.contains(item)) {
            listAdapter.selectedItems.remove(item);
            selectAlarmView.setChecked(false);
            alarmItemView.setBackgroundColor(Color.WHITE);
        } else {
            listAdapter.selectedItems.add(item);
            selectAlarmView.setChecked(true);
            alarmItemView.setBackgroundColor(Color.LTGRAY);
        }

        if (listAdapter.actionMode == null) {
            return;
        }

        int selectedItemsCount = listAdapter.selectedItems.size();
        String title = selectedItemsCount + ": selected";
        if (selectedItemsCount == 0) {
            title = "Select items";
        }

        listAdapter.actionMode.setTitle(title);
    }

    void setAlarm(@NonNull Alarm alarm) {
        this.alarm = alarm;
        alarmNameView.setText(alarm.getName());
        addressView.setText(alarm.getAddress());
        switchAlarmView.setChecked(alarm.getIsActive());
    }

    void setViewMode(boolean selectMode) {
        if (selectMode) {
            selectAlarmView.setVisibility(View.VISIBLE);
            switchAlarmView.setVisibility(View.GONE);
            return;
        }

        alarmItemView.setBackgroundColor(Color.WHITE);
        selectAlarmView.setChecked(false);
        selectAlarmView.setVisibility(View.GONE);
        switchAlarmView.setVisibility(View.VISIBLE);
    }
}
