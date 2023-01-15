package ru.aslastin.service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import ru.aslastin.processor.HttpRequestProcessor;
import ru.aslastin.processor.product.MaxRequestProcessor;
import ru.aslastin.processor.product.MinRequestProcessor;
import ru.aslastin.processor.stat.CountRequestProcessor;
import ru.aslastin.processor.stat.SumRequestProcessor;
import ru.aslastin.servlet.AddProductServlet;
import ru.aslastin.servlet.GetProductsServlet;
import ru.aslastin.servlet.QueryServlet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ProductService implements Service {
    private final int port;
    private final String databasePath;
    private Server server;
    private Thread serverThread;

    public ProductService(int port, String databasePath) {
        this.port = port;
        this.databasePath = databasePath;
    }

    @Override
    public CompletableFuture<?> start() throws Exception {
        initDatabase();

        server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new AddProductServlet(databasePath)), "/add-product");

        context.addServlet(new ServletHolder(new GetProductsServlet(databasePath)),"/get-products");

        Map<String, HttpRequestProcessor> requestProcessorByCommand = new HashMap<>();
        requestProcessorByCommand.put("max", new MaxRequestProcessor(databasePath));
        requestProcessorByCommand.put("min", new MinRequestProcessor(databasePath));
        requestProcessorByCommand.put("sum", new SumRequestProcessor(databasePath));
        requestProcessorByCommand.put("count", new CountRequestProcessor(databasePath));

        context.addServlet(new ServletHolder(new QueryServlet(requestProcessorByCommand)),"/query");

        serverThread = new Thread(() -> {
            try {
                server.start();
                server.join();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        serverThread.start();

        return CompletableFuture.completedFuture(null);
    }

    private void initDatabase() {
        try (Connection c = DriverManager.getConnection(databasePath)) {
            try(Statement stmt = c.createStatement()) {
                String sql = "CREATE TABLE IF NOT EXISTS PRODUCT" +
                        "(ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                        " NAME           TEXT    NOT NULL, " +
                        " PRICE          INT     NOT NULL)";

                stmt.executeUpdate(sql);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CompletableFuture<?> stop() throws Exception {
        server.stop();

        serverThread.interrupt();

        return CompletableFuture.completedFuture(null);
    }
}
