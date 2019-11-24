package sleepless_nights.location_alarm.alarm.use_cases.db;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface AlarmDao {

    @Query("SELECT * FROM alarm")
    List<AlarmEntity> getAll();

    @Insert
    void add(AlarmEntity ... alarmEntities);

    @Update
    void update(AlarmEntity ... alarmEntities);

    @Delete(entity = AlarmEntity.class)
    void remove(AlarmEntity ... alarmEntities);

}
