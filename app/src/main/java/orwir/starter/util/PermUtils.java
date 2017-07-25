package orwir.starter.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import java.util.Arrays;

import io.reactivex.functions.Action;
import orwir.starter.ui.base.BaseActivity;
import timber.log.Timber;

public class PermUtils {

    public static final String[] GEO_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    public static class RequestedAction {
        public final int requestCode;
        public final String[] permissions;
        public final Action action;

        RequestedAction(int requestCode, Action action, String... permissions) {
            this.permissions = permissions;
            this.requestCode = requestCode;
            this.action = action;
        }
    }

    public static void executeWithPermission(BaseActivity context, String permission, int requestCode, Action action) {
        if (ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            Timber.d("permission '%s' already granted.", permission);
            try {
                action.run();
            } catch (Exception e) {
                Timber.e(e.getMessage());
            }
        } else {
            Timber.d("permission '%s' not granted. Request it.");
            context.addRequestedAction(new RequestedAction(requestCode, action, permission));
            ActivityCompat.requestPermissions(context, new String[]{permission}, requestCode);
        }
    }

    public static void executeWithPermission(BaseActivity context, String[] permissions, int requestCode, Action action) {
        if (isPermissionGranted(context, permissions)) {
            Timber.d("permissions '%s' already granted.", Arrays.toString(permissions));
            try {
                action.run();
            } catch (Exception e) {
                Timber.e(e.getMessage());
            }
        } else {
            Timber.d("permissions '%s' not granted. Request it.", Arrays.toString(permissions));
            context.addRequestedAction(new RequestedAction(requestCode, action, permissions));
            ActivityCompat.requestPermissions(context, permissions, requestCode);
        }
    }

    public static boolean isPermissionGranted(@NonNull String[] requestedPermissions, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for (String permission : requestedPermissions) {
            boolean granted = false;
            for (int i = 0; i < permissions.length; i++) {
                if (permission.equals(permissions[i]) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    granted = true;
                }
            }
            if (!granted) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPermissionGranted(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private PermUtils() {}
}
