package org.example.mapper;

import org.example.dto.ProductDto;
import org.example.model.Product;

import java.util.List;

public class ProductMapper {

    public static Product mapToEntity(ProductDto productDto) {
        return new Product(
                productDto.getId(),
                productDto.getTitle(),
                productDto.getUrl()
        );
    }

    public static List<Product> mapToEntityList(List<ProductDto> productDtos) {
        return productDtos.stream()
                .map(ProductMapper::mapToEntity)
                .toList();
    }
}
