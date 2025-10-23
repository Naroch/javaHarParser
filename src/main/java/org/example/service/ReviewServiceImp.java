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
        List<ProductTotalPositiveReviewsProjection> totals = loadProductTotals();
        if (totals == null || totals.isEmpty()) return List.of();

        Map<Long, ProductTotalPositiveReviewsProjection> totalsByProduct = indexTotalsByProduct(totals);

        List<ProductMonthlyCountProjection> monthly = loadMonthlyCounts();
        if (monthly == null || monthly.isEmpty()) return List.of();

        List<ProductMonthlyStatsDto> result = mergeMonthlyWithTotals(monthly, totalsByProduct);
        sortStats(result);
        return result;
    }

    private List<ProductTotalPositiveReviewsProjection> loadProductTotals() {
        return reviewRepository.findProductReviewTotals();
    }

    private Map<Long, ProductTotalPositiveReviewsProjection> indexTotalsByProduct(List<ProductTotalPositiveReviewsProjection> totals) {
        Map<Long, ProductTotalPositiveReviewsProjection> map = new HashMap<>();
        for (ProductTotalPositiveReviewsProjection t : totals) {
            map.put(t.getProductId(), t);
        }
        return map;
    }

    private List<ProductMonthlyCountProjection> loadMonthlyCounts() {
        return reviewRepository.findMonthlyReviews();
    }

    private List<ProductMonthlyStatsDto> mergeMonthlyWithTotals(
            List<ProductMonthlyCountProjection> monthly,
            Map<Long, ProductTotalPositiveReviewsProjection> totalsByProduct
    ) {
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
        return result;
    }

    private void sortStats(List<ProductMonthlyStatsDto> stats) {
        // Sort: total desc, productId, year, month
        stats.sort(Comparator
                .comparing(ProductMonthlyStatsDto::getTotalPositiveReviews, Comparator.reverseOrder())
                .thenComparing(ProductMonthlyStatsDto::getProductId)
                .thenComparing(ProductMonthlyStatsDto::getYear)
                .thenComparing(ProductMonthlyStatsDto::getMonth)
        );
    }
}
