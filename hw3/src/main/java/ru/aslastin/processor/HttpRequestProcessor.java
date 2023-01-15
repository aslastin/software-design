package ru.aslastin.processor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Combines database layer and response layer
public abstract class HttpRequestProcessor {
    private final String databasePath;

    public HttpRequestProcessor(String databasePath) {
        this.databasePath = databasePath;
    }

    public String getDatabasePath() {
        return databasePath;
    }

    public void process(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (Connection c = DriverManager.getConnection(getDatabasePath())) {
            try (Statement stmt = c.createStatement()) {
                processDatabaseQuery(request, response.getWriter(), stmt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        processResponse(response);
    }

    protected abstract void processDatabaseQuery(HttpServletRequest request, PrintWriter responseWriter, Statement stmt)
            throws SQLException;

    protected void processResponse(HttpServletResponse response) {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
