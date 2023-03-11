package searcher;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.stream.Stream;
import response.OkResponse;
import response.OkResponse.Result;

public class LocalSearcher extends Searcher {

    public static final int PORT = 23456;
    public static final String SERVER_URL = "http://localhost:" + PORT;

    public LocalSearcher(
        HttpClient httpClient, int maxResults, Duration maxRequestDuration
    ) {
        super(httpClient, maxResults, maxRequestDuration);
    }

    @Override
    protected String getSearchQueryPrefix() {
        return SERVER_URL + "/search/";
    }

    @Override
    protected Stream<Result> extractResults(String body) {
        return Stream.of(new OkResponse.Result(body, body));
    }
}
