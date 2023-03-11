import static com.xebialabs.restito.builder.stub.StubHttp.whenHttp;
import static com.xebialabs.restito.semantics.Action.status;
import static com.xebialabs.restito.semantics.Action.stringContent;
import static com.xebialabs.restito.semantics.Condition.method;
import static com.xebialabs.restito.semantics.Condition.startsWithUri;

import actor.SearchExecutorActor;
import actor.SearchExecutorActor.Message;
import akka.actor.typed.ActorSystem;
import com.xebialabs.restito.semantics.Action;
import com.xebialabs.restito.server.StubServer;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.glassfish.grizzly.http.Method;
import org.glassfish.grizzly.http.util.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import response.OkResponse;
import response.OkResponse.Result;
import response.Response;
import searcher.LocalSearcher;
import searcher.builder.SearcherBuilder;
import searcher.builder.SearcherBuilderImpl;

public class ActorsStubServerTest {

    private static final int TEST_POOL_SIZE = 2;
    private static final int DEFAULT_MAX_RESULTS = 2;
    private static final Duration DEFAULT_MAX_REQUEST_DURATION = Duration.ofSeconds(3);
    private static final Duration DEFAULT_TIMEOUT = DEFAULT_MAX_REQUEST_DURATION.multipliedBy(2);

    private static ActorSystem<Message> searchExecutor;

    private static void withStubServer(Consumer<StubServer> callback) {
        StubServer stubServer = null;
        try {
            stubServer = new StubServer(LocalSearcher.PORT).run();
            callback.accept(stubServer);
        } finally {
            if (stubServer != null) {
                stubServer.stop();
            }
        }
    }

    private static void withStubServerInNewThead(Consumer<StubServer> callback) {
        var thread = new Thread(() -> withStubServer(callback));
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    public static void initSystem() {
        SearcherBuilder searcherBuilder = new SearcherBuilderImpl(
            HttpClient.newBuilder().executor(Executors.newFixedThreadPool(TEST_POOL_SIZE)).build(),
            DEFAULT_MAX_RESULTS, DEFAULT_MAX_REQUEST_DURATION);

        searchExecutor = ActorSystem.create(
            SearchExecutorActor.create(List.of(LocalSearcher.class), searcherBuilder,
                DEFAULT_MAX_REQUEST_DURATION), "search-executor");
    }

    private <T> void searchAndCheck(
        String query, BiConsumer<? super List<Response>, ? super Throwable> checkAction
    ) {
        try {
            SearchExecutorActor.searchQuery(query, searchExecutor, DEFAULT_TIMEOUT)
                .whenComplete(checkAction).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testOkResponse() {
        withStubServerInNewThead(s -> {
            whenHttp(s).match(method(Method.GET), startsWithUri("/search/query"))
                .then(stringContent("ok"));

            searchAndCheck("query", (res, exc) -> {
                Assertions.assertNull(exc);
                Assertions.assertEquals(res.size(), 1);
                if (res.get(0) instanceof OkResponse okResponse) {
                    Assertions.assertEquals(okResponse.getResults(),
                        List.of(new Result("ok", "ok")));
                    return;
                }
                Assertions.fail();
            });
        });
    }

    @Test
    void testNonResponding() {
        withStubServerInNewThead(s -> {
            whenHttp(s).match(method(Method.GET), startsWithUri("/search/query"))
                .then(Action.delay((int) DEFAULT_MAX_REQUEST_DURATION.multipliedBy(2).toMillis()));

            searchAndCheck("query", (res, exc) -> {
                Assertions.assertNotNull(exc);
            });
        });
    }

    @Test
    void testBadResponse() {
        withStubServerInNewThead(s -> {
            whenHttp(s).match(method(Method.GET), startsWithUri("/search/query"))
                .then(status(HttpStatus.FORBIDDEN_403));

            searchAndCheck("query", (res, exc) -> {
                Assertions.assertNotNull(exc);
            });
        });
    }
}
