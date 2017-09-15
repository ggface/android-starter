package orwir.starter.ui.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import orwir.starter.R;
import orwir.starter.service.AppServices;
import orwir.starter.util.PermUtils;
import timber.log.Timber;

public abstract class BaseActivity extends RxAppCompatActivity {

    @BindView(R.id.toolbar) @Nullable protected Toolbar toolbar;
    @BindView(R.id.swipe) @Nullable protected SwipeRefreshLayout swipe;
    @BindView(R.id.vscroll) @Nullable protected NestedScrollView vscroll;
    @BindView(R.id.progress) @Nullable protected ProgressBar progress;
    @BindView(R.id.no_internet) @Nullable protected TextView noInternet;
    private final Map<Integer, PermUtils.RequestedAction> permissionActions = new ConcurrentHashMap<>();
    //private final Map<Integer, BiConsumer<Integer, Intent>> resultActions = new ConcurrentHashMap<>(); // TODO: 2017-07-31 under construct

    /**
     * Do not use it directly. Use {@link PermUtils#executeWithPermission(BaseActivity, String, int, io.reactivex.functions.Action)}.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        synchronized (permissionActions) { //cuz concurrent collection protected only collection - not handling
            if (permissionActions.containsKey(requestCode)) {
                PermUtils.RequestedAction action = permissionActions.remove(requestCode);
                if (PermUtils.isPermissionGranted(action.permissions, permissions, grantResults)) {
                    Timber.d("permission '%s' granted from user.", Arrays.toString(action.permissions));
                    try {
                        action.action.run();
                    } catch (Exception e) {
                        Timber.e(e.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Do not use it directly. Use {@link PermUtils#executeWithPermission(BaseActivity, String, int, io.reactivex.functions.Action)}.
     */
    public void addRequestedAction(PermUtils.RequestedAction action) {
        permissionActions.put(action.requestCode, action);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());
        if (getInnerContentId() != -1) {
            ViewStubCompat container = (ViewStubCompat) findViewById(R.id.stub);
            container.setLayoutResource(getInnerContentId());
            container.inflate();
        }
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        watchSwipeRefresh();
        watchInternetConnection();

        if (savedInstanceState == null) {
            updateScreenData();
        }
    }

    @LayoutRes
    protected abstract int getContentId();

    @LayoutRes
    protected int getInnerContentId() {
        return -1; //stub
    }

    protected void updateScreenData() {} //stub

    protected void watchSwipeRefresh() {
        if (swipe != null) {
            swipe.setOnRefreshListener(() -> {
                swipe.setRefreshing(true);
                updateScreenData();
                Single.timer(2, TimeUnit.SECONDS)
                        .compose(bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(v -> swipe.setRefreshing(false), Timber::e);
            });
        }
    }

    protected void watchInternetConnection() {
        if (noInternet != null) {
            AppServices.with(this)
                    .flatMapObservable(AppServices::online)
                    .distinctUntilChanged()
                    .compose(bindToLifecycle())
                    .subscribe(online -> noInternet.setVisibility(online ? View.GONE : View.VISIBLE), Timber::e);
        }
    }

    protected void setProgress(boolean visible) {
        if (progress != null) {
            progress.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    protected void setSwipeRefresh(boolean refreshing) {
        if (swipe != null) {
            swipe.setRefreshing(refreshing);
        }
    }

}
