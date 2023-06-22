import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.MongoCollection;
import com.mongodb.rx.client.MongoDatabase;
import com.mongodb.rx.client.Success;
import instance.Product;
import instance.User;
import org.bson.Document;
import rx.Observable;

public class ProductListComponent implements AutoCloseable {

    private final MongoClient client;
    private final MongoCollection<Document> productCollection;
    private final MongoCollection<Document> userCollection;

    public ProductListComponent() {
        client = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase db = client.getDatabase("product-list-application");
        productCollection = db.getCollection("products");
        userCollection = db.getCollection("users");
    }

    public Observable<Success> addProduct(Product product) {
        return productCollection.insertOne(product.toDocument());
    }

    public Observable<Success> addUser(User user) {
        return userCollection.insertOne(user.toDocument());
    }

    public Observable<Product> getProducts() {
        return productCollection.find().toObservable().map(Product::new);
    }

    public Observable<User> getUsers() {
        return userCollection.find().toObservable().map(User::new);
    }

    public Observable<User> getUser(Long userId) {
        return getUsers().filter(user -> user.getId().equals(userId));
    }

    @Override
    public void close() {
        client.close();
    }
}
