package org.example.service;

import org.example.dto.ProductMonthlyStatsDto;
import org.example.model.Review;
import org.example.repository.ReviewRepository;
import org.example.repository.projection.ProductMonthlyCountProjection;
import org.example.repository.projection.ProductTotalPositiveReviewsProjection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Transactional(readOnly = true)
    public List<ProductMonthlyStatsDto> getMonthlyPositiveReviewsStats() {
        // Load totals per product
        List<ProductTotalPositiveReviewsProjection> totals = reviewRepository.findProductPositiveReviewTotals();
        if (totals == null || totals.isEmpty()) return List.of();

        // Index totals by productId
        Map<Long, ProductTotalPositiveReviewsProjection> totalsByProduct = new HashMap<>();
        for (ProductTotalPositiveReviewsProjection t : totals) {
            totalsByProduct.put(t.getProductId(), t);
        }

        // Load monthly counts
        List<ProductMonthlyCountProjection> monthly = reviewRepository.findMonthlyPositiveReviews();
        if (monthly == null || monthly.isEmpty()) return List.of();

        List<ProductMonthlyStatsDto> result = new ArrayList<>(monthly.size());
        for (ProductMonthlyCountProjection m : monthly) {
            ProductTotalPositiveReviewsProjection t = totalsByProduct.get(m.getProductId());
            if (t == null) continue; // safety, should not happen
            result.add(new ProductMonthlyStatsDto(
                    m.getProductId(),
                    t.getProductTitle(),
                    t.getProductUrl(),
                    m.getYear(),
                    m.getMonth(),
                    m.getPositiveReviewsCount(),
                    t.getTotalPositiveReviews()
            ));
        }

        // Sort: total desc, productId, year, month
        result.sort(Comparator
                .comparing(ProductMonthlyStatsDto::getTotalPositiveReviews, Comparator.reverseOrder())
                .thenComparing(ProductMonthlyStatsDto::getProductId)
                .thenComparing(ProductMonthlyStatsDto::getYear)
                .thenComparing(ProductMonthlyStatsDto::getMonth)
        );

        return result;
    }
}
