package ru.aslastin.servlet;

import ru.aslastin.processor.AddProductRequestProcessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AddProductServlet extends HttpServlet {
    private final AddProductRequestProcessor requestProcessor;

    public AddProductServlet(String databasePath) {
        requestProcessor = new AddProductRequestProcessor(databasePath);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        requestProcessor.process(request, response);
    }
}
