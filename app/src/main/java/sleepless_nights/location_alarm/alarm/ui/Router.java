package sleepless_nights.location_alarm.alarm.ui;

public interface Router {

    void showAlarmList();

    void showAllAlarms();

    void showAlarm(long id);

    void editAlarm(long id);

}
