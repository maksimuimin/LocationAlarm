package sleepless_nights.location_alarm.alarm.use_cases.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {AlarmEntity.class}, version = 1)
abstract public class AlarmDb extends RoomDatabase {
    abstract public AlarmDao alarmDao();
}
