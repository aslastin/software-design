package ru.aslastin.processor.stat;

public class CountRequestProcessor extends StatRequestProcessor {
    public CountRequestProcessor(String databasePath) {
        super(databasePath, "SELECT COUNT(*) FROM PRODUCT", "Number of products: ");
    }
}
