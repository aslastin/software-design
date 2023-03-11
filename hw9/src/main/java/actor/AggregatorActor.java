package actor;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

// https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html
public class AggregatorActor<Reply, Aggregate, Context> extends
    AbstractBehavior<AggregatorActor.Command> {

    private final int expectedReplies;
    private final ActorRef<Aggregate> replyTo;
    private final Function<List<Reply>, Aggregate> aggregateReplies;
    private final List<Reply> replies = new ArrayList<>();

    private AggregatorActor(
        ActorContext<Command> context,
        Class<Reply> replyClass,
        BiConsumer<ActorRef<Reply>, ActorContext<Context>> sendRequests,
        int expectedReplies,
        ActorRef<Aggregate> replyTo,
        Function<List<Reply>, Aggregate> aggregateReplies,
        Duration timeout,
        ActorContext<Context> spawnContext
    ) {
        super(context);
        this.expectedReplies = expectedReplies;
        this.replyTo = replyTo;
        this.aggregateReplies = aggregateReplies;

        context.setReceiveTimeout(timeout, ReceiveTimeout.INSTANCE);

        ActorRef<Reply> replyAdapter = context.messageAdapter(replyClass, WrappedReply::new);
        sendRequests.accept(replyAdapter, spawnContext);
    }

    public static <R, A, Ctx> Behavior<Command> create(
        Class<R> replyClass,
        BiConsumer<ActorRef<R>, ActorContext<Ctx>> sendRequests,
        int expectedReplies,
        ActorRef<A> replyTo,
        Function<List<R>, A> aggregateReplies,
        Duration timeout,
        ActorContext<Ctx> spawnContext
    ) {
        return Behaviors.setup(context ->
            new AggregatorActor<>(
                context,
                replyClass,
                sendRequests,
                expectedReplies,
                replyTo,
                aggregateReplies,
                timeout,
                spawnContext)
        );
    }

    public static <R, A> Behavior<Command> create(
        Class<R> replyClass,
        BiConsumer<ActorRef<R>, ActorContext<Command>> sendRequests,
        int expectedReplies,
        ActorRef<A> replyTo,
        Function<List<R>, A> aggregateReplies,
        Duration timeout
    ) {
        return Behaviors.setup(context ->
            new AggregatorActor<>(
                context,
                replyClass,
                sendRequests,
                expectedReplies,
                replyTo,
                aggregateReplies,
                timeout,
                context)
        );
    }

    @Override
    public Receive<Command> createReceive() {
        return newReceiveBuilder()
            .onMessage(WrappedReply.class, this::onReply)
            .onMessage(ReceiveTimeout.class, notUsed -> onReceiveTimeout())
            .build();
    }

    private Behavior<Command> onReply(WrappedReply wrappedReply) {
        Reply reply = wrappedReply.reply;
        replies.add(reply);
        return replies.size() == expectedReplies ? aggregateAndSendResult() : this;
    }

    private Behavior<Command> onReceiveTimeout() {
        return aggregateAndSendResult();
    }

    private Behavior<Command> aggregateAndSendResult() {
        Aggregate result = aggregateReplies.apply(replies);
        replyTo.tell(result);
        return Behaviors.stopped();
    }

    private enum ReceiveTimeout implements Command {
        INSTANCE

    }

    interface Command {}

    private class WrappedReply implements Command {

        final Reply reply;

        public WrappedReply(Reply reply) {
            this.reply = reply;
        }
    }

}
