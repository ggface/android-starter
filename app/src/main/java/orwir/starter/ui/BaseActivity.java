package orwir.starter.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.ViewStubCompat;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import orwir.starter.R;
import orwir.starter.service.AppFacade;
import orwir.starter.util.PermUtils;
import timber.log.Timber;

public abstract class BaseActivity extends RxAppCompatActivity {

    @BindView(R.id.toolbar) @Nullable protected Toolbar toolbar;
    @BindView(R.id.swipe) @Nullable protected SwipeRefreshLayout swipe;
    @BindView(R.id.vscroll) @Nullable protected NestedScrollView vscroll;
    private Snackbar noInternet;
    private final Map<Integer, PermUtils.RequestedAction> requestedActions = new ConcurrentHashMap<>();

    /**
     Do not use it directly. Use {@link PermUtils#executeWithPermission(BaseActivity, String, int, io.reactivex.functions.Action)}.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        synchronized (requestedActions) { //cuz concurrent collection protected only collection - not handling
            if (requestedActions.containsKey(requestCode)) {
                PermUtils.RequestedAction action = requestedActions.remove(requestCode);
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
     Do not use it directly. Use {@link PermUtils#executeWithPermission(BaseActivity, String, int, io.reactivex.functions.Action)}.
     */
    public void addRequestedAction(PermUtils.RequestedAction action) {
        requestedActions.put(action.requestCode, action);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentId());
        if (getContentId() == R.layout.container && getInnerContentId() != -1) {
            ViewStubCompat container = (ViewStubCompat) findViewById(R.id.stub);
            container.setLayoutResource(getInnerContentId());
            container.inflate();
        }
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }
        noInternet = Snackbar.make(findViewById(android.R.id.content), R.string.no_internet, Snackbar.LENGTH_INDEFINITE);
        AppFacade.with(this)
                .flatMapObservable(AppFacade::online)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(online -> {
                    if (online) {
                        noInternet.dismiss();
                    } else {
                        noInternet.show();
                    }
                });
    }

    @LayoutRes
    protected abstract int getContentId();

    @LayoutRes
    protected int getInnerContentId() {
        return -1; //stub
    }

}
