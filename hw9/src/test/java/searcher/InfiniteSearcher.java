package searcher;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import response.Response;

public class InfiniteSearcher extends StubSearcher {

    public InfiniteSearcher(
        HttpClient httpClient, int maxResults, Duration maxRequestDuration
    ) {
        super(httpClient, maxResults, maxRequestDuration);
    }

    @Override
    public CompletableFuture<Response> search(String query) {
        return CompletableFuture.supplyAsync(() -> {
            while (true) {
                // emulating situation where server doesn't respond
            }
        });
    }
}
