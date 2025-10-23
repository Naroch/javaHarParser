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
import java.util.Objects;
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
    @Transactional(readOnly = true)
    public List<ProductMonthlyStatsDto> getMonthlyPositiveReviewsStats() {
        // Load reviews with products to avoid N+1 and LazyInitialization issues
        List<Review> reviews = reviewRepository.findAllWithProducts();

        if (reviews.isEmpty()) {
            return List.of();
        }

        // Filter to only positive reviews (business logic interpretation)
        List<Review> positiveReviews = reviews.stream()
                .filter(Review::isRecommend)
                .toList();

        if (positiveReviews.isEmpty()) {
            return List.of();
        }

        // totalPositiveReviews per product
        Map<Long, Long> totalPerProduct = new HashMap<>();

        // monthly counts per product -> year -> month
        Map<Long, Map<Integer, Map<Integer, Long>>> monthlyCounts = new HashMap<>();

        // Keep product metadata (title, url) per product id
        Map<Long, Product> productById = new HashMap<>();

        for (Review review : positiveReviews) {
            // Extract year and month from creation date
            ZonedDateTime zdt = ZonedDateTime.ofInstant(review.getCreationDate().toInstant(), ZoneId.systemDefault());
            int year = zdt.getYear();
            int month = zdt.getMonthValue();

            if (review.getProducts() == null) continue;

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

        // Build DTOs combining monthly counts with total per product
        List<ProductMonthlyStatsDto> result = new ArrayList<>();
        for (Map.Entry<Long, Map<Integer, Map<Integer, Long>>> productEntry : monthlyCounts.entrySet()) {
            long productId = productEntry.getKey();
            Product product = productById.get(productId);
            String title = product != null ? product.getTitle() : null;
            String url = product != null ? product.getUrl() : null;
            long total = totalPerProduct.getOrDefault(productId, 0L);

            Map<Integer, Map<Integer, Long>> yearsMap = productEntry.getValue();
            for (Map.Entry<Integer, Map<Integer, Long>> yearEntry : yearsMap.entrySet()) {
                int year = yearEntry.getKey();
                Map<Integer, Long> monthsMap = yearEntry.getValue();
                for (Map.Entry<Integer, Long> monthEntry : monthsMap.entrySet()) {
                    int month = monthEntry.getKey();
                    long count = monthEntry.getValue();
                    result.add(new ProductMonthlyStatsDto(productId, title, url, year, month, count, total));
                }
            }
        }

        // Sort as required by SQL: total desc, product_id, year, month
        result.sort(Comparator
                .comparing(ProductMonthlyStatsDto::getTotalPositiveReviews, Comparator.reverseOrder())
                .thenComparing(ProductMonthlyStatsDto::getProductId)
                .thenComparing(ProductMonthlyStatsDto::getYear)
                .thenComparing(ProductMonthlyStatsDto::getMonth)
        );

        return result;
    }
}
