package org.example.repository;

import org.example.model.Review;
import org.example.repository.projection.ProductMonthlyCountProjection;
import org.example.repository.projection.ProductTotalPositiveReviewsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    @Query("select distinct r from Review r join fetch r.products p")
    List<Review> findAllWithProducts();

    // Split queries (no CTEs)
    @Query(value = "SELECT\n" +
            "    p.id AS productId,\n" +
            "    p.title AS productTitle,\n" +
            "    p.url AS productUrl,\n" +
            "    COUNT(r.id) AS totalPositiveReviews\n" +
            "FROM reviews r\n" +
            "INNER JOIN reviews_products rp ON r.id = rp.review_id\n" +
            "INNER JOIN products p ON rp.product_id = p.id\n" +
            "GROUP BY p.id, p.title, p.url\n" +
            "ORDER BY COUNT(r.id) DESC, p.id;", nativeQuery = true)
    List<ProductTotalPositiveReviewsProjection> findProductReviewTotals();

    @Query(value = "SELECT\n" +
            "    p.id AS productId,\n" +
            "    EXTRACT(YEAR FROM r.creation_date)::int AS year,\n" +
            "    EXTRACT(MONTH FROM r.creation_date)::int AS month,\n" +
            "    COUNT(r.id) AS positiveReviewsCount\n" +
            "FROM reviews r\n" +
            "INNER JOIN reviews_products rp ON r.id = rp.review_id\n" +
            "INNER JOIN products p ON rp.product_id = p.id\n" +
            "GROUP BY p.id, EXTRACT(YEAR FROM r.creation_date), EXTRACT(MONTH FROM r.creation_date)\n" +
            "ORDER BY p.id, year, month;", nativeQuery = true)
    List<ProductMonthlyCountProjection> findMonthlyReviews();
}
