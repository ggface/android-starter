package orwir.backbone.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import orwir.backbone.R;
import orwir.backbone.util.PermUtils;
import rx.functions.Action0;
import timber.log.Timber;

public abstract class BaseActivity extends RxAppCompatActivity {

    @BindView(R.id.toolbar) @Nullable protected Toolbar toolbar;
    @BindView(R.id.swipe) @Nullable protected SwipeRefreshLayout swipe;
    @BindView(R.id.vscroll) @Nullable protected NestedScrollView vscroll;
    private final Map<Integer, PermUtils.RequestedAction> requestedActions = new ConcurrentHashMap<>();

    /**
     Do not use it directly. Use {@link PermUtils#executeWithPermission(BaseActivity, String, int, Action0)}.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        synchronized (requestedActions) { //cuz concurrent collection protected only collection - not handling
            if (requestedActions.containsKey(requestCode)) {
                PermUtils.RequestedAction action = requestedActions.remove(requestCode);
                if (PermUtils.isPermissionGranted(action.permissions, permissions, grantResults)) {
                    Timber.d("permission '%s' granted from user.", Arrays.toString(action.permissions));
                    action.action.call();
                }
            }
        }
    }

    /**
     Do not use it directly. Use {@link PermUtils#executeWithPermission(BaseActivity, String, int, Action0)}.
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
    }

    @LayoutRes
    protected abstract int getContentId();

    @LayoutRes
    protected int getInnerContentId() {
        return -1; //stub
    }

}
