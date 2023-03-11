package searcher;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import response.OkResponse;
import response.OkResponse.Result;
import response.Response;

public abstract class Searcher {

    protected final HttpClient httpClient;
    protected final int maxResults;
    protected final Duration maxRequestDuration;
    private final Pattern resultExtractorPattern;

    public Searcher(HttpClient httpClient, int maxResults, Duration maxRequestDuration) {
        this.httpClient = httpClient;
        this.maxResults = maxResults;
        this.maxRequestDuration = maxRequestDuration;
        this.resultExtractorPattern = getResultExtractorPattern();
    }

    public CompletableFuture<Response> search(String query) {
        return httpClient.sendAsync(makeHttpRequestBuilder(query).build(),
                BodyHandlers.ofString(StandardCharsets.UTF_8))
            .thenApply(response -> {
                if (response.statusCode() != 200) {
                    throw new IllegalArgumentException(
                        String.format("status code(%d) != OK", response.statusCode()));
                }
                return extractResults(response.body())
                    .collect(Collectors.collectingAndThen(Collectors.toList(),
                        results -> new OkResponse(getSearchedBy(), results)
                    ));
            });
    }

    protected HttpRequest.Builder makeHttpRequestBuilder(String query) {
        String url = getSearchQueryPrefix() + URLEncoder.encode(query, StandardCharsets.UTF_8);
        return HttpRequest.newBuilder(URI.create(url))
            .timeout(maxRequestDuration)
            .GET();
    }

    protected Stream<Result> extractResults(String body) {
        Matcher matcher = resultExtractorPattern.matcher(body);

        List<Result> results = new ArrayList<>();
        List<Result> candidates = new ArrayList<>();

        while (matcher.find() && results.size() < maxResults) {
            String name = matcher.group("name");
            String url = matcher.group("url");

            var processedName = processMatched(name);
            var result = new OkResponse.Result(processedName, processMatched(url));

            // try not to add results with unreadable name
            if (processedName.replaceAll("[:,\\s-\\.;]", "").isBlank()) {
                if (results.size() + candidates.size() < maxResults) {
                    candidates.add(result);
                }
            } else {
                results.add(result);
            }
        }

        while (!candidates.isEmpty() && results.size() != maxResults) {
            var last = candidates.size() - 1;
            results.add(candidates.get(last));
            candidates.remove(last);
        }

        return results.stream();
    }

    private String processMatched(String matched) {
        return matched
            .replaceAll("(<.*?>)|(&.*?;)", "")
            .replaceAll("\\s+", " ");
    }

    public String getSearchedBy() {
        return this.getClass().getSimpleName();
    }

    protected abstract String getSearchQueryPrefix();

    protected Pattern getResultExtractorPattern() {
        return null;
    }
}
