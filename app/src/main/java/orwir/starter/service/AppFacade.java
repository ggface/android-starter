package orwir.starter.service;

import android.content.Context;
import android.content.Intent;

import java.io.File;

import io.reactivex.Single;
import orwir.starter.logic.network.RetrofitNetwork;
import orwir.starter.util.ServiceSubscriber;

public class AppFacade {

    public static Single<AppFacade> with(Context context) {
        return Single.<AndroidService.ServiceBinder>create(new ServiceSubscriber<>(context, new Intent(context, AndroidService.class), Context.BIND_AUTO_CREATE))
                .map(AndroidService.ServiceBinder::getAppContext);
    }

    static class Builder {

        private File cacheDir;
        private int cacheSize;

        public Builder cache(File cache, int size) {
            cacheDir = cache;
            cacheSize = size;
            return this;
        }

        public AppFacade build() {
            AppFacade context = new AppFacade();
            context.network = new RetrofitNetwork(cacheDir, cacheSize);
            return context;
        }

    }

    private RetrofitNetwork network;

    private AppFacade() {}

}
