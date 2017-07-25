package orwir.starter.logic.api;


import io.reactivex.Completable;
import io.reactivex.Flowable;
import orwir.starter.logic.model.User;

public interface UserService {

    Completable login(String username, String password);

    Flowable<User> user();

}
