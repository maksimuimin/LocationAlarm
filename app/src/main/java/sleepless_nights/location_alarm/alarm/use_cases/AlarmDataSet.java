package sleepless_nights.location_alarm.alarm.use_cases;

import android.util.Log;
import android.util.LongSparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import sleepless_nights.location_alarm.alarm.Alarm;

public class AlarmDataSet implements Iterable<Alarm> {
    private static final String TAG = "AlarmDataSet";
    private LongSparseArray<Alarm> dataSet = new LongSparseArray<>();

    public AlarmDataSet() {}

    private AlarmDataSet(AlarmDataSet alarmDataSet) {
        Log.d(TAG, String.format(Locale.getDefault(),
                "on copy, new alarmDataSet size: %d, old alarmDataSet size: %d",
                this.size(), alarmDataSet.size()));
        this.dataSet = new LongSparseArray<>(alarmDataSet.size());
        for (int i = 0; i < alarmDataSet.size(); i++) {
            Alarm alarm = alarmDataSet.getAlarmByPosition(i);
            if (alarm == null) {
                Log.wtf(TAG, String.format(Locale.getDefault(),
                        "got null Alarm on position %d while making a copy", i));
                continue;
            }
            this.dataSet.put(alarm.getId(), new Alarm(alarm));
        }
    }

    AlarmDataSet(@NonNull List<Alarm> alarms) {
        for (Alarm alarm : alarms) {
            dataSet.put(alarm.getId(), alarm);
        }
    }

    @NonNull
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public AlarmDataSet clone() { return new AlarmDataSet(this); }

    @Nullable
    Alarm getAlarmById(long id) {
        return dataSet.get(id, null);
    }

    @Nullable
    public Alarm getAlarmByPosition(int pos) {
        return dataSet.get(dataSet.keyAt(pos), null);
    }

    void createAlarm(@NonNull Alarm alarm) {
        dataSet.put(alarm.getId(), alarm);
    }

    void deleteAlarm(@NonNull Alarm alarm) {
        dataSet.remove(alarm.getId());
    }

    void updateAlarm(@NonNull Alarm alarm) {
        if (dataSet.get(alarm.getId(), null) == null) {
            return;
        }
        dataSet.put(alarm.getId(), alarm);
    }

    public int size() { return dataSet.size(); }

    public boolean isEmpty() { return dataSet.size() == 0; }

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

    @NonNull
    @Override
    public Iterator<Alarm> iterator() {
        return new Iterator<Alarm>() {
            int position = 0;

            @Override
            public boolean hasNext() {
                return position != dataSet.size();
            }

            @Override
            public Alarm next() {
                Alarm alarm = getAlarmByPosition(position);
                position++;
                return alarm;
            }
        };
    }
}
