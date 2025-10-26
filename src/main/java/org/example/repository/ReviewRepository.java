package org.example.repository;

import org.example.dto.ProductMonthlyStatsProjection;
import org.example.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    @Query(value = """
            WITH ProductReviews AS (
                SELECT
                    p.id AS product_id,
                    p.title AS product_title,
                    p.url AS product_url,
                    COUNT(r.id) AS total_positive_reviews
                FROM reviews r
                INNER JOIN reviews_products rp ON r.id = rp.review_id
                INNER JOIN products p ON rp.product_id = p.id
                WHERE r.recommend = true
                GROUP BY p.id, p.title, p.url
            ),
            MonthlyReviews AS (
                SELECT
                    p.id AS product_id,
                    EXTRACT(YEAR FROM r.creation_date) AS year,
                    EXTRACT(MONTH FROM r.creation_date) AS month,
                    COUNT(r.id) AS positive_reviews_count
                FROM reviews r
                INNER JOIN reviews_products rp ON r.id = rp.review_id
                INNER JOIN products p ON rp.product_id = p.id
                WHERE r.recommend = true
                GROUP BY
                    p.id,
                    EXTRACT(YEAR FROM r.creation_date),
                    EXTRACT(MONTH FROM r.creation_date)
            )
            SELECT
                pr.product_id,
                pr.product_title,
                pr.product_url,
                mr.year,
                mr.month,
                mr.positive_reviews_count,
                pr.total_positive_reviews
            FROM ProductReviews pr
            INNER JOIN MonthlyReviews mr ON pr.product_id = mr.product_id
            ORDER BY
                pr.total_positive_reviews DESC,
                pr.product_id,
                mr.year,
                mr.month;
            """, nativeQuery = true)
    List<ProductMonthlyStatsProjection> findMonthlyReviewStats();
}
