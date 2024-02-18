package org.example.mapper;

import org.example.dto.ReviewDto;
import org.example.model.Review;

import java.util.List;

public class ReviewMapper {

    public static Review mapToEntity(ReviewDto reviewDto, String sellerName) {
        return new Review(
                reviewDto.getId(),
                sellerName,
                reviewDto.getCreationDate(),
                reviewDto.getLastChangeDate(),
                reviewDto.isRatedAgain(),
                reviewDto.getRates().getDescription(),
                reviewDto.getRates().getService(),
                reviewDto.isRecommend(),
                ProductMapper.mapToEntityList(reviewDto.getOffers())
        );
    }

    public static List<Review> mapToEntityList(List<ReviewDto> reviewDtos, String sellerName) {
        return reviewDtos.stream()
                .map((reviewDto) -> ReviewMapper.mapToEntity(reviewDto, sellerName))
                .toList();
    }
}
