package orwir.starter.service;

import android.content.Context;
import android.content.Intent;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import orwir.starter.logic.api.NewsService;
import orwir.starter.logic.api.UserService;
import orwir.starter.logic.network.RetrofitNetwork;
import orwir.starter.util.ServiceSubscriber;

public class AppServices {

    public static Single<AppServices> with(Context context) {
        return Single.<FacadeKeeper.ServiceBinder>create(new ServiceSubscriber<>(context, new Intent(context, FacadeKeeper.class), Context.BIND_AUTO_CREATE))
                .map(FacadeKeeper.ServiceBinder::getAppFacade);
    }

    static class Builder {

        private AppServices services = new AppServices();
        private File cacheDir;
        private int cacheSize;

        public Builder cache(File cache, int size) {
            cacheDir = cache;
            cacheSize = size;
            return this;
        }

        public Builder userService(UserService userService) {
            services.userService = userService;
            return this;
        }

        public Builder newsService(NewsService newsService) {
            services.newsService = newsService;
            return this;
        }

        public AppServices build() {
            AppServices context = new AppServices();
            context.network = new RetrofitNetwork(cacheDir, cacheSize);
            return context;
        }

    }

    private UserService userService;
    private NewsService newsService;
    private RetrofitNetwork network;
    private final BehaviorSubject<Boolean> onlineSubject = BehaviorSubject.create();

    private AppServices() {}

    public Observable<Boolean> online() {
        return onlineSubject.distinctUntilChanged();
    }

    public UserService getUserService() {
        return userService;
    }

    public NewsService getNewsService() {
        return newsService;
    }

    void setOnline(boolean online) {
        onlineSubject.onNext(online);
    }

}
