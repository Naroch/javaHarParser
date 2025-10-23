package org.example.service;

import org.example.dto.ProductMonthlyStatsDto;
import org.example.model.Product;
import org.example.model.Review;
import org.example.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
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
        List<Review> reviews = loadReviewsWithProducts();
        if (reviews.isEmpty()) return List.of();

        Map<Long, Long> totalPerProduct = new HashMap<>();
        Map<Long, Map<Integer, Map<Integer, Long>>> monthlyCounts = new HashMap<>();
        Map<Long, Product> productById = new HashMap<>();

        aggregatePositiveReviews(reviews, totalPerProduct, monthlyCounts, productById);
        if (monthlyCounts.isEmpty()) return List.of();

        List<ProductMonthlyStatsDto> result = buildMonthlyStatsDtos(totalPerProduct, monthlyCounts, productById);
        sortMonthlyStatsDtos(result);
        return result;
    }

    private List<Review> loadReviewsWithProducts() {
        // Load reviews with products to avoid N+1 and LazyInitialization issues
        return reviewRepository.findAllWithProducts();
    }

    private void aggregatePositiveReviews(
            List<Review> reviews,
            Map<Long, Long> totalPerProduct,
            Map<Long, Map<Integer, Map<Integer, Long>>> monthlyCounts,
            Map<Long, Product> productById
    ) {
        for (Review review : reviews) {
            if (review == null || !review.isRecommend()) continue;
            if (review.getCreationDate() == null) continue;
            if (review.getProducts() == null || review.getProducts().isEmpty()) continue;

            ZonedDateTime zdt = ZonedDateTime.ofInstant(review.getCreationDate().toInstant(), ZoneId.systemDefault());
            int year = zdt.getYear();
            int month = zdt.getMonthValue();

            for (Product product : review.getProducts()) {
                if (product == null) continue;
                long productId = product.getId();
                productById.putIfAbsent(productId, product);

                totalPerProduct.merge(productId, 1L, Long::sum);

                monthlyCounts
                        .computeIfAbsent(productId, k -> new HashMap<>())
                        .computeIfAbsent(year, k -> new HashMap<>())
                        .merge(month, 1L, Long::sum);
            }
        }
    }

    private List<ProductMonthlyStatsDto> buildMonthlyStatsDtos(
            Map<Long, Long> totalPerProduct,
            Map<Long, Map<Integer, Map<Integer, Long>>> monthlyCounts,
            Map<Long, Product> productById
    ) {
        List<ProductMonthlyStatsDto> result = new ArrayList<>();
        for (Map.Entry<Long, Map<Integer, Map<Integer, Long>>> productEntry : monthlyCounts.entrySet()) {
            long productId = productEntry.getKey();
            Product product = productById.get(productId);
            String title = product != null ? product.getTitle() : null;
            String url = product != null ? product.getUrl() : null;
            long total = totalPerProduct.getOrDefault(productId, 0L);

            for (Map.Entry<Integer, Map<Integer, Long>> yearEntry : productEntry.getValue().entrySet()) {
                int year = yearEntry.getKey();
                for (Map.Entry<Integer, Long> monthEntry : yearEntry.getValue().entrySet()) {
                    int month = monthEntry.getKey();
                    long count = monthEntry.getValue();
                    result.add(new ProductMonthlyStatsDto(productId, title, url, year, month, count, total));
                }
            }
        }
        return result;
    }

    private void sortMonthlyStatsDtos(List<ProductMonthlyStatsDto> result) {
        // Sort as required by SQL: total desc, product_id, year, month
        result.sort(Comparator
                .comparing(ProductMonthlyStatsDto::getTotalPositiveReviews, Comparator.reverseOrder())
                .thenComparing(ProductMonthlyStatsDto::getProductId)
                .thenComparing(ProductMonthlyStatsDto::getYear)
                .thenComparing(ProductMonthlyStatsDto::getMonth)
        );
    }
}
