package ru.aslastin.processor.product;

public class MinRequestProcessor extends ProductsRequestProcessor {
    public MinRequestProcessor(String databasePath) {
        super(databasePath, "SELECT * FROM PRODUCT ORDER BY PRICE LIMIT 1",
                "<h1>Product with min price: </h1>");
    }
}
