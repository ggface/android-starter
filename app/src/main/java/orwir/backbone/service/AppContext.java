package orwir.backbone.service;

import android.content.Context;
import android.content.Intent;

import java.io.File;

import io.reactivex.Observable;
import orwir.backbone.logic.network.RetrofitNetwork;
import orwir.backbone.util.ServiceSubscriber;

public class AppContext {

    public static Observable<AppContext> get(Context context) {
        return Observable.<AndroidService.ServiceBinder>create(new ServiceSubscriber<>(context, new Intent(context, AndroidService.class), Context.BIND_AUTO_CREATE))
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

        public AppContext build() {
            AppContext context = new AppContext();
            context.network = new RetrofitNetwork(cacheDir, cacheSize);
            return context;
        }

    }

    private RetrofitNetwork network;

    private AppContext() {}

}
