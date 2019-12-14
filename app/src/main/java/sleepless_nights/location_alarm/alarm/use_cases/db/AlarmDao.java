package sleepless_nights.location_alarm.alarm.use_cases.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface AlarmDao {

    @Query("SELECT * FROM alarm")
    List<AlarmEntity> getAll();

    @Insert(entity = AlarmEntity.class)
    long[] create(AlarmEntity ... alarmEntities);

    @Update
    void update(AlarmEntity ... alarmEntities);

    @Delete(entity = AlarmEntity.class)
    void delete(AlarmEntity ... alarmEntities);

}
