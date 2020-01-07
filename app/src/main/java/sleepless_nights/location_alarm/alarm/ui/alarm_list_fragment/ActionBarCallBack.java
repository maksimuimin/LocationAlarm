package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;
import sleepless_nights.location_alarm.R;

class ActionBarCallBack implements ActionMode.Callback {

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.delete_alarm_cab, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // TODO Auto-generated method stub
        mode.setTitle("CheckBox is Checked");
        return false;
    }
}