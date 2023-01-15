package ru.aslastin.processor.product;

import ru.aslastin.processor.HttpRequestProcessor;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductsRequestProcessor extends HttpRequestProcessor {
    private final String query;
    private final String productsHeader;

    public ProductsRequestProcessor(String databasePath, String query, String productsHeader) {
        super(databasePath);
        this.query = query;
        this.productsHeader = productsHeader;
    }

    public ProductsRequestProcessor(String databasePath, String query) {
        this(databasePath, query, null);
    }

    @Override
    protected void processDatabaseQuery(HttpServletRequest request, PrintWriter responseWriter, Statement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery(query)) {
            responseWriter.println("<html><body>");

            if (productsHeader != null) {
                responseWriter.println(productsHeader);
            }

            while (rs.next()) {
                String  name = rs.getString("name");
                int price  = rs.getInt("price");
                responseWriter.println(name + "\t" + price + "</br>");
            }

            responseWriter.println("</body></html>");
        }
    }
}
