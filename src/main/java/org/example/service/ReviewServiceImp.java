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
        // Canonicalize Product instances across all reviews within this save operation
        // to avoid Hibernate's "Multiple representations of the same entity are being merged" error.
        // When multiple detached Product instances with the same ID appear in different Review objects
        // in the same persistence context, cascading MERGE can trigger that exception.
        // We ensure that each unique productId maps to exactly one Product instance referenced by all reviews.
        java.util.Map<Long, org.example.model.Product> canonicalProducts = new java.util.HashMap<>();

        for (Review review : reviews) {
            if (review.getProducts() == null || review.getProducts().isEmpty()) {
                continue;
            }
            // Deduplicate products within a single review by ID and replace with canonical instances
            java.util.LinkedHashMap<Long, org.example.model.Product> perReview = new java.util.LinkedHashMap<>();
            for (org.example.model.Product p : review.getProducts()) {
                if (p == null) continue;
                long id = p.getId();
                org.example.model.Product canonical = canonicalProducts.computeIfAbsent(id, k -> p);
                perReview.put(id, canonical);
            }
            review.setProducts(new java.util.ArrayList<>(perReview.values()));
        }

        reviewRepository.saveAll(reviews);
    }

    @Override
    public List<ProductMonthlyStatsDto> getMonthlyReviewsStats() {
        List<ProductMonthlyStatsProjection> rows = reviewRepository.findMonthlyReviewStats();
        return mapRows(rows);
    }

    @Override
    public List<ProductMonthlyStatsDto> getMonthlyReviewsStatsBySeller(String seller) {
        List<ProductMonthlyStatsProjection> rows = reviewRepository.findMonthlyReviewStatsBySeller(seller);
        return mapRows(rows);
    }

    @Override
    public List<String> getDistinctSellers() {
        return reviewRepository.findDistinctSellers();
    }

    private List<ProductMonthlyStatsDto> mapRows(List<ProductMonthlyStatsProjection> rows) {
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
