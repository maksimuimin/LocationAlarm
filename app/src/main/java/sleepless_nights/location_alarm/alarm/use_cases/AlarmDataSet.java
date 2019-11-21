package sleepless_nights.location_alarm.alarm.use_cases;

import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import sleepless_nights.location_alarm.alarm.Alarm;

import java.util.List;

public class AlarmDataSet {
    private SparseArray<Alarm> dataSet = new SparseArray<>();

    public AlarmDataSet() {}
    AlarmDataSet(@NonNull List<Alarm> alarms) {
        for (Alarm alarm : alarms) {
            dataSet.put(alarm.getId(), alarm);
        }
    }

    @Nullable
    Alarm getAlarmById(int id) {
        return dataSet.get(id, null);
    }

    @Nullable
    Alarm getAlarmByPosition(int pos) {
        return dataSet.get(dataSet.keyAt(pos), null);
    }

    void createAlarm(@NonNull Alarm alarm) {
        dataSet.put(alarm.getId(), alarm);
    }

    void deleteAlarm(int id) {
        dataSet.remove(id);
    }

    void updateAlarm(@NonNull Alarm alarm) {
        if (dataSet.get(alarm.getId(), null) == null) {
            return;
        }
        dataSet.put(alarm.getId(), alarm);
    }

    public int size() {
        return dataSet.size();
    }

    @NonNull
    public DiffUtil.DiffResult diffFrom(AlarmDataSet oldDataSet) {
        return DiffUtil.calculateDiff(new AlarmDataSetDiffUtilCallback(oldDataSet, this));
    }

    public class AlarmDataSetDiffUtilCallback extends DiffUtil.Callback {
        private final AlarmDataSet oldDataSet;
        private final AlarmDataSet newDataSet;

        AlarmDataSetDiffUtilCallback(AlarmDataSet oldDataSet, AlarmDataSet newDataSet) {
            this.oldDataSet = oldDataSet;
            this.newDataSet = newDataSet;
        }

        @Override
        public int getOldListSize() {
            return oldDataSet.size();
        }

        @Override
        public int getNewListSize() {
            return newDataSet.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            Alarm oldAlarm = oldDataSet.getAlarmByPosition(oldItemPosition);
            Alarm newAlarm = newDataSet.getAlarmByPosition(newItemPosition);
            if(oldAlarm == null || newAlarm == null) {
                return false;
            }
            return newAlarm.getId() == oldAlarm.getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Alarm oldAlarm = oldDataSet.getAlarmByPosition(oldItemPosition);
            Alarm newAlarm = newDataSet.getAlarmByPosition(newItemPosition);
            if(oldAlarm == null || newAlarm == null) {
                return false;
            }
            return oldAlarm.equals(newAlarm);
        }
    }
}
