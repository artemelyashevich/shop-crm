package com.elyashevich.core.infrastructure.web.mapper;

import com.elyashevich.core.domain.model.Product;
import com.elyashevich.core.infrastructure.web.dto.product.ProductRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public Product toModel(ProductRequestDto dto) {
        return Product.builder()
                .title(dto.title())
                .description(dto.description())
                .price(dto.price())
                .images(dto.images())
                .categoryId(dto.categoryId())
                .colorId(dto.colorId())
                .build();
    }
}
