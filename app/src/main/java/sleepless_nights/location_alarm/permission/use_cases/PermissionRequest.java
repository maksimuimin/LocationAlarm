package sleepless_nights.location_alarm.permission.use_cases;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.List;

import sleepless_nights.location_alarm.permission.Permission;

public class PermissionRequest {
    private static final String TAG = "PermissionRequest";
    private Activity activity;
    private List<Permission> permissions;
    private int requestId;

    PermissionRequest(@NonNull Activity activity, @NonNull List<Permission> permissions,
                      int requestId) {
        this.activity = activity;
        this.permissions = permissions;
        this.requestId = requestId;
    }

    boolean shouldShowRequestPermissionRationale() {
        for (Permission permission : permissions) {
            if (ActivityCompat.
                    shouldShowRequestPermissionRationale(activity,permission.getPermission())) {
                return true;
            }
        }
        return false;
    }

    public void show() {
        if (permissions.size() == 0) {
            Log.wtf(TAG, "trying to request empty list of permissions");
            return;
        }
        String[] manifestPermissions = new String[permissions.size()];
        for (int i = 0; i < permissions.size(); i++) {
            manifestPermissions[i] = permissions.get(i).getPermission();
        }
        ActivityCompat.requestPermissions(activity, manifestPermissions, requestId);
    }

    public List<Permission> getPermissions() {
        return permissions;
    }
}
