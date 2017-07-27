package orwir.starter.logic.mock;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.schedulers.Schedulers;
import orwir.starter.logic.api.NewsService;
import orwir.starter.logic.model.Article;
import orwir.starter.logic.model.User;
import orwir.starter.logic.util.Lists;


public class MockNewsService implements NewsService {

    private final BehaviorProcessor<List<Article>> newsProcessor = BehaviorProcessor.create();
    private final Flowable<User> userFlowable;

    public MockNewsService(Flowable<User> userFlowable) {
        this.userFlowable = userFlowable;
        userFlowable.distinctUntilChanged().skip(1).subscribe(user -> newsProcessor.onNext(user.isAnonymous() ? anonymousArticles : authoredArticles));
    }

    @Override
    public Completable requestNews(Predicate<Article> predicate, Comparator<Article> sort) {
        return userFlowable.firstElement()
                .flatMapSingle(user -> Single.fromCallable(() -> user.isAnonymous() ? anonymousArticles : authoredArticles))
                .map(news -> {
                    news = Lists.filter(news, predicate);
                    Collections.sort(news, sort);
                    return news;
                })
                .doOnSuccess(newsProcessor::onNext)
                .toCompletable()
                .subscribeOn(Schedulers.io());
    }

    @Override
    public Flowable<List<Article>> news() {
        return newsProcessor.onBackpressureLatest();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static final DateFormat DATE = new SimpleDateFormat("dd.MM.yyyy", Locale.ENGLISH);

    private static Date date(String date) {
        try {
            return DATE.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    private static final List<Article> anonymousArticles = Arrays.asList(
            new Article(1L, "Анонимные анонимы наносят ответный удар", "Группа анонимов анонимнчила в анонимном месте", "Как стало известно анонимным новостям, некие анонимы анонимно собрались в неустановленном месте для проведения ответной акции против проведенной акции за запрет анонимности. Участники отказались давать интервью даже на условиях анонимности.", date("30.07.2017")),
            new Article(2L, "Анонимы это интроверты или экстраверты?", "Ученые выяснили насколько анонимы коммуникативны", "Группа ученых во главе с человеком пожелавшим остаться анонимным провела исследования, в ходе которого респонденты отвечали на ряд вопросов на условиях анонимности с целью выяснить насколько анонимы расположены к общению и раскрытию информации о себе. По словам главы исследования результаты удивили экспертов, но раскрыть их они не могут по условиям анонимности.", date("28.07.2017")),
            new Article(3L, "Анонимы среди нас", "Что делать если нужно выходить на улицу", "", date("26.07.2017")),
            new Article(4L, "Грани приватности", "Журналисты выяснили о чем лучше молчать", "", date("24.07.2017")),
            new Article(5L, "Цена анонимности", "Сколько стоят симки и документы на бомжах", "", date("22.07.2017")),
            new Article(6L, "Случайный деанон", "История из жизни анонима навсегда изменившая его жизнь", "", date("20.07.2017"))
    );

    private static final List<Article> authoredArticles = Arrays.asList(
            new Article(1L, "БББ - биткоины, блокчейн, боль", "В связи с падением курса криптовалют ученые прогнозируют увеличение температуры в ареоле обитания майнеров", "Британские ученые пришли к выводу, что недавнее повышение температуры во многих городах мира связано с резким подъемом, а затем таким же резким спадом курса крипто-валют.", date("30.07.2017")),
            new Article(2L, "Майнеры - кто они", "Россия на пороге очередной волны валютных спекулянтов", "Ведущие экономисты страны предрекают бум криптовалютных спекуляций. Василий Чизкейкович, ведущий экономист Экономико-Аналитического центра России в своем интервью сказал: \"Не исключено, что скоро мы увидим на улицах наших городов молодых людей предлагающих недорого купить немного криптовалюты по QR-коду.\"", date("28.07.2017")),
            new Article(3L, "Бум поддержанных видеокарт", "Сайты по продаже поддержаных вещей наводнили видеокарты", "В последнее время увеличелось количество б/у видеокарт. Как заметили наблюдатели многие из них были куплены недавно, до сих пор на гарантии и как уверяют продавцы ими пользовались только для того чтобы их мамы смогли играть в косынку.", date("26.07.2017")),
            new Article(4L, "Майнинг - кому это выгодно", "Производители видеокарт заявили о рекордных продажах", "", date("24.07.2017")),
            new Article(5L, "Эфир и коины", "Разбираемся в многообразии криптовалют", "", date("22.07.2017")),
            new Article(6L, "Фермер поневоле", "Скрытые майнеры и опасность вареза", "", date("20.07.2017"))
    );
    
}
