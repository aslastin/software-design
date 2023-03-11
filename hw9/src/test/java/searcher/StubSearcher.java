package searcher;

import java.net.http.HttpClient;
import java.time.Duration;

public abstract class StubSearcher extends Searcher {

    public StubSearcher(
        HttpClient httpClient, int maxResults, Duration maxRequestDuration
    ) {
        super(null, maxResults, maxRequestDuration);
    }

    @Override
    protected String getSearchQueryPrefix() {
        throw new RuntimeException("unexpected call");
    }
}
