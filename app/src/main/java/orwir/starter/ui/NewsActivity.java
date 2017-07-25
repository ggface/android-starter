package orwir.starter.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.Arrays;
import java.util.Comparator;

import butterknife.BindView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Predicate;
import orwir.starter.R;
import orwir.starter.logic.model.Article;
import orwir.starter.service.AppFacade;
import orwir.starter.ui.base.BaseActivity;


public class NewsActivity extends BaseActivity {

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, NewsActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.search) EditText search;
    @BindView(R.id.sort) Spinner sort;
    @BindView(R.id.news) RecyclerView news;

    @Override
    protected int getContentId() {
        return R.layout.container;
    }

    @Override
    protected int getInnerContentId() {
        return R.layout.activity_news;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NewsAdapter adapter = new NewsAdapter();
        news.setAdapter(adapter);
        news.setLayoutManager(new LinearLayoutManager(this));
        news.setNestedScrollingEnabled(false);

        sort.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                Arrays.asList(getString(R.string.newest), getString(R.string.oldest))));

        AppFacade.with(this)
                .flatMapPublisher(facade -> facade.getNewsService().news())
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adapter::setItems);

        Flowable<Predicate<Article>> predicateFlowable = RxTextView.afterTextChangeEvents(search)
                .map(event -> event.editable().toString())
                .map(this::queryPredicate)
                .toFlowable(BackpressureStrategy.LATEST);

        Flowable<Comparator<Article>> sortFlowable = Flowable.create(e -> {
            sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    e.onNext((o1, o2) -> o1.getDate().compareTo(o2.getDate()) * (position == 0 ? -1 : 1));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }, BackpressureStrategy.LATEST);

        Flowable.combineLatest(predicateFlowable.compose(bindToLifecycle()), sortFlowable.compose(bindToLifecycle()), Pair::new)
                .compose(bindToLifecycle())
                .subscribe(pair -> requestNews(pair.first, pair.second));

    }

    private void requestNews(Predicate<Article> predicate, Comparator<Article> sort) {
        progress.setVisibility(View.VISIBLE);
        AppFacade.with(this)
                .flatMapCompletable(facade -> facade.getNewsService().requestNews(predicate, sort))
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> progress.setVisibility(View.GONE), e -> {
                    progress.setVisibility(View.GONE);
                    Snackbar.make(vscroll, R.string.oops_something_wrong, Snackbar.LENGTH_LONG).show();
                });
    }

    private Predicate<Article> queryPredicate(String query) {
        return article -> article.getTitle().toLowerCase().contains(query.toLowerCase())
                || article.getDescription().toLowerCase().contains(query.toLowerCase())
                || article.getContent().toLowerCase().contains(query.toLowerCase());

    }

}
