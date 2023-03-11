package searcher.builder;

import java.lang.reflect.InvocationTargetException;
import java.net.http.HttpClient;
import java.time.Duration;
import searcher.Searcher;

public class SearcherBuilderImpl implements SearcherBuilder {

    private final HttpClient httpClient;
    private final int maxResults;
    private final Duration maxRequestDuration;

    public SearcherBuilderImpl(
        HttpClient httpClient, int maxResults, Duration maxRequestDuration
    ) {
        this.httpClient = httpClient;
        this.maxResults = maxResults;
        this.maxRequestDuration = maxRequestDuration;
    }

    public Searcher build(Class<? extends Searcher> clazz) {
        try {
            return clazz.getDeclaredConstructor(HttpClient.class, int.class, Duration.class)
                .newInstance(httpClient, maxResults, maxRequestDuration);
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException |
                 IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
