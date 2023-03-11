package searcher;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.regex.Pattern;

public class BingSearcher extends Searcher {

    public BingSearcher(
        HttpClient httpClient, int maxResults, Duration maxRequestDuration
    ) {
        super(httpClient, maxResults, maxRequestDuration);
    }

    @Override
    public String getSearchedBy() {
        return "Bing";
    }

    @Override
    protected String getSearchQueryPrefix() {
        return "https://www.bing.com/search?q=";
    }

    @Override
    protected Pattern getResultExtractorPattern() {
        return Pattern.compile(
            "<a target=\"_blank\" href=\".*?\" h=\".*?\">(?<name>.*?)</a>.*?"
                + "<cite>(?<url>.*?)</cite>"
        );
    }
}
