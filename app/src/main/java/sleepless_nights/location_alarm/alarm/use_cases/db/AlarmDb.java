package sleepless_nights.location_alarm.alarm.use_cases.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AlarmEntity.class}, version = 2)
abstract public class AlarmDb extends RoomDatabase {
    public static final String DB_NAME = "alarms";
    abstract public AlarmDao alarmDao();
}
