package orwir.starter.logic.api;


import java.util.Comparator;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.functions.Predicate;
import orwir.starter.logic.model.Article;

public interface NewsService {

    Completable requestNews(Predicate<Article> predicate, Comparator<Article> sort);

    Flowable<List<Article>> news();

}
