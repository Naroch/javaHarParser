package org.example.service;

import org.example.dto.ProductMonthlyStatsDto;
import org.example.model.Review;

import java.util.List;

public interface ReviewService {
    void insertReviews(List<Review> reviews);

    List<ProductMonthlyStatsDto> getMonthlyReviewsStats();

    List<ProductMonthlyStatsDto> getMonthlyReviewsStatsBySeller(String seller);
}
