package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.graphics.Color;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
            if (!listAdapter.isSelectMode()) {
                ActionBarCallBack actionBarCallBack = new ActionBarCallBack(listAdapter, viewModel);
                ((AppCompatActivity)v.getContext()).startSupportActionMode(actionBarCallBack);

                listAdapter.setSelectMode(true);
                listAdapter.updateHolders();
            }
            
            selectItem(v);
            return true;
        });

        alarmItemView.setOnClickListener(this::selectItem);
        selectAlarmView.setOnClickListener(this::selectItem);

        switchAlarmView.setOnCheckedChangeListener((buttonView, isChecked) -> {
            alarm.setIsActive(isChecked);
            viewModel.updateAlarm(alarm);
        });
    }

    private void selectItem(View view) {
        if (!listAdapter.isSelectMode()) {
            return;
        }

        ArrayList<Long> selectedItems = listAdapter.getSelectedItems();
        Long item = alarm.getId();
        AppCompatActivity activity = (AppCompatActivity)view.getContext();

        if (selectedItems.contains(item)) {
            selectedItems.remove(item);
            selectAlarmView.setChecked(false);
            alarmItemView.setBackgroundColor(Color.WHITE);
        } else {
            selectedItems.add(item);
            selectAlarmView.setChecked(true);
            alarmItemView.setBackgroundColor(Color.LTGRAY);
        }

        if (listAdapter.getActionMode() == null) {
            return;
        }

        int selectedItemsCount = selectedItems.size();
        String title = selectedItemsCount + activity.getString(R.string.selected);
        if (selectedItemsCount == 0) {
            title = activity.getString(R.string.select_items);
        }
        listAdapter.getActionMode().setTitle(title);
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
