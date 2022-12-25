package ru.aslastin.getter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class VKTweetsDatesGetter implements TweetsDatesGetter {
    static final String VERSION = "5.131";
    static final int MAX_COUNT = 200;
    static final String VK_URL = "https://api.vk.com";

    private final String accessToken;
    private final String serverUrl;

    public VKTweetsDatesGetter(String accessToken, String serverUrl) {
        this.accessToken = accessToken;
        this.serverUrl = serverUrl;
    }

    public VKTweetsDatesGetter(String accessToken) {
        this(accessToken, VK_URL);
    }

    @Override
    public List<Long> getTweetsDates(String hashtag, long startTime, long endTime) {
        List<Long> tweetsDates = new ArrayList<>();

        String startFrom = null;
        int oldSize;

        do {
            String requestUrl = makeRequestUrl(hashtag, startTime, endTime, startFrom);
            oldSize = tweetsDates.size();
            startFrom = fillTweetsDatesAndGetNextFrom(tweetsDates, requestUrl);
        } while (tweetsDates.size() - oldSize != 0 && !startFrom.isEmpty());

        return tweetsDates;
    }

    String makeRequestUrl(String hashtag, long startTime, long endTime, String startFrom) {
        String url = serverUrl +
                "/method/newsfeed.search?" +
                "q=" + hashtag.replace("#", "%23") +
                "&start_time=" + startTime +
                "&end_time=" + endTime +
                "&access_token=" + accessToken +
                "&count=" + MAX_COUNT +
                "&v=" + VERSION;

        if (startFrom != null) {
            url += "&start_from=" + startFrom;
        }

        return url;
    }

    private String fillTweetsDatesAndGetNextFrom(List<Long> tweetsDates, String requestUrl) {
        JSONObject serverObject = getJsonServerResponse(requestUrl);

        JSONObject responseObject = serverObject.optJSONObject("response");
        if (responseObject == null) {
            throw new RuntimeException("error response:\n" + serverObject);
        }

        JSONArray items = responseObject.getJSONArray("items");
        for (Object object : items) {
            JSONObject item = (JSONObject) object;
            tweetsDates.add(item.getLong("date"));
        }

        return responseObject.optString("next_from");
    }

    private JSONObject getJsonServerResponse(String requestUrl) {
        try {
            URL url = new URL(requestUrl);
            URLConnection connection = url.openConnection();
            try (var inputStream = connection.getInputStream()) {
                return new JSONObject(new JSONTokener(inputStream));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
