package com.elyashevich.core.infrastructure.persistance.mapper.impl;

import com.elyashevich.core.domain.model.Category;
import com.elyashevich.core.infrastructure.persistance.entity.CategoryMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryEntityMapper implements EntityMapper<Category, CategoryMongoEntity> {

    @Override
    public Category toDomain(CategoryMongoEntity entity) {
        if (entity == null) {
            return null;
        }

        return Category.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .storeId(entity.getStoreId())
                .build();
    }

    @Override
    public CategoryMongoEntity toEntity(Category domain) {
        if (domain == null) {
            return null;
        }

        return CategoryMongoEntity.builder()
                .id(domain.getId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .storeId(domain.getStoreId())
                .build();
    }

    @Override
    public void updateEntity(Category domain, CategoryMongoEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setTitle(domain.getTitle());
        entity.setDescription(domain.getDescription());
        entity.setStoreId(domain.getStoreId());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }

    @Override
    public List<Category> toDomain(List<CategoryMongoEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<CategoryMongoEntity> toEntity(List<Category> domains) {
        return domains.stream().map(this::toEntity).toList();
    }
}