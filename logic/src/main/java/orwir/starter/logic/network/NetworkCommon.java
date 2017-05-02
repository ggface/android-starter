package orwir.starter.logic.network;

import com.google.gson.Gson;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;

public interface NetworkCommon {

    Publisher<?> retryPolicy(Flowable<? extends Throwable> errors);

    Gson getGson();

}
