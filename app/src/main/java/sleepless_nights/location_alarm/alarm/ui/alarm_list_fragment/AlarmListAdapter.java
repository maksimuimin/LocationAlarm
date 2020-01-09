package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.util.Log;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private static final String TAG = "AlarmListAdapter";
    private AlarmDataSet alarmDataSet;
    private AlarmViewModel viewModel;

    private ArrayList<Long> selectedItems = new ArrayList<>();
    private boolean selectMode;
    private ActionMode actionMode;

    AlarmListAdapter(@NonNull AlarmDataSet alarmDataSet,
                     @NonNull AlarmViewModel viewModel) {
        this.alarmDataSet = alarmDataSet;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.alarm_item, parent, false);

        return new AlarmViewHolder(view, viewModel, this);
    }

    @Override
    public void onBindViewHolder(@NonNull final AlarmViewHolder holder, int position) {
        Alarm alarm = viewModel.getAlarmByPosition(position);
        if (alarm == null) {
            Log.wtf(TAG, "Trying to bindViewHolder with null Alarm");
            return;
        }

        holder.setAlarm(alarm);
        holder.setViewMode(selectMode);
    }

    ActionMode getActionMode() { return actionMode; }
    void setActionMode(ActionMode actionMode) { this.actionMode = actionMode; }

    ArrayList<Long> getSelectedItems() { return selectedItems; }
    void clearSelectedItems() { this.selectedItems.clear(); }

    void updateHolders() { notifyDataSetChanged(); }

    void setSelectMode(boolean selectMode) { this.selectMode = selectMode; }
    boolean isSelectMode() { return selectMode; }

    @Override
    public int getItemCount() { return alarmDataSet.size(); }

    void setAlarmDataSet(AlarmDataSet alarmDataSet) { this.alarmDataSet = alarmDataSet; }
    AlarmDataSet getAlarmDataSet() { return alarmDataSet; }
}
