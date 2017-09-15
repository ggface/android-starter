package orwir.starter.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.File;

public class FacadeKeeper extends Service {

    public class ServiceBinder extends Binder {

        public AppServices getAppFacade() {
            return facade;
        }
    }

    private AppServices facade;
    private final ServiceBinder binder = new ServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        facade = new AppServices.Builder()
                .cache(new File(getExternalCacheDir(), "network-cache"), 10 * 1024 * 1024) //10 Mb
                .build();

        watchNetworkState();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void watchNetworkState() {
        //https://developer.android.com/topic/performance/background-optimization.html
        registerReceiver(new NetworkChangeReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }
}
