package org.example.repository.projection;

public interface ProductMonthlyStatsProjection {
    long getProductId();
    String getProductTitle();
    String getProductUrl();
    int getYear();
    int getMonth();
    long getPositiveReviewsCount();
    long getTotalPositiveReviews();
}
