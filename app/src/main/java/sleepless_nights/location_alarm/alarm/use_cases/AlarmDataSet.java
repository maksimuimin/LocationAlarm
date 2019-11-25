package sleepless_nights.location_alarm.alarm.use_cases;

import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;

import sleepless_nights.location_alarm.alarm.Alarm;

import java.util.List;

public class AlarmDataSet {
    private static final String TAG = "AlarmDataSet";
    private SparseArray<MutableLiveData<Alarm>> dataSet = new SparseArray<>();

    AlarmDataSet(@NonNull List<? extends Alarm> alarms) {
        for (Alarm alarm : alarms) {
            dataSet.put(alarm.getId(), new MutableLiveData<>(alarm));
        }
    }

    @Nullable
    LiveData<Alarm> getAlarmLiveDataById(int id) {
        return dataSet.get(id, null);
    }

    @Nullable
    LiveData<Alarm> getAlarmLiveDataByPosition(int pos) {
        return dataSet.get(dataSet.keyAt(pos), null);
    }

    void addAlarm(@NonNull Alarm alarm) {
        dataSet.put(alarm.getId(), new MutableLiveData<>(alarm));
    }

    void removeAlarm(Alarm alarm) {
        dataSet.remove(alarm.getId());
    }

    void updateAlarm(Alarm alarm) {
        MutableLiveData<Alarm> alarmLiveData = dataSet.get(alarm.getId(), null);
        if (alarmLiveData == null) {
            Log.e(TAG, "Requested update of not existing alarm");
            return;
        }
        alarmLiveData.postValue(alarm);
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
            LiveData<Alarm> oldItem = oldDataSet.getAlarmLiveDataByPosition(oldItemPosition);
            LiveData<Alarm> newItem = newDataSet.getAlarmLiveDataByPosition(newItemPosition);
            if(oldItem == null || newItem == null) {
                return false;
            }

            Alarm oldAlarm = oldItem.getValue();
            Alarm newAalrm = newItem.getValue();
            if (oldAlarm == null || newAalrm == null) {
                return false;
            }

            return oldAlarm.getId() == newAalrm.getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            LiveData<Alarm> oldItem = oldDataSet.getAlarmLiveDataByPosition(oldItemPosition);
            LiveData<Alarm> newItem = newDataSet.getAlarmLiveDataByPosition(newItemPosition);
            if(oldItem == null || newItem == null) {
                return false;
            }

            Alarm oldAlarm = oldItem.getValue();
            Alarm newAalrm = newItem.getValue();
            if (oldAlarm == null || newAalrm == null) {
                return false;
            }

            return oldAlarm.equals(newAalrm);
        }
    }
}
