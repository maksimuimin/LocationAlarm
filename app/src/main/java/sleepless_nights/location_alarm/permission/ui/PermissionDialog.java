package sleepless_nights.location_alarm.permission.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import sleepless_nights.location_alarm.R;
import sleepless_nights.location_alarm.permission.Permission;
import sleepless_nights.location_alarm.permission.use_cases.PermissionRequest;

public class PermissionDialog {
    private AlertDialog dialog;
    private String msgIntro;
    private PermissionRequest request;

    public PermissionDialog(@NonNull Context context, PermissionRequest request) {
        msgIntro = context.getString(R.string.permission_dialog_msg_intro);
        this.request = request;
        dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.permissions_dialog_title)
                .setMessage(buildDialogMessage())
                .setCancelable(false)
                .setPositiveButton(R.string.dialog_positive_button,
                        (dialog, which) -> request.show())
                .create();
    }

    @NonNull
    private String buildDialogMessage() {
        StringBuilder msgBuilder = new StringBuilder(msgIntro);
        for (Permission permission : request.getPermissions()) {
            String reason = permission.whyRequired();
            msgBuilder.append("\n- ").append(reason);
        }
        return msgBuilder.toString();
    }

    public void show() {
        dialog.show();
    }
}
