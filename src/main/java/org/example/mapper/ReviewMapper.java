package org.example.mapper;

import org.example.dto.ReviewDto;
import org.example.model.Review;

import java.util.List;

public class ReviewMapper {

    public static Review mapToEntity(ReviewDto reviewDto, String sellerName) {

        List products = ProductMapper.mapToEntityList(reviewDto.getOffers() == null ? List.of() : reviewDto.getOffers());

        return new Review(
                reviewDto.getId(),
                sellerName,
                reviewDto.getCreationDate(),
                reviewDto.getLastChangeDate(),
                reviewDto.isRatedAgain(),
                5,
                5,
                reviewDto.isRecommend(),
                products
        );
    }

    public static List<Review> mapToEntityList(List<ReviewDto> reviewDtos, String sellerName) {
        if (reviewDtos == null || reviewDtos.isEmpty()) {
            return List.of();
        }
        return reviewDtos.stream()
                .map((reviewDto) -> ReviewMapper.mapToEntity(reviewDto, sellerName))
                .toList();
    }
}
