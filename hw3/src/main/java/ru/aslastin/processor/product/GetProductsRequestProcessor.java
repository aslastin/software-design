package ru.aslastin.processor.product;

public class GetProductsRequestProcessor extends ProductsRequestProcessor {
    public GetProductsRequestProcessor(String databasePath) {
        super(databasePath, "SELECT * FROM PRODUCT");
    }
}
