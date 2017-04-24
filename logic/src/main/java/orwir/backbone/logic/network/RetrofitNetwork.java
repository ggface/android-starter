package orwir.backbone.logic.network;

import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import orwir.backbone.logic.BuildConfig;
import rx.Observable;
import timber.log.Timber;

public class RetrofitNetwork implements NetworkCommon {

    private final Gson gson;

    public RetrofitNetwork(File cacheDir, int cacheSize) {
        gson = new GsonBuilder().create();

        HttpLoggingInterceptor logger = new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        //workaround for http://stackoverflow.com/questions/39133437/sslhandshakeexception-handshake-failed-on-android-n-7-0
        //affected version 6.0.0 and 7.0.0
        List<ConnectionSpec> connectionSpecs = Arrays.asList(
                new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS).allEnabledCipherSuites().build(),
                ConnectionSpec.COMPATIBLE_TLS,
                ConnectionSpec.CLEARTEXT);

        Cache cache = null;
        if (cacheDir != null && cacheSize > 0) {
            cache = new Cache(cacheDir, cacheSize);
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logger)
                .cache(cache)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .connectionSpecs(connectionSpecs)
                .build();

        /*remote = new Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.SERVER_ADDRESS)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build()
                .create(Interface.class);*/
    }

    @Override
    public Observable<?> retryPolicy(Observable<? extends Throwable> errors) {
        return errors.zipWith(Observable.range(1, 2), Pair::new).flatMap(pair -> {
            Throwable error = pair.first;
            int tryCount = pair.second;
            if (tryCount > 1) {
                return Observable.error(error);
            }
            Timber.w("Request retry: %d, reason: %s", tryCount, error.getMessage());
            return Observable.timer(1, TimeUnit.SECONDS);
        });
    }

    @Override
    public Gson getGson() {
        return gson;
    }

}
