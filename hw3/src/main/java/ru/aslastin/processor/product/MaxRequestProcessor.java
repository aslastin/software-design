package ru.aslastin.processor.product;

public class MaxRequestProcessor extends ProductsRequestProcessor {
    public MaxRequestProcessor(String databasePath) {
        super(databasePath, "SELECT * FROM PRODUCT ORDER BY PRICE DESC LIMIT 1",
                "<h1>Product with max price: </h1>");
    }
}

