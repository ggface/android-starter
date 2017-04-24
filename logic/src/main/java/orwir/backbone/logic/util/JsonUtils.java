package orwir.backbone.logic.util;

import com.google.gson.JsonElement;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    public static final int MARKERS_COUNT_MAX = 2;

    public static String string(JsonElement json, String path) {
        JsonElement element = path(json, path);
        if (checkNotNull(element)) {
            return element.getAsString();
        }
        return "";
    }

    public static String string(JsonElement json, Object... segments) {
        JsonElement element = segments(json, segments);
        if (checkNotNull(element)) {
            return element.getAsString();
        }
        return "";
    }

    public static int integer(JsonElement json, String path) {
        JsonElement element = path(json, path);
        if (checkNotNull(element)) {
            return element.getAsInt();
        }
        return 0;
    }

    public static int integer(JsonElement json, Object... segments) {
        JsonElement element = segments(json, segments);
        if (checkNotNull(element)) {
            return element.getAsInt();
        }
        return 0;
    }

    public static float real(JsonElement json, String path) {
        JsonElement element = path(json, path);
        if (checkNotNull(element)) {
            return element.getAsFloat();
        }
        return 0;
    }

    public static float real(JsonElement json, Object... segments) {
        JsonElement element = segments(json, segments);
        if (checkNotNull(element)) {
            return element.getAsFloat();
        }
        return 0;
    }

    public static long asLong(JsonElement json, String path) {
        JsonElement element = path(json, path);
        if (checkNotNull(element)) {
            return element.getAsLong();
        }
        return 0;
    }

    public static long asLong(JsonElement json, Object... segments) {
        JsonElement element = segments(json, segments);
        if (checkNotNull(element)) {
            return element.getAsLong();
        }
        return 0;
    }

    public static boolean bool(JsonElement json, String path) {
        JsonElement element = path(json, path);
        return checkNotNull(element) && element.getAsBoolean();
    }

    public static boolean bool(JsonElement json, Object... segments) {
        JsonElement element = segments(json, segments);
        return checkNotNull(element) && element.getAsBoolean();
    }

    public static BigDecimal bigDecimal(JsonElement json, String path) {
        JsonElement element = path(json, path);
        if (checkNotNull(element)) {
            return element.getAsBigDecimal();
        }
        return BigDecimal.ZERO;
    }

    public static BigDecimal bigDecimal(JsonElement json, Object... segments) {
        JsonElement element = segments(json, segments);
        if (checkNotNull(element)) {
            return element.getAsBigDecimal();
        }
        return BigDecimal.ZERO;
    }

    @SuppressWarnings("unchecked")
    public static <T extends JsonElement> T path(JsonElement json, String path) {
        List<Object> segments = new ArrayList<>();
        for (String segment : path.split("\\.")) {
            if (segment.charAt(segment.length() - 1) == ']') {
                segments.add(segment.substring(0, segment.length() - 3));
                segments.add(Character.getNumericValue(segment.charAt(segment.length() - 2)));
            } else {
                segments.add(segment);
            }
        }
        return segments(json, segments.toArray());
    }

    @SuppressWarnings("unchecked")
    public static <T extends JsonElement> T segments(JsonElement json, Object... segments) {
        if (checkNull(json)) {
            return null;
        }
        JsonElement element = json;
        for (Object segment : segments) {
            if (segment instanceof String) {
                element = element.getAsJsonObject().get((String) segment);
            } else if (segment instanceof Integer) {
                element = element.getAsJsonArray().get((Integer) segment);
            }
            if (checkNull(element)) {
                return null;
            }
        }
        return (T) element;
    }

    public static boolean checkNull(JsonElement element) {
        return element == null || element.isJsonNull();
    }

    public static boolean checkNotNull(JsonElement element) {
        return !checkNull(element);
    }

    private JsonUtils() {}
}
