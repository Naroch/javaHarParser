package org.example;

import org.example.model.Review;
import org.example.model.Product;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseHandler {

    private final static String url = "jdbc:postgresql://localhost:32768/AllegroAnalitics";
    private final static String username = "admin";
    private final static String password = "admin";

    private final static String INSERT_REVIEW = """
            INSERT INTO reviews (id, creationDate, lastChangeDate, ratedAgain, descriptionRating, serviceRating, recommend)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING""";
    private final static String INSERT_PRODUCT = """
            INSERT INTO products (id, title, url) 
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO NOTHING""";
    private final static String LINK_REVIEW_WITH_PRODUCTS = """
            INSERT INTO reviews_products (review_id, product_id)
            VALUES (?, ?)
            ON CONFLICT (review_id, product_id) DO NOTHING"""; //TODO

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void saveReviewsAndProducts(List<Review> reviews) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement insertReview = connection.prepareStatement(INSERT_REVIEW);
                 PreparedStatement insertProduct = connection.prepareStatement(INSERT_PRODUCT);
                 PreparedStatement linkReviewProduct = connection.prepareStatement(LINK_REVIEW_WITH_PRODUCTS)) {

                for (Review review : reviews) {
                    insertReview.setString(1, review.getId());
                    insertReview.setTimestamp(2, new java.sql.Timestamp(review.getCreationDate().getTime()));
                    insertReview.setTimestamp(3, new java.sql.Timestamp(review.getLastChangeDate().getTime()));
                    insertReview.setBoolean(4, review.isRatedAgain());
                    insertReview.setInt(5, review.getDescriptionRating());
                    insertReview.setInt(6, review.getServiceRating());
                    insertReview.setBoolean(7, review.isRecommend());
                    insertReview.addBatch();

                    for (Product product : review.getProducts()) {
                        insertProduct.setInt(1, (int) product.getId());
                        insertProduct.setString(2, product.getTitle());
                        insertProduct.setString(3, product.getUrl());
                        insertProduct.addBatch();

                        linkReviewProduct.setString(1, review.getId());
                        linkReviewProduct.setInt(2, (int) product.getId());
                        linkReviewProduct.addBatch();
                    }
                }

                insertReview.executeBatch();
                insertProduct.executeBatch();
                linkReviewProduct.executeBatch();
                connection.commit();
                System.out.println("Batch insert completed successfully.");
            } catch (SQLException e) {
                connection.rollback();
                System.err.println("Failed to execute batch insert. Transaction is rolled back.");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("Failed to obtain database connection.");
            e.printStackTrace();
        }
    }
}
