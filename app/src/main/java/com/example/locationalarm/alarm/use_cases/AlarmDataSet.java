package com.example.locationalarm.alarm.use_cases;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.locationalarm.alarm.Alarm;

import java.util.ArrayList;

public class AlarmDataSet {
    private ArrayList<Alarm> alarms;
    private ArrayList<AlarmDataSetUpdate> updates;

    AlarmDataSet(@NonNull ArrayList<Alarm> _alarms) {
        alarms = _alarms;
        updates = new ArrayList<>();
        for (int i = 0; i < alarms.size(); i++) {
            updates.add(new AlarmDataSetUpdate(i, AlarmDataSetUpdateType.INSERT));
        }
    }

    public ArrayList<Alarm> getAlarms() {
        return alarms;
    }

    public ArrayList<AlarmDataSetUpdate> commitUpdates() {
        ArrayList<AlarmDataSetUpdate> updatesToCommit = new ArrayList<>(updates);
        updates.clear();
        return updatesToCommit;
    }

    void insertAlarm(Alarm alarm) {
        alarms.add(alarm);
        updates.add(new AlarmDataSetUpdate(alarms.size() - 1, AlarmDataSetUpdateType.INSERT));
    }

    void changeAlarm(int idx, @Nullable String newName,
                     @Nullable String newAddress, @Nullable Boolean newIsActive) {
        changeAlarmQuietly(idx, newName, newAddress, newIsActive);
        updates.add(new AlarmDataSetUpdate(idx, AlarmDataSetUpdateType.CHANGE));
    }

    void changeAlarmQuietly(int idx, @Nullable String newName,
                            @Nullable String newAddress, @Nullable Boolean newIsActive) {
        if (newName != null) alarms.get(idx).setName(newName);
        if (newAddress != null) alarms.get(idx).setAddress(newAddress);
        if (newIsActive != null) alarms.get(idx).setIsActive(newIsActive);
    }

    void removeAlarm(int idx) {
        alarms.remove(idx);
        updates.add(new AlarmDataSetUpdate(idx, AlarmDataSetUpdateType.REMOVE));
    }

    public class AlarmDataSetUpdate {
        public int idx;
        public AlarmDataSetUpdateType type;

        AlarmDataSetUpdate(int _idx, AlarmDataSetUpdateType _type) {
            idx = _idx;
            type = _type;
        }
    }

    public enum AlarmDataSetUpdateType {
        CHANGE,
        INSERT,
        REMOVE
    }
}
