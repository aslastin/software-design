package searcher;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import response.Response;

public class FailedSearcher extends StubSearcher {

    public FailedSearcher(
        HttpClient httpClient, int maxResults, Duration maxRequestDuration
    ) {
        super(httpClient, maxResults, maxRequestDuration);
    }

    @Override
    public CompletableFuture<Response> search(String query) {
        return CompletableFuture.failedFuture(new RuntimeException("search failed"));
    }
}
