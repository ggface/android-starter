package orwir.starter.service;

import android.content.Context;
import android.content.Intent;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import orwir.starter.logic.network.RetrofitNetwork;
import orwir.starter.util.ServiceSubscriber;

public class AppServices {

    public static Single<AppServices> with(Context context) {
        return Single.<FacadeKeeper.ServiceBinder>create(new ServiceSubscriber<>(context, new Intent(context, FacadeKeeper.class), Context.BIND_AUTO_CREATE))
                .map(FacadeKeeper.ServiceBinder::getAppFacade);
    }

    static class Builder {

        private File cacheDir;
        private int cacheSize;

        public Builder cache(File cache, int size) {
            cacheDir = cache;
            cacheSize = size;
            return this;
        }

        public AppServices build() {
            AppServices context = new AppServices();
            context.network = new RetrofitNetwork(cacheDir, cacheSize);
            return context;
        }

    }

    private RetrofitNetwork network;
    private final BehaviorSubject<Boolean> onlineSubject = BehaviorSubject.create();

    private AppServices() {}

    public Observable<Boolean> online() {
        return onlineSubject.distinctUntilChanged();
    }

    void setOnline(boolean online) {
        onlineSubject.onNext(online);
    }

}
