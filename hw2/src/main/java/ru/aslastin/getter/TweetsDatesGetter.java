package ru.aslastin.getter;

import java.util.List;

public interface TweetsDatesGetter {
    List<Long> getTweetsDates(String hashtag, long startTime, long endTime);
}
