package orwir.starter.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;

public class AndroidService extends Service {

    public class ServiceBinder extends Binder {

        public AppContext getAppContext() {
            return context;
        }
    }

    private AppContext context;
    private final ServiceBinder binder = new ServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = new AppContext.Builder()
                .cache(new File(getExternalCacheDir(), "network-cache"), 10 * 1024 * 1024) //10 Mb
                .build();
    }
}
