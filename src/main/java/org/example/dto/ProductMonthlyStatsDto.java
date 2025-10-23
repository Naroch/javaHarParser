package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductMonthlyStatsDto {
    private long productId;
    private String productTitle;
    private String productUrl;
    private int year;
    private int month;
    private long positiveReviewsCount;
    private long totalPositiveReviews;
}
