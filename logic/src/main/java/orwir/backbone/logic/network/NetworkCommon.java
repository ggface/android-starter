package orwir.backbone.logic.network;

import com.google.gson.Gson;

import rx.Observable;

public interface NetworkCommon {

    Observable<?> retryPolicy(Observable<? extends Throwable> errors);

    Gson getGson();

}
