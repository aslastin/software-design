package searcher;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.regex.Pattern;

public class YandexSearcher extends Searcher {

    public YandexSearcher(
        HttpClient httpClient, int maxResults, Duration maxRequestDuration
    ) {
        super(httpClient, maxResults, maxRequestDuration);
    }

    @Override
    public String getSearchedBy() {
        return "Yandex";
    }

    @Override
    protected String getSearchQueryPrefix() {
        return "https://yandex.ru/search/?text=";
    }

    @Override
    protected Pattern getResultExtractorPattern() {
        return Pattern.compile(
            "<a class=\".*?OrganicTitle-Link.*?\".*?href=\"(?<url>.*?)\".*?>.*?"
                + "<span class=\".*?OrganicTitleContentSpan.*?\" role=\"text\">(?<name>.*?)</span>"
        );
    }
}
