package ru.aslastin.servlet;

import ru.aslastin.processor.HttpRequestProcessor;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class QueryServlet extends HttpServlet {
    private final Map<String, HttpRequestProcessor> requestProcessorByCommand;

    public QueryServlet(Map<String, HttpRequestProcessor> requestProcessorByCommand) {
        this.requestProcessorByCommand = requestProcessorByCommand;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String command = request.getParameter("command");

        if (requestProcessorByCommand.containsKey(command)) {
            requestProcessorByCommand.get(command).process(request, response);
        } else {
            response.getWriter().println("Unknown command: " + command);
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

}
