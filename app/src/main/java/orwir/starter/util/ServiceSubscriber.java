package orwir.starter.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;

public class ServiceSubscriber<T extends IBinder> implements ObservableOnSubscribe<T> {

    private final Context context;
    private final Intent intent;
    private final int flags;

    public ServiceSubscriber(@NonNull Context context, @NonNull Intent intent, int flags) {
        this.context = context;
        this.intent = intent;
        this.flags = flags;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void subscribe(@NonNull ObservableEmitter<T> emitter) throws Exception {
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder binder) {
                Timber.v("Service '%s' is connected to %s", componentName.getShortClassName(), context.getClass().getSimpleName());
                emitter.onNext((T) binder);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                Timber.v("Service '%s' is disconnected from %s", componentName.getShortClassName(), context.getClass().getSimpleName());
                emitter.onComplete();
            }
        };
        emitter.setDisposable(new Disposable() {
            boolean disposed;
            @Override
            public void dispose() {
                Timber.v("Service unbound from %s", context.getClass().getSimpleName());
                disposed = true;
                context.unbindService(connection);
            }

            @Override
            public boolean isDisposed() {
                return disposed;
            }
        });
        context.startService(intent); //for proper work in a background
        context.bindService(intent, connection, flags);
    }

}
