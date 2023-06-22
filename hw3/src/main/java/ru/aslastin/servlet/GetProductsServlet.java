package ru.aslastin.servlet;

import ru.aslastin.processor.product.GetProductsRequestProcessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GetProductsServlet extends HttpServlet {
    private final GetProductsRequestProcessor requestProcessor;

    public GetProductsServlet(String databasePath) {
        requestProcessor = new GetProductsRequestProcessor(databasePath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requestProcessor.process(request, response);
    }
}
