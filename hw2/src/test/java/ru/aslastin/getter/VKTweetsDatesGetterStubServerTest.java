package ru.aslastin.getter;

import com.xebialabs.restito.builder.stub.StubWithCondition;
import com.xebialabs.restito.server.StubServer;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.jupiter.api.Test;
import ru.aslastin.getter.data.VKTweetsDatesGetterData;

import java.util.List;
import java.util.function.Consumer;

import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.*;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static ru.aslastin.getter.VKTweetsDatesGetter.MAX_COUNT;
import static ru.aslastin.getter.VKTweetsDatesGetter.VERSION;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VKTweetsDatesGetterStubServerTest {
    private static final int PORT = 12345;
    private static final String ACCESS_TOKEN = "12345";
    private static final String SERVER_URL = "http://localhost:" + PORT;

    private static final long START_TIME = 123;
    private static final long END_TIME = 456;

    private final VKTweetsDatesGetter vkTweetsDatesGetter = new VKTweetsDatesGetter(ACCESS_TOKEN, SERVER_URL);

    @Test
    void singleQuery() {
        withStubServer(s -> {
            requestCondition(s, "#test1")
                    .then(stringContent(VKTweetsDatesGetterData.JSON_TEST1_RESPONSE));

            assertThat(getTweetsDates("#test1"))
                    .containsExactlyInAnyOrder(123L, 124L);
        });
    }

    @Test
    void multipleQueries() {
        withStubServer(s -> {
            requestCondition(s, "#test2")
                    .then(stringContent(VKTweetsDatesGetterData.JSON_TEST2_RESPONSE1));

            requestCondition(s, "#test2", "3/225886670_4251")
                    .then(stringContent(VKTweetsDatesGetterData.JSON_TEST2_RESPONSE2));

            assertThat(getTweetsDates("#test2"))
                    .containsExactlyInAnyOrder(200L, 201L, 202L, 203L);
        });
    }

    @Test
    void badResponse() {
        assertThrows(RuntimeException.class, () -> withStubServer(s -> {
            requestCondition(s, "#bad")
                    .then(status(HttpStatus.BAD_GATEWAY_502));

            getTweetsDates("#bad");
        }));
    }

    static StubWithCondition requestCondition(StubServer stubServer, String hashtag, String startFrom) {
        return whenHttp(stubServer)
                .match(method(Method.GET), startsWithUri("/method/newsfeed.search"),
                        parameter("q", hashtag),
                        parameter("start_time", Long.toString(START_TIME)),
                        parameter("end_time", Long.toString(END_TIME)),
                        parameter("access_token", ACCESS_TOKEN),
                        parameter("count", Integer.toString(MAX_COUNT)),
                        parameter("v", VERSION),
                        startFrom == null ? alwaysTrue() : parameter("start_from", startFrom)
                );
    }

    static StubWithCondition requestCondition(StubServer stubServer, String hashtag) {
        return requestCondition(stubServer, hashtag, null);
    }

    List<Long> getTweetsDates(String hashtag) {
        return vkTweetsDatesGetter.getTweetsDates(hashtag, START_TIME, END_TIME);
    }

    private static void withStubServer(Consumer<StubServer> callback) {
        StubServer stubServer = null;
        try {
            stubServer = new StubServer(PORT).run();
            callback.accept(stubServer);
        } finally {
            if (stubServer != null) {
                stubServer.stop();
            }
        }
    }
}
