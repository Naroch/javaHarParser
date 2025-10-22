package org.example.service;

import org.example.model.Review;
import org.example.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
