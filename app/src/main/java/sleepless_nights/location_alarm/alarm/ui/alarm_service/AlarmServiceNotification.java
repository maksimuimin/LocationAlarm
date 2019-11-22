package sleepless_nights.location_alarm.alarm.ui.alarm_service;

import android.app.Notification;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.util.Locale;

class AlarmServiceNotification { //TODO develop
    static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL = "";
    private Notification notification;

    AlarmServiceNotification(@NonNull Context context, int alarmsCount) {
        notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
                .setContentTitle("LocationAlarm")
                .setContentText(buildTextContent(alarmsCount))
                .build();
    }

    Notification getNotification() { return notification; }

    @NonNull
    private String buildTextContent(int alarmsCount) {
        return String.format(Locale.getDefault(), "Active alarms: %d", alarmsCount);
    }
}
