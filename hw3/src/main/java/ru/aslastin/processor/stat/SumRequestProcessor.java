package ru.aslastin.processor.stat;

public class SumRequestProcessor extends StatRequestProcessor {
    public SumRequestProcessor(String databasePath) {
        super(databasePath, "SELECT SUM(price) FROM PRODUCT", "Summary price: ");
    }
}
