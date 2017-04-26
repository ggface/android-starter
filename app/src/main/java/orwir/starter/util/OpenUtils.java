package orwir.starter.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Pair;
import android.view.View;

public class OpenUtils {

    @SafeVarargs
    public static void startWithTransition(@NonNull Context context, @NonNull Intent intent, @Nullable Pair<View, String>... sharedElements) {
        if (context instanceof Activity) {
            startWithTransition(context, intent, sharedElements);
        } else {
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    public static void startWithTransition(@NonNull Context context, @NonNull Intent intent, int enterAnim, int exitAnim) {
        if (context instanceof Activity) {
            startWithTransition(context, intent, enterAnim, exitAnim);
        } else {
            context.startActivity(intent);
        }
    }

    public static void startWithTransition(@NonNull Activity caller, @NonNull Intent intent, int enterAnim, int exitAnim) {
        try {
            caller.startActivity(intent, ActivityOptionsCompat.makeCustomAnimation(caller, enterAnim, exitAnim).toBundle());
        } catch (Exception e) {
            caller.startActivity(intent);
        }
    }

    /*@SafeVarargs
    public static void startWithTransition(@NonNull Activity caller, @NonNull Intent intent, @Nullable Pair<View, String>... sharedElements) {
        try {
            caller.startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(caller, sharedElements).toBundle());
        } catch (Exception e) {
            caller.startActivity(intent);
        }
    }*/

    private OpenUtils() {}
}
