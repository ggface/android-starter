package orwir.starter.ui;


import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import orwir.starter.R;
import orwir.starter.service.AppFacade;
import orwir.starter.ui.base.BaseActivity;

public class LoginActivity extends BaseActivity {

    @BindView(R.id.username) EditText username;
    @BindView(R.id.password) EditText password;

    @Override
    protected int getContentId() {
        return R.layout.container;
    }

    @Override
    protected int getInnerContentId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        swipe.setEnabled(false);

        username.setText("username");
        password.setText("password");
    }

    @OnClick(R.id.login)
    void login() {
        login(username.getText().toString(), password.getText().toString());
    }

    @OnClick(R.id.anonumous)
    void anonymous() {
        login("anonymous", "anonymous");
    }

    private void login(String username, String password) {
        progress.setVisibility(View.VISIBLE);
        AppFacade.with(this)
                .flatMapCompletable(facade -> facade.getUserService().login(username, password))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    progress.setVisibility(View.GONE);
                    NewsActivity.startActivity(this);
                }, e -> {
                    progress.setVisibility(View.GONE);
                    @StringRes int message = (e instanceof IllegalArgumentException) ? R.string.incorrect_username_or_password : R.string.oops_something_wrong;
                    Snackbar.make(vscroll, message, Snackbar.LENGTH_LONG).show();
                });
    }

}
