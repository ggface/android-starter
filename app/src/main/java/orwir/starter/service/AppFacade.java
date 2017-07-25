package orwir.starter.service;

import android.content.Context;
import android.content.Intent;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;
import orwir.starter.logic.api.NewsService;
import orwir.starter.logic.api.UserService;
import orwir.starter.util.ServiceSubscriber;

public class AppFacade {

    public static Single<AppFacade> with(Context context) {
        return Single.<FacadeKeeper.ServiceBinder>create(new ServiceSubscriber<>(context, new Intent(context, FacadeKeeper.class), Context.BIND_AUTO_CREATE))
                .map(FacadeKeeper.ServiceBinder::getAppFacade);
    }

    static class Builder {

        private AppFacade facade = new AppFacade();
        private File cacheDir;
        private int cacheSize;

        public Builder cache(File cache, int size) {
            cacheDir = cache;
            cacheSize = size;
            return this;
        }

        public Builder userService(UserService userService) {
            facade.userService = userService;
            return this;
        }

        public Builder newsService(NewsService newsService) {
            facade.newsService = newsService;
            return this;
        }

        public AppFacade build() {
            //RetrofitNetwork network = new RetrofitNetwork(cacheDir, cacheSize);
            return facade;
        }

    }

    private UserService userService;
    private NewsService newsService;
    private final BehaviorSubject<Boolean> onlineSubject = BehaviorSubject.create();

    private AppFacade() {}

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
