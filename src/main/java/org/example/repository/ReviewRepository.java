package org.example.repository;

import org.example.dto.ProductMonthlyStatsProjection;
import org.example.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    @Query(value = "WITH ProductReviews AS (\n" +
            "    SELECT\n" +
            "        p.id AS product_id,\n" +
            "        p.title AS product_title,\n" +
            "        p.url AS product_url,\n" +
            "        COUNT(r.id) AS total_positive_reviews\n" +
            "    FROM reviews r\n" +
            "    INNER JOIN reviews_products rp ON r.id = rp.review_id\n" +
            "    INNER JOIN products p ON rp.product_id = p.id\n" +
            "    WHERE r.recommend = true\n" +
            "    GROUP BY p.id, p.title, p.url\n" +
            "),\n" +
            "MonthlyReviews AS (\n" +
            "    SELECT\n" +
            "        p.id AS product_id,\n" +
            "        EXTRACT(YEAR FROM r.creation_date) AS year,\n" +
            "        EXTRACT(MONTH FROM r.creation_date) AS month,\n" +
            "        COUNT(r.id) AS positive_reviews_count\n" +
            "    FROM reviews r\n" +
            "    INNER JOIN reviews_products rp ON r.id = rp.review_id\n" +
            "    INNER JOIN products p ON rp.product_id = p.id\n" +
            "    WHERE r.recommend = true\n" +
            "    GROUP BY\n" +
            "        p.id,\n" +
            "        EXTRACT(YEAR FROM r.creation_date),\n" +
            "        EXTRACT(MONTH FROM r.creation_date)\n" +
            ")\n" +
            "SELECT\n" +
            "    pr.product_id,\n" +
            "    pr.product_title,\n" +
            "    pr.product_url,\n" +
            "    mr.year,\n" +
            "    mr.month,\n" +
            "    mr.positive_reviews_count,\n" +
            "    pr.total_positive_reviews\n" +
            "FROM ProductReviews pr\n" +
            "INNER JOIN MonthlyReviews mr ON pr.product_id = mr.product_id\n" +
            "ORDER BY\n" +
            "    pr.total_positive_reviews DESC,\n" +
            "    pr.product_id,\n" +
            "    mr.year,\n" +
            "    mr.month;\n", nativeQuery = true)
    List<ProductMonthlyStatsProjection> findMonthlyPositiveReviewStats();
}
