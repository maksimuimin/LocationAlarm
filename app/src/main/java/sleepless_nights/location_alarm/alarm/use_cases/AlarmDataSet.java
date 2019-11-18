package sleepless_nights.location_alarm.alarm.use_cases;

import android.util.Log;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.DiffUtil;

import sleepless_nights.location_alarm.alarm.Alarm;

import java.util.ArrayList;

public class AlarmDataSet {
    private static final String TAG = "AlarmDataSet";
    private SparseArray<MutableLiveData<Alarm>> dataSet = new SparseArray<>();

    AlarmDataSet() {}
    AlarmDataSet(@NonNull ArrayList<Alarm> alarms) {
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

    void removeAlarm(int id) {
        dataSet.remove(id);
    }

    void changeAlarm(int id, @Nullable String name,
                     @Nullable String address, @Nullable Boolean isActive) {
        MutableLiveData<Alarm> alarmLiveData = dataSet.get(id, null);
        if (alarmLiveData == null) {
            Log.e(TAG, "Requested change of not existing alarm");
            return;
        }

        Alarm alarm = dataSet.get(id).getValue();
        if (alarm == null) {
            Log.wtf(TAG, "dataSet contains LiveData to null Alarm");
            return;
        }

        if (name != null) alarm.setName(name);
        if (address != null) alarm.setAddress(address);
        if (isActive != null) alarm.setIsActive(isActive);
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

        AlarmDataSetDiffUtilCallback(AlarmDataSet _oldDataSet, AlarmDataSet _newDataSet) {
            oldDataSet = _oldDataSet;
            newDataSet = _newDataSet;
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
