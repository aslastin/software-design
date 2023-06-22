package ru.aslastin;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.aslastin.service.ProductService;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProductServiceTest {
    final static int PORT = 8082;
    final static String SERVER_URL = "http://localhost:" + PORT;
    final static String DATABASE_PATH = "jdbc:sqlite:test.db";

    static ProductService productService;

    @BeforeAll
    static void beforeAll() throws Exception {
        productService = new ProductService(PORT, DATABASE_PATH);

        productService.start().get(10, TimeUnit.SECONDS);

        // some extra time for setup
        Thread.sleep(3000);
    }

    @AfterAll
    static void afterAll() throws Exception {
        productService.stop().get(10, TimeUnit.SECONDS);
    }

    ServiceInfo serviceInfo;

    @BeforeEach
    void beforeEach() {
        serviceInfo = new ServiceInfo(SERVER_URL, HttpClient.newHttpClient());

        try (Connection c = DriverManager.getConnection(DATABASE_PATH)) {
            String sql = "DELETE FROM PRODUCT";
            Statement stmt = c.createStatement();

            stmt.executeUpdate(sql);
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Product extractProduct(String line) {
        int separatorIndex = line.indexOf('\t');
        assertNotEquals(-1, separatorIndex);

        String name = line.substring(0, separatorIndex);

        int endIndex = line.indexOf("</br>");
        assertNotEquals(-1, endIndex);

        long price = Long.parseLong(line.substring(separatorIndex + 1, endIndex));

        return new Product(name, price);
    }

    static Optional<Product> extractProductFromResponse(HttpResponse<byte[]> response, String productHeader)
            throws Exception {
        assertEquals(200, response.statusCode());

        var reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(response.body()), StandardCharsets.UTF_8)
        );

        assertEquals("<html><body>", reader.readLine());
        assertEquals(productHeader, reader.readLine());

        Optional<Product> result = Optional.empty();

        String line = reader.readLine();
        if (!"</body></html>".equals(line)) {
            result = Optional.of(extractProduct(line));
            line = reader.readLine();
        }

        assertEquals("</body></html>", line);

        assertNull(reader.readLine());

        return result;
    }

    static long extractLongFromResponse(HttpResponse<byte[]> response, String header) throws Exception {
        assertEquals(200, response.statusCode());

        var reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(response.body()), StandardCharsets.UTF_8)
        );

        assertEquals("<html><body>", reader.readLine());
        assertEquals(header, reader.readLine());

        long result = Long.parseLong(reader.readLine());

        assertEquals("</body></html>", reader.readLine());

        assertNull(reader.readLine());

        return result;
    }

    void addProduct(Product product) throws Exception {
        var response = serviceInfo.addProduct(product.getName(), product.getPrice());

        assertEquals(200, response.statusCode());

        assertEquals("OK\n", new String(response.body(), StandardCharsets.UTF_8));
    }

    List<Product> getProducts() throws Exception {
        var response = serviceInfo.getProducts();

        assertEquals(200, response.statusCode());

        var reader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(response.body()), StandardCharsets.UTF_8)
        );

        assertEquals("<html><body>", reader.readLine());

        List<Product> products = new ArrayList<>();
        while (true) {
            String line = reader.readLine();
            assertNotNull(line);

            if (line.equals("</body></html>")) {
                break;
            }

            products.add(extractProduct(line));
        }

        assertNull(reader.readLine());

        return products;
    }

    Optional<Product> getMaxProduct() throws Exception {
        return extractProductFromResponse(serviceInfo.max(), "<h1>Product with max price: </h1>");
    }

    Optional<Product> getMinProduct() throws Exception {
        return extractProductFromResponse(serviceInfo.min(), "<h1>Product with min price: </h1>");
    }

    long getSum() throws Exception {
        return extractLongFromResponse(serviceInfo.sum(), "Summary price: ");
    }

    long getCount() throws Exception {
        return extractLongFromResponse(serviceInfo.count(), "Number of products: ");
    }

    @Test
    void differentProducts() throws Exception {
        var iphone = new Product("iphone", 2000);
        var apple = new Product("apple", 2);

        addProduct(iphone);
        addProduct(apple);

        assertThat(getProducts()).containsExactlyInAnyOrder(iphone, apple);

        assertEquals(iphone, getMaxProduct().get());
        assertEquals(apple, getMinProduct().get());
        assertEquals(2002, getSum());
        assertEquals(2, getCount());

        var car = new Product("car", 100_000);
        addProduct(car);

        assertThat(getProducts()).containsExactlyInAnyOrder(iphone, apple, car);

        assertEquals(car, getMaxProduct().get());
        assertEquals(apple, getMinProduct().get());
        assertEquals(102_002, getSum());
        assertEquals(3, getCount());
    }

    @Test
    void sameProducts() throws Exception {
        var a = new Product("a", 1);
        var b = new Product("b", 10);
        var c = new Product("c", 100);

        addProduct(a);
        addProduct(b);
        addProduct(c);
        addProduct(b);

        assertThat(getProducts()).containsExactlyInAnyOrder(a, b, b, c);

        assertEquals(c, getMaxProduct().get());
        assertEquals(a, getMinProduct().get());
        assertEquals(121, getSum());
        assertEquals(4, getCount());
    }

    @Test
    void empty() throws Exception {
        assertTrue(getMinProduct().isEmpty());
        assertTrue(getMaxProduct().isEmpty());

        assertEquals(0, getSum());
        assertEquals(0, getCount());
    }

}
