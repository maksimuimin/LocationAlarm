package com.example.locationalarm.alarm.use_cases;

import androidx.annotation.NonNull;

import com.example.locationalarm.alarm.Alarm;

import java.util.ArrayList;

public class AlarmDataSet {
    private ArrayList<Alarm> alarms;
    private ArrayList<AlarmDataSetUpdate> updates;

    public AlarmDataSet(@NonNull ArrayList<Alarm> _alarms) {
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

    public void insertAlarm(Alarm alarm) {
        alarms.add(alarm);
        updates.add(new AlarmDataSetUpdate(alarms.size() - 1, AlarmDataSetUpdateType.INSERT));
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
