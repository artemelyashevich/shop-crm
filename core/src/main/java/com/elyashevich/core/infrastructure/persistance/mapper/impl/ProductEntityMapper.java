package com.elyashevich.core.infrastructure.persistance.mapper.impl;

import com.elyashevich.core.domain.model.Product;
import com.elyashevich.core.infrastructure.persistance.entity.CategoryMongoEntity;
import com.elyashevich.core.infrastructure.persistance.entity.ColorMongoEntity;
import com.elyashevich.core.infrastructure.persistance.entity.ProductMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ProductEntityMapper implements EntityMapper<Product, ProductMongoEntity> {

    @Override
    public Product toDomain(ProductMongoEntity entity) {
        if (entity == null) return null;

        return Product.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .images(new ArrayList<>(entity.getImages()))
                .reviewId(entity.getReviewId())
                .storeId(entity.getStoreId())
                .categoryId(Optional.ofNullable(entity.getCategory())
                        .map(CategoryMongoEntity::getId)
                        .orElse(null))
                .colorId(Optional.ofNullable(entity.getColor())
                        .map(ColorMongoEntity::getId)
                        .orElse(null))
                .build();
    }

    @Override
    public ProductMongoEntity toEntity(Product domain) {
        if (domain == null) return null;

        return ProductMongoEntity.builder()
                .id(domain.getId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .price(domain.getPrice())
                .images(new ArrayList<>(domain.getImages()))
                .reviewId(domain.getReviewId())
                .storeId(domain.getStoreId())
                .build();
    }

    @Override
    public void updateEntity(Product domain, ProductMongoEntity entity) {
        if (domain == null || entity == null) return;

        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setPrice(domain.getPrice());
        entity.setImages(new ArrayList<>(domain.getImages()));
        entity.setReviewId(domain.getReviewId());
        entity.setStoreId(domain.getStoreId());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }

    @Override
    public List<Product> toDomain(List<ProductMongoEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<ProductMongoEntity> toEntity(List<Product> domains) {
        return domains.stream().map(this::toEntity).toList();
    }
}
