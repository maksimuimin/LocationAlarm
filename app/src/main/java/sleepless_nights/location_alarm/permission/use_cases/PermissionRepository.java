package sleepless_nights.location_alarm.permission.use_cases;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import sleepless_nights.location_alarm.LocationAlarmApplication;
import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.permission.Permission;
import sleepless_nights.location_alarm.permission.ui.PermissionDialog;

public class PermissionRepository {
    private static final String TAG = "PermissionRepository";
    private HashMap<Permission.Group, List<Permission>> permissionsMap;
    private int permissionRequestIdSource = 0;
    private Context context;

    public PermissionRepository(Context context) {
        this.context = context;
        permissionsMap = new HashMap<>();
        for (Permission.Group group : Permission.Group.values()) {
            permissionsMap.put(group, new ArrayList<>());
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Objects.requireNonNull(permissionsMap.get(Permission.Group.MUST_HAVE))
                    .add(new Permission(Manifest.permission.ACCESS_FINE_LOCATION,
                            context.getString(R.string.access_fine_location_permission_description)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Objects.requireNonNull(permissionsMap.get(Permission.Group.MUST_HAVE))
                    .add(new Permission(Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                            context.getString(R.string.access_background_location_permission_description)));
        }
    }

    @NonNull
    public static PermissionRepository getInstance(Context context) {
        return LocationAlarmApplication.from(context).getPermissionRepository();
    }

    @Nullable
    public Integer requirePermissionsByGroup(Activity activity, Permission.Group group) {
        List<Permission> notGrantedPermissions = getNotGrantedPermissionsByGroup(group);
        if (notGrantedPermissions == null) return null;
        int permissionRequestId = getPermissionRequestId();
        PermissionRequest permissionRequest =
                new PermissionRequest(activity, notGrantedPermissions, permissionRequestId);
        if (permissionRequest.shouldShowRequestPermissionRationale()) {
            new PermissionDialog(activity, permissionRequest).show();
        } else {
            permissionRequest.show();
        }
        return permissionRequestId;
    }

    @Nullable
    private List<Permission> getNotGrantedPermissionsByGroup(Permission.Group group) {
        List<Permission> permissions = permissionsMap.get(group);
        if (permissions == null) {
            Log.wtf(TAG, "Required unknown group of permissions: " + group.toString());
            return null;
        }
        ArrayList<Permission> notGrantedPermissions = new ArrayList<>();
        for (Permission permission : permissions) {
            if (!isPermissionGranted(permission)) notGrantedPermissions.add(permission);
        }
        return notGrantedPermissions.isEmpty() ? null : notGrantedPermissions;
    }

    private boolean isPermissionGranted(Permission permission) {
        return ContextCompat.checkSelfPermission(
                context.getApplicationContext(),
                permission.getPermission()) == PackageManager.PERMISSION_GRANTED;
    }

    private int getPermissionRequestId() {
        permissionRequestIdSource++;
        return permissionRequestIdSource;
    }
}
