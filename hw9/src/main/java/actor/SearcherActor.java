package actor;

import actor.SearcherActor.SearchMessage;
import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import response.ErrorResponse;
import response.Response;
import searcher.Searcher;
import searcher.builder.SearcherBuilder;

public class SearcherActor extends AbstractBehavior<SearchMessage> {

    private final SearcherBuilder searcherBuilder;
    private final Class<? extends Searcher> searcherClass;

    private SearcherActor(
        ActorContext<SearchMessage> context,
        SearcherBuilder searcherBuilder,
        Class<? extends Searcher> searcherClass
    ) {
        super(context);
        this.searcherBuilder = searcherBuilder;
        this.searcherClass = searcherClass;
    }

    public static Behavior<SearchMessage> create(
        SearcherBuilder searcherBuilder, Class<? extends Searcher> searcherClass
    ) {
        return Behaviors.setup(context ->
            new SearcherActor(context, searcherBuilder, searcherClass)
        );
    }

    @Override
    public Receive<SearchMessage> createReceive() {
        return newReceiveBuilder()
            .onMessage(SearchRequest.class, this::onSearchRequest)
            .onMessage(SearchResult.class, this::onSearchResult)
            .build();
    }

    private Behavior<SearchMessage> onSearchRequest(SearchRequest searchRequest) {
        Searcher searcher = searcherBuilder.build(searcherClass);
        getContext().pipeToSelf(
            searcher.search(searchRequest.query),
            (ok, exc) -> new SearchResult(
                exc == null ? ok : new ErrorResponse(searcher.getSearchedBy(), exc.getMessage()),
                searchRequest.replyTo)
        );
        return this;
    }

    private Behavior<SearchMessage> onSearchResult(SearchResult searchResult) {
        searchResult.replyTo.tell(searchResult.response);
        return this;
    }

    public interface SearchMessage {}

    public record SearchRequest(String query, ActorRef<Response> replyTo) implements
        SearchMessage {}

    public record SearchResult(Response response, ActorRef<Response> replyTo) implements
        SearchMessage {}

}
