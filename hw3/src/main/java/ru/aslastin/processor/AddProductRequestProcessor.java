package ru.aslastin.processor;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Statement;

public class AddProductRequestProcessor extends HttpRequestProcessor {

    public AddProductRequestProcessor(String databasePath) {
        super(databasePath);
    }

    @Override
    protected void processDatabaseQuery(HttpServletRequest request, PrintWriter responseWriter, Statement stmt) throws SQLException {
        String name = request.getParameter("name");
        long price = Long.parseLong(request.getParameter("price"));

        String sql = "INSERT INTO PRODUCT " +
                "(NAME, PRICE) VALUES (\"" + name + "\"," + price + ")";

        stmt.executeUpdate(sql);

        responseWriter.println("OK");
    }
}
