package application;

import actor.SearchExecutorActor;
import actor.SearchExecutorActor.Message;
import akka.actor.typed.ActorSystem;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import response.ErrorResponse;
import response.OkResponse;
import response.Response;
import searcher.Searcher;
import searcher.builder.SearcherBuilder;
import searcher.builder.SearcherBuilderImpl;

public class SearchApplication {

    static final int HTTP_CLIENT_POOL_SIZE = 8;
    static final long TIMEOUT_FACTOR = 2;
    static final String TAB1 = " ".repeat(2);
    static final String TAB2 = " ".repeat(4);

    private static void doMain(String[] args) throws Exception {
        var argsParser = new ArgsParser(args);

        List<Class<? extends Searcher>> searchClasses = argsParser.getSearchClasses();
        int maxResults = argsParser.getMaxResults();
        Duration maxRequestDuration = argsParser.getMaxRequestDuration();

        SearcherBuilder searcherBuilder = new SearcherBuilderImpl(
            HttpClient.newBuilder()
                .executor(Executors.newFixedThreadPool(HTTP_CLIENT_POOL_SIZE))
                .build(),
            maxResults,
            maxRequestDuration
        );
        Duration timeout = maxRequestDuration.multipliedBy(searchClasses.size() * TIMEOUT_FACTOR);

        ActorSystem<Message> searchExecutor = ActorSystem.create(
            SearchExecutorActor.create(searchClasses, searcherBuilder, maxRequestDuration),
            "search-executor");

        try (var reader = new BufferedReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            while (true) {
                System.out.print("Enter query to search: ");
                String query = reader.readLine();
                if (query == null || query.isBlank()) {
                    break;
                }

                List<Response> responses =
                    SearchExecutorActor.searchQuery(query, searchExecutor, timeout).get();

                System.out.println(toStringBuilder(responses));
            }

            System.out.println("Stop searching");
        } finally {
            searchExecutor.terminate();
        }
    }

    public static StringBuilder toStringBuilder(List<Response> responses) {
        StringBuilder sb = new StringBuilder();

        if (responses.isEmpty()) {
            sb.append("query timed out");
            return sb;
        }

        for (var response : responses) {
            sb.append('*').append(TAB1).append(response.getSearchedBy()).append(" :");
            if (response instanceof OkResponse okResponse) {
                sb.append('\n');
                for (var result : okResponse.getResults()) {
                    sb.append(TAB2).append('*').append(TAB1)
                        .append(result.name()).append(" - ").append(result.url()).append('\n');
                }
            } else {
                var errorResponse = (ErrorResponse) response;
                sb.append(' ').append(errorResponse.errorMessage()).append('\n');
            }
        }
        sb.append('\n');
        return sb;
    }

    public static void main(String[] args) {
        try {
            doMain(args);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
