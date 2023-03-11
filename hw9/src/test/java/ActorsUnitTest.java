import actor.SearchExecutorActor;
import actor.SearchExecutorActor.StartMessage;
import akka.actor.testkit.typed.FishingOutcome;
import akka.actor.testkit.typed.FishingOutcome.Complete$;
import akka.actor.testkit.typed.javadsl.ActorTestKit;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import response.ErrorResponse;
import response.OkResponse;
import response.Response;
import searcher.FailedSearcher;
import searcher.InfiniteSearcher;
import searcher.OkSearcher;
import searcher.Searcher;
import searcher.builder.SearcherBuilderImpl;

public class ActorsUnitTest {

    private static final int DEFAULT_MAX_RESULTS = 0;
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(2);
    private static final String DUMMY = "dummy";
    public ActorTestKit testKit;
    public TestProbe<List<Response>> testProbe;
    public ActorRef<SearchExecutorActor.Message> searcherExecutorRef;

    @BeforeEach
    public void before() {
        testKit = ActorTestKit.create();
    }

    @AfterEach
    public void cleanup() {
        testKit.shutdownTestKit();
    }

    void init(List<Class<? extends Searcher>> searcherClasses, int maxResults, Duration timeout) {
        testProbe = testKit.createTestProbe();

        var searcherBuilder = new SearcherBuilderImpl(null, maxResults, timeout);
        searcherExecutorRef = testKit.spawn(
            SearchExecutorActor.create(searcherClasses, searcherBuilder, timeout),
            "searcher-executor");
    }

    void init(List<Class<? extends Searcher>> searcherClasses, Duration timeout) {
        init(searcherClasses, DEFAULT_MAX_RESULTS, timeout);
    }

    void init(List<Class<? extends Searcher>> searcherClasses) {
        init(searcherClasses, DEFAULT_TIMEOUT);
    }

    void tell() {
        searcherExecutorRef.tell(new StartMessage(DUMMY, testProbe.getRef()));
    }

    void checkResponses(int expectedOk, int expectedFailed) {
        testProbe.fishForMessage(DEFAULT_TIMEOUT.plusSeconds(3), responses -> {
            String errorMessage = null;

            if (responses.size() != (expectedOk + expectedFailed)) {
                errorMessage = String.format("expected %d responses, but found %d",
                    expectedOk + expectedFailed, responses.size());
            } else {
                int counterOk = expectedOk;
                int counterFailed = expectedFailed;

                while (!responses.isEmpty()) {
                    var lastIndex = responses.size() - 1;
                    var response = responses.get(lastIndex);

                    if (response instanceof OkResponse okResponse && okResponse.equals(
                        OkSearcher.STUB_RESPONSE) && counterOk > 0) {
                        --counterOk;
                    } else if (response instanceof ErrorResponse && counterFailed > 0) {
                        --counterFailed;
                    } else {
                        break;
                    }

                    responses.remove(lastIndex);
                }

                if (!(responses.size() == 0 && counterOk == 0 && counterFailed == 0)) {
                    errorMessage = String.format(
                        "responses.size() = %d, counterOk = %d, counterFailed = %d",
                        responses.size(), counterOk, counterFailed);
                }
            }

            return errorMessage == null ? Complete$.MODULE$ : new FishingOutcome.Fail(errorMessage);
        });
    }

    @Test
    public void testAllSearchesOk() {
        init(List.of(OkSearcher.class, OkSearcher.class));

        tell();

        checkResponses(2, 0);
    }

    @Test
    void testSearchNonResponding() {
        init(List.of(OkSearcher.class, InfiniteSearcher.class));

        tell();

        checkResponses(1, 0);
    }

    @Test
    void testSearchErrorResult() {
        init(List.of(FailedSearcher.class));

        tell();

        checkResponses(0, 1);
    }

    @Test
    void testSearchMixedResults() {
        init(List.of(OkSearcher.class, OkSearcher.class, FailedSearcher.class,
            InfiniteSearcher.class, InfiniteSearcher.class));

        tell();

        checkResponses(2, 1);
    }
}
