package sleepless_nights.location_alarm.permission;

import androidx.annotation.NonNull;

public class Permission {
    private String permission;
    private String whyRequired;

    public enum Group {
        // Группа - основной индикатор permission'ов в репозитории
        // Если нам нужен permission для конкретной фичи, то заводим для неё новую группу
        MUST_HAVE // Без permission'а с такой группой невозможна нормальная работа приложения
    }

    public Permission(@NonNull String permission, @NonNull String whyRequired) {
        this.permission = permission;
        this.whyRequired = whyRequired;
    }

    @NonNull
    public String getPermission() { return permission; }

    @NonNull
    public String whyRequired() { return whyRequired; }
}
