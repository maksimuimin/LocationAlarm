package sleepless_nights.location_alarm.alarm.use_cases;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ListUpdateCallback;
import sleepless_nights.location_alarm.alarm.Alarm;

abstract public class AlarmDataSetUpdate implements ListUpdateCallback {
    private static final String TAG = "AlarmDataSetUpdate";

    private AlarmDataSet updatedAlarmDataSet;

    public AlarmDataSetUpdate(AlarmDataSet updatedAlarmDataSet) {
        this.updatedAlarmDataSet = updatedAlarmDataSet;
    }

    @Override
    public void onInserted(int position, int count) {
        for (int i = position; i < count; i++) {
            Alarm alarm = updatedAlarmDataSet.getAlarmByPosition(i);
            if (alarm == null) {
                Log.wtf(TAG, "updAlarmDataSet doesn't contain inserted alarm");
                continue;
            }
            onInserted(alarm);
        }
    }

    abstract public void onInserted(Alarm alarm);

    @Override
    public void onRemoved(int position, int count) {
        for (int i = position; i < count; i++) {
            Alarm alarm = updatedAlarmDataSet.getAlarmByPosition(position);
            if (alarm == null) {
                Log.wtf(TAG, "activeAlarmsDataSet doesn't contain inserted alarm");
                continue;
            }
            onRemoved(alarm);
        }
    }

    abstract public void onRemoved(Alarm alarm);

    @Override
    public void onMoved(int fromPosition, int toPosition) {
        //alarms can'be moved for now
    }

    @Override
    public void onChanged(int position, int count, @Nullable Object payload) {
        for (int i = position; i < count; i++) {
            Alarm alarm = updatedAlarmDataSet.getAlarmByPosition(position);
            if (alarm == null) {
                Log.wtf(TAG, "activeAlarmsDataSet doesn't contain changed alarm");
                continue;
            }
            onChanged(alarm);
        }
    }

    abstract public void onChanged(Alarm alarm);

}
