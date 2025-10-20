package org.example;

import org.example.model.Review;
import org.example.model.Product;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseHandler {
//
//    CREATE TABLE reviews (
//            id varchar(24) PRIMARY KEY,
//    seller varchar(25) NOT NULL,
//    creationDate TIMESTAMP WITHOUT TIME ZONE NOT NULL,
//    lastChangeDate TIMESTAMP WITHOUT TIME ZONE NOT NULL,
//    ratedAgain BOOLEAN NOT NULL,
//    descriptionRating INT NOT NULL,
//    serviceRating INT NOT NULL,
//    recommend BOOLEAN NOT NULL
//);
//
//    CREATE TABLE products (
//            id BIGSERIAL PRIMARY KEY,
//            title VARCHAR(255) NOT NULL,
//    url TEXT NOT NULL
//);
//
//    CREATE TABLE reviews_products (
//            review_id varchar(24) NOT NULL,
//    product_id BIGSERIAL NOT NULL,
//    CONSTRAINT pk_review_product PRIMARY KEY (review_id, product_id)
//);



//    WITH ProductReviews AS (
//            SELECT
//                    p.id AS product_id,
//            p.title AS product_title,
//            p.url AS product_url,
//            COUNT(r.id) AS total_positive_reviews
//    FROM
//    reviews r
//    INNER JOIN reviews_products rp ON r.id = rp.review_id
//    INNER JOIN products p ON rp.product_id = p.id
//            WHERE
//    r.recommend = TRUE
//    GROUP BY
//    p.id
//),
//    MonthlyReviews AS (
//            SELECT
//                    p.id AS product_id,
//            EXTRACT(YEAR FROM r.creationDate) AS year,
//    EXTRACT(MONTH FROM r.creationDate) AS month,
//    COUNT(r.id) AS positive_reviews_count
//    FROM
//    reviews r
//    INNER JOIN reviews_products rp ON r.id = rp.review_id
//    INNER JOIN products p ON rp.product_id = p.id
//            WHERE
//    r.recommend = TRUE
//    GROUP BY
//    p.id, EXTRACT(YEAR FROM r.creationDate), EXTRACT(MONTH FROM r.creationDate)
//            )
//    SELECT
//    pr.product_id,
//    pr.product_title,
//    pr.product_url,
//    mr.year,
//    mr.month,
//    mr.positive_reviews_count,
//    pr.total_positive_reviews
//            FROM
//    ProductReviews pr
//    INNER JOIN MonthlyReviews mr ON pr.product_id = mr.product_id
//    ORDER BY
//    pr.total_positive_reviews DESC, pr.product_id, mr.year, mr.month;
private final static String url = "jdbc:postgresql://localhost:32768/AllegroAnalitics?charSet=UTF-8";

    private final static String username = "admin";
    private final static String password = "admin";

    private final static String INSERT_REVIEW = """
            INSERT INTO reviews (id, seller, creationDate, lastChangeDate, ratedAgain, descriptionRating, serviceRating, recommend)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (id) DO NOTHING""";
    private final static String INSERT_PRODUCT = """
            INSERT INTO products (id, title, url) 
            VALUES (?, ?, ?)
            ON CONFLICT (id) DO NOTHING""";
    private final static String LINK_REVIEW_WITH_PRODUCTS = """
            INSERT INTO reviews_products (review_id, product_id)
            VALUES (?, ?)
            ON CONFLICT (review_id, product_id) DO NOTHING""";

    public static void saveReviewsAndProducts(List<Review> reviews) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement insertReview = connection.prepareStatement(INSERT_REVIEW);
                 PreparedStatement insertProduct = connection.prepareStatement(INSERT_PRODUCT);
                 PreparedStatement linkReviewProduct = connection.prepareStatement(LINK_REVIEW_WITH_PRODUCTS)) {

                for (Review review : reviews) {
                    insertReview.setString(1, review.getId());
                    insertReview.setString(2, review.getSeller());
                    insertReview.setTimestamp(3, new java.sql.Timestamp(review.getCreationDate().getTime()));
                    insertReview.setTimestamp(4, new java.sql.Timestamp(review.getLastChangeDate().getTime()));
                    insertReview.setBoolean(5, review.isRatedAgain());
                    insertReview.setInt(6, review.getDescriptionRating());
                    insertReview.setInt(7, review.getServiceRating());
                    insertReview.setBoolean(8, review.isRecommend());
                    insertReview.addBatch();

                    for (Product product : review.getProducts()) {
                        insertProduct.setLong(1,  product.getId());
                        insertProduct.setString(2, product.getTitle());
                        insertProduct.setString(3, product.getUrl());
                        insertProduct.addBatch();

                        linkReviewProduct.setString(1, review.getId());
                        linkReviewProduct.setLong(2, product.getId());
                        linkReviewProduct.addBatch();
                    }
                }

                insertReview.executeBatch();
                insertProduct.executeBatch();
                linkReviewProduct.executeBatch();
                connection.commit();
//                System.out.println("Batch insert completed successfully.");
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

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
