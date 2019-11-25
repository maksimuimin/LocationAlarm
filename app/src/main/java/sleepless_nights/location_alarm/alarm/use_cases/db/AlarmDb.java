package sleepless_nights.location_alarm.alarm.use_cases.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {AlarmEntity.class}, version = 2)
abstract public class AlarmDb extends RoomDatabase {
    public static final String DB_NAME = "alarms";
    abstract public AlarmDao alarmDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE " + DB_NAME + " ADD COLUMN radius REAL");
        }
    };
}
