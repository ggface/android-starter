package orwir.starter.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.trello.rxlifecycle2.components.support.RxDialogFragment;

import butterknife.ButterKnife;

public class BaseDialogFragment extends RxDialogFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }

    protected BaseActivity getBaseActivity() {
        if (!isAdded()) {
            throw new IllegalStateException("Fragment has not attached to activity yet");
        }
        return (BaseActivity) getActivity();
    }

}
