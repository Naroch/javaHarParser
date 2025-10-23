package org.example.dto;

public interface ProductMonthlyStatsProjection {
    Long getProduct_id();
    String getProduct_title();
    String getProduct_url();
    Integer getYear();
    Integer getMonth();
    Long getPositive_reviews_count();
    Long getTotal_positive_reviews();
}
