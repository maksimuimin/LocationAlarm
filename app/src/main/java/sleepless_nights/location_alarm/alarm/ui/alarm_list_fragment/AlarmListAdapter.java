package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.ui.Router;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.view_models.AlarmViewModel;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private static final String TAG = "AlarmListAdapter";
    private AlarmDataSet alarmDataSet;
    private AlarmViewModel viewModel;
    private Router router;

    AlarmListAdapter(@NonNull AlarmDataSet alarmDataSet,
                     @NonNull AlarmViewModel viewModel,
                     Router router) {
        this.alarmDataSet = alarmDataSet;
        this.viewModel = viewModel;
        this.router = router;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.alarm_item, parent, false);

        return new AlarmViewHolder(view, viewModel, router);
    }


    @Override
    public void onBindViewHolder(@NonNull final AlarmViewHolder holder, int position) {
        Alarm alarm = viewModel.getAlarmByPosition(position);
        if (alarm == null) {
            Log.wtf(TAG, "Trying to bindViewHolder with null Alarm");
            return;
        }
        holder.setAlarm(alarm);
    }

    @Override
    public int getItemCount() { return alarmDataSet.size(); }

    void setAlarmDataSet(AlarmDataSet alarmDataSet) { this.alarmDataSet = alarmDataSet; }
    AlarmDataSet getAlarmDataSet() { return alarmDataSet; }
}
