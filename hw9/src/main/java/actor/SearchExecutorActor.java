package actor;

import actor.AggregatorActor.Command;
import actor.SearchExecutorActor.Message;
import actor.SearcherActor.SearchRequest;
import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.AskPattern;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import response.Response;
import searcher.Searcher;
import searcher.builder.SearcherBuilder;

public class SearchExecutorActor extends AbstractBehavior<Message> {

    final Map<Long, ActorRef<List<Response>>> recipientRefMap;
    private final List<Class<? extends Searcher>> searcherClasses;
    private final SearcherBuilder searcherBuilder;
    private final Duration timeout;
    Long requestId;

    private SearchExecutorActor(
        ActorContext<Message> context,
        List<Class<? extends Searcher>> searcherClasses,
        SearcherBuilder searcherBuilder,
        Duration timeout
    ) {
        super(context);
        this.searcherBuilder = searcherBuilder;
        this.searcherClasses = searcherClasses;
        this.timeout = timeout;
        requestId = 0L;
        recipientRefMap = new HashMap<>();
    }

    public static Behavior<Message> create(
        List<Class<? extends Searcher>> searcherClasses,
        SearcherBuilder searcherBuilder,
        Duration timeout
    ) {
        return Behaviors.setup(context ->
            new SearchExecutorActor(context, searcherClasses, searcherBuilder, timeout)
        );
    }

    public static CompletableFuture<List<Response>> searchQuery(
        String query, ActorSystem<Message> searchExecutor, Duration timeout
    ) {
        CompletionStage<List<Response>> future = AskPattern.ask(
            searchExecutor,
            replyTo -> new StartMessage(query, replyTo),
            timeout,
            searchExecutor.scheduler()
        );

        return future.exceptionally(exc -> List.of())
            .toCompletableFuture();
    }

    @Override
    public Receive<Message> createReceive() {
        return newReceiveBuilder()
            .onMessage(StartMessage.class, this::onStartMessage)
            .onMessage(EndMessage.class, this::onEndMessage)
            .build();
    }

    private Behavior<Message> onStartMessage(StartMessage startMessage) {
        var id = this.requestId++;
        recipientRefMap.put(id, startMessage.replyTo);

        BiConsumer<ActorRef<Response>, ActorContext<Command>> sendRequests =
            (replyTo, spawnContext) -> IntStream.range(0, searcherClasses.size())
                .mapToObj(ind -> spawnContext.spawn(
                    SearcherActor.create(searcherBuilder, searcherClasses.get(ind)),
                    String.format("%s_%d_%d", searcherClasses.get(ind).getSimpleName(), id, ind)
                )).forEach(
                    searcher -> searcher.tell(new SearchRequest(startMessage.query, replyTo))
                );

        int expectedReplies = searcherClasses.size();

        getContext().spawn(
            AggregatorActor.create(
                Response.class,
                sendRequests,
                expectedReplies,
                getContext().getSelf(),
                responses -> new EndMessage(id, responses),
                timeout
            ),
            "searcher-aggregator-" + id
        );

        return this;
    }

    private Behavior<Message> onEndMessage(EndMessage endMessage) {
        recipientRefMap.get(endMessage.requestId).tell(endMessage.responses);
        recipientRefMap.remove(endMessage.requestId);
        return this;
    }

    public interface Message {}

    public record StartMessage(String query, ActorRef<List<Response>> replyTo) implements Message {}

    public record EndMessage(Long requestId, List<Response> responses) implements Message {}
}
