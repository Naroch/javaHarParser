package org.example.service;

import org.example.model.Review;

import java.util.List;

public interface ReviewService {
    void insertReviews(List<Review> reviews);
}
