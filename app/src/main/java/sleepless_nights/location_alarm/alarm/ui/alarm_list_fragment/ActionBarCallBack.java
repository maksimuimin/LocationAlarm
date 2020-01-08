package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;
import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

class ActionBarCallBack implements ActionMode.Callback {

    private AlarmListAdapter listAdapter;
    private AlarmViewModel viewModel;

    ActionBarCallBack(
            AlarmListAdapter listAdapter,
            AlarmViewModel viewModel) {
        this.listAdapter = listAdapter;
        this.viewModel = viewModel;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.delete_alarm) {
            for (Long selectedItemId : listAdapter.selectedItems) {
                Alarm alarm = viewModel.getAlarmLiveDataById(selectedItemId);
                viewModel.deleteAlarm(alarm);
            }

            mode.finish();
        }
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.delete_alarm_cab, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        listAdapter.multiSelect = false;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        listAdapter.actionMode = mode;
        return false;
    }
}