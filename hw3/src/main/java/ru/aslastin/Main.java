package ru.aslastin;

import ru.aslastin.service.ProductService;

import java.util.concurrent.TimeUnit;

public class Main {
    private final static int PORT = 8081;
    private final static String DATABASE_PATH = "jdbc:sqlite:test.db";

    private Main() {
        // just main method
    }

    public static void main(String[] args) throws Exception {
        int port = PORT;
        String databasePath = DATABASE_PATH;

        if (args != null && args.length == 2) {
            port = Integer.parseInt(args[0]);
            databasePath = args[1];
        }

        new ProductService(port, databasePath).start().get(10, TimeUnit.SECONDS);
    }
}
