package sleepless_nights.location_alarm.alarm.use_cases.db;

import java.util.ArrayList;
import java.util.List;

import sleepless_nights.location_alarm.alarm.Alarm;

public class AlarmEntityAdapter {


    public static Alarm adapt(AlarmEntity alarmEntity) {
        Alarm res = new Alarm();
        res.setId(alarmEntity.id);
        res.setName(alarmEntity.name);
        res.setLatitude(alarmEntity.latitude);
        res.setLongitude(alarmEntity.longitude);
        res.setActive(alarmEntity.active);
        res.setAddress(alarmEntity.address);
        return res;
    }

    public static AlarmEntity adapt(Alarm alarm) {
        AlarmEntity res = new AlarmEntity();
        res.id = alarm.getId();
        res.name = alarm.getName();
        res.latitude = alarm.getLatitude();
        res.longitude = alarm.getLongitude();
        res.active = alarm.isActive();
        return res;
    }

    public static List<Alarm> adaptAlarmEntities(List<AlarmEntity> alarmEntities) {
        List<Alarm> res = new ArrayList<>();
        for (AlarmEntity alarmEntity : alarmEntities) {
            res.add(adapt(alarmEntity));
        }
        return res;
    }

    public static List<AlarmEntity> adaptAlarms(List<Alarm> alarms) {
        List<AlarmEntity> res = new ArrayList<>();
        for (Alarm alarm : alarms) {
            res.add(adapt(alarm));
        }
        return res;
    }

}
