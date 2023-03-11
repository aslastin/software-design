package searcher;

import java.net.http.HttpClient;
import java.net.http.HttpRequest.Builder;
import java.time.Duration;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import response.OkResponse.Result;

public class GoogleSearcher extends Searcher {

    private static final String URL_CLASS = "fuLhoc ZWRArf";
    private static final String NAME_CLASS = "CVA68e qXLe6d fuLhoc ZWRArf";

    public GoogleSearcher(
        HttpClient httpClient, int maxResults, Duration maxRequestDuration
    ) {
        super(httpClient, maxResults, maxRequestDuration);
    }

    @Override
    public String getSearchedBy() {
        return "Google";
    }

    @Override
    protected String getSearchQueryPrefix() {
        return "https://www.google.com/search?q=";
    }

    @Override
    protected Pattern getResultExtractorPattern() {
        return Pattern.compile(
            "<a class=\"" + URL_CLASS + "\" href=\"/url\\?q=(?<url>.*?)&amp;.*?\">"
                + "<span class=\"" + NAME_CLASS + "\">(?<name>.*?)</span>"
        );
    }

    @Override
    protected Stream<Result> extractResults(String body) {
        return super.extractResults(body);
    }

    @Override
    protected Builder makeHttpRequestBuilder(String query) {
        return super.makeHttpRequestBuilder(query).header("User-Agent", "Mozilla/4.0");
    }
}
