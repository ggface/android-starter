package orwir.starter.logic.mock;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;
import orwir.starter.logic.api.UserService;
import orwir.starter.logic.model.User;

public class MockUserService implements UserService {

    private final BehaviorProcessor<User> userProcessor = BehaviorProcessor.create();

    @Override
    public Completable login(String username, String password) {
        return Single.<User>create(e -> {
                    if ("username".equalsIgnoreCase(username) && "password".equals(password)) {
                        e.onSuccess(new User(false));
                    } else if ("anonymous".equalsIgnoreCase(username) && "anonymous".equals(password)) {
                        e.onSuccess(new User(true));
                    } else {
                        e.onError(new IllegalArgumentException("incorrect username/password"));
                    }
                })
                .doOnSuccess(userProcessor::onNext)
                .toCompletable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<User> user() {
        return userProcessor.onBackpressureLatest();
    }

}
