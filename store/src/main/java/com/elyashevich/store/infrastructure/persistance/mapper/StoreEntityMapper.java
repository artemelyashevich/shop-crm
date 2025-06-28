package com.elyashevich.store.infrastructure.persistance.mapper;

import com.elyashevich.store.domain.model.Store;
import com.elyashevich.store.infrastructure.persistance.entity.StoreMongoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StoreEntityMapper implements EntityMapper<Store, StoreMongoEntity> {

    @Override
    public Store toDomain(StoreMongoEntity entity) {
        if (entity == null) {
            return null;
        }

        return Store.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .userId(entity.getUserId())
                .productIds(new ArrayList<>(entity.getProducts()))
                .categoryIds(new ArrayList<>(entity.getCategories()))
                .colorIds(new ArrayList<>(entity.getColors()))
                .reviewIds(new ArrayList<>(entity.getReviews()))
                .orderItemIds(new ArrayList<>(entity.getOrderItems()))
                .build();
    }

    @Override
    public StoreMongoEntity toEntity(Store domain) {
        if (domain == null) {
            return null;
        }

        return StoreMongoEntity.builder()
                .id(domain.getId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .userId(domain.getUserId())
                .products(new ArrayList<>(domain.getProductIds()))
                .categories(new ArrayList<>(domain.getCategoryIds()))
                .colors(new ArrayList<>(domain.getColorIds()))
                .reviews(new ArrayList<>(domain.getReviewIds()))
                .orderItems(new ArrayList<>(domain.getOrderItemIds()))
                .build();
    }

    @Override
    public List<Store> toDomain(List<StoreMongoEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<StoreMongoEntity> toEntity(List<Store> domains) {
        return domains.stream().map(this::toEntity).toList();
    }
}