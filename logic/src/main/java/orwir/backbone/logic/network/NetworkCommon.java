package orwir.backbone.logic.network;

import com.google.gson.Gson;

import io.reactivex.Observable;

public interface NetworkCommon {

    Observable<?> retryPolicy(Observable<? extends Throwable> errors);

    Gson getGson();

}
