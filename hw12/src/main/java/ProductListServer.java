import instance.Currency;
import instance.Product;
import instance.User;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import io.reactivex.netty.protocol.http.server.HttpServerRequest;
import io.reactivex.netty.protocol.http.server.HttpServerResponse;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import rx.Observable;

public class ProductListServer {

    public static final int PORT = 8080;

    private final ProductListComponent component;

    public ProductListServer(ProductListComponent component) {
        this.component = component;
    }

    public static void main(String[] args) {
        try (var component = new ProductListComponent()) {
            new ProductListServer(component).start();
        }
    }

    public void start() {
        HttpServer
            .newServer(PORT)
            .start(this::handleRequest)
            .awaitShutdown();
    }

    private <I> Map<String, String> getQueryParams(HttpServerRequest<I> req) {
        return req.getQueryParameters().entrySet()
            .stream()
            .collect(Collectors.toMap(Entry::getKey, entry -> entry.getValue().get(0)));
    }

    private <I, O> Observable<Void> handleRequest(
        HttpServerRequest<I> req, HttpServerResponse<O> resp
    ) {
        String path = req.getDecodedPath();
        Map<String, String> queryParams = getQueryParams(req);

        Observable<String> response = switch (path) {
            case "/addUser" -> {
                var id = Long.parseLong(queryParams.get("id"));
                var currencyId = Integer.parseInt(queryParams.get("currencyId"));
                var user = new User(id, currencyId);
                yield component.addUser(user).map(status -> user.toString());
            }
            case "/addProduct" -> {
                var id = Long.parseLong(queryParams.get("id"));
                var name = queryParams.get("name");
                var price = Double.parseDouble("price");
                var currencyId = Integer.parseInt(queryParams.get("currencyId"));
                var product = new Product(id, name, price, currencyId);
                yield component.addProduct(product).map(status -> product.toString());
            }
            case "/userProduct" -> {
                var userId = Long.parseLong(queryParams.get("userId"));
                yield component.getUser(userId)
                    .flatMap(user ->
                        component.getProducts().map(product ->
                            product.toString(Currency.getCurrencyById(user.getCurrencyId()))
                        ));
            }
            case "/currency" -> Observable.from(Currency.values()).map(Currency::toString);
            case "/user" -> component.getUsers().map(User::toString);
            case "/product" -> component.getProducts().map(Product::toString);
            default -> Observable.just("Unsupported query");
        };

        return resp.writeStringAndFlushOnEach(response)
            .onErrorResumeNext(error -> resp.setStatus(HttpResponseStatus.BAD_REQUEST));
    }
}
