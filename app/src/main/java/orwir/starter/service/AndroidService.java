package orwir.starter.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;

public class AndroidService extends Service {

    public class ServiceBinder extends Binder {

        public AppFacade getAppFacade() {
            return facade;
        }
    }

    private AppFacade facade;
    private final ServiceBinder binder = new ServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //Timber.v("Service '%s' was created", getClass().getSimpleName());
        facade = new AppFacade.Builder()
                .cache(new File(getExternalCacheDir(), "network-cache"), 10 * 1024 * 1024) //10 Mb
                .build();

        watchNetworkState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Timber.v("Service '%s' was destroyed", getClass().getSimpleName());
    }

    private void watchNetworkState() {
        //https://developer.android.com/topic/performance/background-optimization.html
        registerReceiver(new NetworkChangeReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
}
