package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHandler {

    // JDBC URL for the PostgreSQL database
    private final static String url = "jdbc:postgresql://localhost:32768/AllegroAnalitics";
    private final static String username = "admin";
    private final static String password = "admin";

    private final static String INSERT_REVIEW = """
            INSERT INTO reviews (id, creationDate, lastChangeDate, ratedAgain, descriptionRating, serviceRating, recommend)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING""";
    private final static String INSERT_PRODUCT = """
            INSERT INTO products (id, orderOfferId, title, url) 
            VALUES (?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING""";
    private final static String LINK_REVIEW_WITH_PRODUCTS = """
            INSERT INTO reviews_products (review_id, product_id)
            VALUES (?, ?)
            ON CONFLICT (review_id, product_id) DO NOTHING""";

    private Connection getConnection() {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the PostgreSQL database!");
            return connection;
        } catch (SQLException e) {
            System.err.println("Error connecting to the PostgreSQL database!");
            e.printStackTrace();
            return null;
        }
    }
}
