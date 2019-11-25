package sleepless_nights.location_alarm.alarm.use_cases.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alarm")
public class AlarmEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public String address;

    public boolean active;

    public double latitude;

    public double longitude;

    public float radius;
}
