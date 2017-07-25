package orwir.starter.logic.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class Lists {

    public static <IN, OUT> List<OUT> transform(Collection<IN> source, Function<IN, OUT> transformer) {
        List<OUT> result = new ArrayList<>(source.size());
        for (IN val : source) {
            try {
                result.add(transformer.apply(val));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    @SafeVarargs
    public static <T> List<T> merge(Collection<T>... sources) {
        List<T> result = new ArrayList<>();
        for (Collection<T> collection : sources) {
            result.addAll(collection);
        }
        return result;
    }

    public static <T> List<T> filter(Collection<T> input, Predicate<T> filter) {
        if (input == null || input.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> result = new ArrayList<>(input.size());
        for (T elem : input) {
            try {
                if (filter.test(elem)) {
                    result.add(elem);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    private Lists() {}

}
