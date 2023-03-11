package searcher;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import response.OkResponse;
import response.OkResponse.Result;
import response.Response;

public class OkSearcher extends StubSearcher {

    public static Response STUB_RESPONSE = new OkResponse(
        "ok-searcher",
        IntStream.range(0, 10)
            .mapToObj(Integer::toString)
            .map(i -> new Result(i, i))
            .collect(Collectors.toList())
    );

    public OkSearcher(
        HttpClient httpClient, int maxResults, Duration maxRequestDuration
    ) {
        super(httpClient, maxResults, maxRequestDuration);
    }

    @Override
    public CompletableFuture<Response> search(String query) {
        return CompletableFuture.completedFuture(STUB_RESPONSE);
    }
}
