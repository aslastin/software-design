package ru.aslastin;

import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.concurrent.TimeUnit.HOURS;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static ru.aslastin.Main.buildTweetsChart;

class BuildTweetsChartTest {
    private final static long START_TIME = 24 * 60 * 60;
    private final static long END_TIME = 2 * START_TIME;

    @Test
    void singleColumn() {
        var tweetsDates = List.of(START_TIME - 5, START_TIME + 1, START_TIME + 2, END_TIME + 5);
        assertThat(buildTweetsChart(tweetsDates, START_TIME, START_TIME + HOURS.toSeconds(2)))
                .containsExactly(0, 2);
    }

    @Test
    void multipleColumns() {
        var tweetsDates = List.of(
                START_TIME,
                START_TIME + HOURS.toSeconds(2) + 1,
                START_TIME + HOURS.toSeconds(3),
                END_TIME + 20
        );

        assertThat(buildTweetsChart(tweetsDates, START_TIME, START_TIME + HOURS.toSeconds(4)))
                .containsExactly(0, 2, 0, 1);
    }

}
