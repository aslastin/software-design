package ru.aslastin;

import ru.aslastin.getter.TweetsDatesGetter;
import ru.aslastin.getter.VKTweetsDatesGetter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {

    private Main() {
        // just main method
    }

    private static String extractHashtag(String inputHashtag) {
        if (!inputHashtag.startsWith("#")) {
            throw new RuntimeException("hashtag must starts with #");
        }
        return inputHashtag;
    }

    private static int extractN(String inputN) {
        int n = Integer.parseInt(inputN);
        if (n < 1 || n > 24) {
            throw new RuntimeException("incorrect n, it must be in [1; 24]");
        }
        return n;
    }

    private static String readAccessToken(String file) throws IOException {
        return Files.readString(Path.of(file), StandardCharsets.UTF_8);
    }

    public static List<Integer> buildTweetsChart(List<Long> tweetsDates, long startTime, long endTime) {
        var sortedTweetsDates = new ArrayList<>(tweetsDates);
        Collections.sort(sortedTweetsDates);

        List<Integer> tweetsChart = new ArrayList<>();

        int oldIndex = 0;
        int curIndex = 0;

        final long step = 60 * 60;
        for (long curTime = startTime; curTime < endTime; curTime += step) {
            while (curIndex < sortedTweetsDates.size() && sortedTweetsDates.get(curIndex) <= curTime + step) {
                if (sortedTweetsDates.get(curIndex) < startTime) {
                    oldIndex = ++curIndex;
                } else {
                    ++curIndex;
                }
            }

            tweetsChart.add(curIndex - oldIndex);
            oldIndex = curIndex;
        }

        Collections.reverse(tweetsChart);

        return tweetsChart;
    }

    private static void printTweetsChart(List<Integer> tweetsChart) {
        System.out.println("Tweets chart:");
        for (int index = 0; index < tweetsChart.size(); ++index) {
            int hour = index + 1;
            System.out.println(hour + " : " + tweetsChart.get(index));
        }
    }

    private static void doMain(String inputHashtag, String inputN) throws IOException {
        String hashtag = extractHashtag(inputHashtag);

        int n = extractN(inputN);
        long endTime = System.currentTimeMillis() / 1000;
        long startTime = endTime - n * 60L * 60;

        String accessToken = readAccessToken("hw2/src/main/resources/access_token");
        TweetsDatesGetter tweetsDatesGetter = new VKTweetsDatesGetter(accessToken);
        List<Long> tweetsDates = tweetsDatesGetter.getTweetsDates(hashtag, startTime, endTime);

        List<Integer> tweetsChart = buildTweetsChart(tweetsDates, startTime, endTime);

        printTweetsChart(tweetsChart);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            String msg = """
                    Expected 2 args:  [hashtag] [N], where
                    hashtag: hashtag, which will be used for searching
                    N: the number of hours for which you need to build a tweet chart (1 <= N <= 24)
                    """;
            System.err.println(msg);
            return;
        }

        try {
            doMain(args[0], args[1]);
        } catch (IOException | RuntimeException e) {
            System.err.println(e);
        }
    }
}
