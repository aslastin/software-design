package ru.aslastin.processor.stat;

import ru.aslastin.processor.HttpRequestProcessor;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class StatRequestProcessor extends HttpRequestProcessor {
    private final String query;
    private final String statHeader;

    public StatRequestProcessor(String databasePath, String query, String statHeader) {
        super(databasePath);
        this.query = query;
        this.statHeader = statHeader;
    }

    public StatRequestProcessor(String databasePath, String query) {
        this(databasePath, query, null);
    }

    @Override
    protected void processDatabaseQuery(HttpServletRequest request, PrintWriter responseWriter, Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(query)) {
            responseWriter.println("<html><body>");

            if (statHeader != null) {
                responseWriter.println(statHeader);
            }

            if (rs.next()) {
                responseWriter.println(rs.getInt(1));
            }

            responseWriter.println("</body></html>");
        }
    }
}
