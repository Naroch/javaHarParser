package org.example.repository.projection;

public interface ProductMonthlyCountProjection {
    long getProductId();
    int getYear();
    int getMonth();
    long getPositiveReviewsCount();
}
