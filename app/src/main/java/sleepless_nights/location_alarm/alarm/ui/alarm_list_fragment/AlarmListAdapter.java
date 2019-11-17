package sleepless_nights.location_alarm.alarm.ui.alarm_list_fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.locationalarm.R;
import sleepless_nights.location_alarm.alarm.Alarm;
import sleepless_nights.location_alarm.alarm.use_cases.AlarmDataSet;
import sleepless_nights.location_alarm.alarm.view_models.alarm_view_model.AlarmViewModel;

public class AlarmListAdapter extends RecyclerView.Adapter<AlarmViewHolder> {
    private static final String TAG = "AlarmListAdapter";
    private AlarmDataSet alarmDataSet;
    private AlarmViewModel viewModel;
    private LifecycleOwner parentLifecycleOwner;

    AlarmListAdapter(@NonNull AlarmDataSet _alarmDataSet,
                     @NonNull AlarmViewModel _viewModel,
                     @NonNull LifecycleOwner _parentLifecycleOwner) {
        alarmDataSet = _alarmDataSet;
        viewModel = _viewModel;
        parentLifecycleOwner = _parentLifecycleOwner;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.alarm_item, parent, false);

        return new AlarmViewHolder(view, viewModel, parentLifecycleOwner);
    }


    @Override
    public void onBindViewHolder(@NonNull final AlarmViewHolder holder, int position) {
        LiveData<Alarm> alarmLiveData = alarmDataSet.getAlarmLiveDataByPosition(position);
        if (alarmLiveData == null) {
            Log.wtf(TAG, "Trying to bindViewHolder with null LiveData");
            return;
        }
        holder.setAlarmLiveData(alarmLiveData);
    }

    @Override
    public int getItemCount() { return alarmDataSet.size(); }

    void setAlarmDataSet(AlarmDataSet _alarmDataSet) { alarmDataSet = _alarmDataSet; }
    AlarmDataSet getAlarmDataSet() { return alarmDataSet; }
}
