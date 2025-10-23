package org.example.repository.projection;

public interface ProductTotalPositiveReviewsProjection {
    long getProductId();
    String getProductTitle();
    String getProductUrl();
    long getTotalPositiveReviews();
}
