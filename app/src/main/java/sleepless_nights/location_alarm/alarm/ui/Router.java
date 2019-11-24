package sleepless_nights.location_alarm.alarm.ui;

import java.util.Objects;

import sleepless_nights.location_alarm.alarm.Alarm;

public class Router {

    public interface Callback {
        void showAlarms();
        void showAlarmCreation();
        void showAlarmDetails(Alarm alarm);
    }

    private static Callback callback;

    public static void setCallback(Callback callback) {
        Router.callback = callback;
    }

    public static void removeCallback(Callback callback) {
        if (Objects.equals(Router.callback, callback)) {
            Router.callback = null;
        }
    }


    public static void showAlarms() {
        if (Router.callback != null) {
            Router.callback.showAlarms();
        }
    }

    public static void showAlarmCreation() {
        if (Router.callback != null) {
            Router.callback.showAlarmCreation();
        }
    }

    public static void showAlarmDetails(Alarm alarm) {
        if (Router.callback != null) {
            Router.callback.showAlarmDetails(alarm);
        }
    }

}
