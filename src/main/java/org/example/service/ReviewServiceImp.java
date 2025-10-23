package org.example.service;

import org.example.dto.ProductMonthlyStatsDto;
import org.example.dto.ProductMonthlyStatsProjection;
import org.example.model.Review;
import org.example.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImp(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void insertReviews(List<Review> reviews) {
        reviewRepository.saveAll(reviews);
    }

    @Override
    public List<ProductMonthlyStatsDto> getMonthlyPositiveReviewsStats() {
        List<ProductMonthlyStatsProjection> rows = reviewRepository.findMonthlyReviewStats();
        return rows.stream()
                .map(p -> new ProductMonthlyStatsDto(
                        p.getProduct_id(),
                        p.getProduct_title(),
                        p.getProduct_url(),
                        p.getYear(),
                        p.getMonth(),
                        p.getPositive_reviews_count(),
                        p.getTotal_positive_reviews()
                ))
                .collect(Collectors.toList());
    }
}
