package application;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import searcher.BingSearcher;
import searcher.GoogleSearcher;
import searcher.Searcher;
import searcher.YandexSearcher;

public class ArgsParser {

    public static final String SEARCH_ARG = "--search";
    public static final String MAX_RESULTS_ARG = "--max_results";
    public static final String MAX_REQUEST_DURATION_ARG = "--max_request_duration";

    public static final Map<String, Class<? extends Searcher>> SEARCHER_CLASS_BY_NAME = Map.of(
        "Yandex", YandexSearcher.class,
        "Google", GoogleSearcher.class,
        "Bing", BingSearcher.class
    );
    public static final int DEFAULT_MAX_RESULTS = 5;
    public static final Duration DEFAULT_MAX_REQUEST_DURATION = Duration.ofSeconds(5);
    public static final Map<String, Function<Long, Duration>> DURATION_BY_SUFFIX = Map.of(
        "ms", Duration::ofMillis,
        "s", Duration::ofSeconds
    );

    private final Map<String, String> valueByArg;

    public ArgsParser(String[] args) {
        if (args == null) {
            valueByArg = null;
            return;
        }

        valueByArg = new HashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            valueByArg.put(args[i], args[i + 1]);
        }
    }

    public List<Class<? extends Searcher>> getSearchClasses() {
        if (!valueByArg.containsKey(SEARCH_ARG)) {
            return null;
        }

        List<Class<? extends Searcher>> searchClasses = new ArrayList<>();
        for (String searcher : valueByArg.get(SEARCH_ARG).split(",")) {
            if (!SEARCHER_CLASS_BY_NAME.containsKey(searcher)) {
                throw new IllegalArgumentException("unknown searcher: " + searcher);
            }
            searchClasses.add(SEARCHER_CLASS_BY_NAME.get(searcher));
        }

        return searchClasses;
    }

    public Integer getMaxResults() {
        if (!valueByArg.containsKey(MAX_RESULTS_ARG)) {
            return DEFAULT_MAX_RESULTS;
        }

        int maxResults = Integer.parseInt(valueByArg.get(MAX_RESULTS_ARG));
        if (maxResults <= 0) {
            throw new IllegalArgumentException(
                "max results arg must be > 0, but found " + maxResults);
        }

        return maxResults;
    }

    public Duration getMaxRequestDuration() {
        if (!valueByArg.containsKey(MAX_REQUEST_DURATION_ARG)) {
            return DEFAULT_MAX_REQUEST_DURATION;
        }

        String value = valueByArg.get(MAX_REQUEST_DURATION_ARG);
        for (var suffixAndProcessor : DURATION_BY_SUFFIX.entrySet()) {
            if (value.endsWith(suffixAndProcessor.getKey())) {
                var time = Long.parseLong(
                    value.substring(0, value.length() - suffixAndProcessor.getKey().length())
                );
                if (time <= 0) {
                    throw new IllegalArgumentException("time must be > 0, but found " + time);
                }
                return suffixAndProcessor.getValue().apply(time);
            }
        }

        throw new IllegalArgumentException("unknown duration value: " + value);
    }
}
