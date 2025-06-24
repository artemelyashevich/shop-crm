package com.elyashevich.core.infrastructure.persistance.mapper.impl;

import com.elyashevich.core.domain.model.Color;
import com.elyashevich.core.infrastructure.persistance.entity.ColorMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ColorEntityMapper implements EntityMapper<Color, ColorMongoEntity> {

    @Override
    public Color toDomain(ColorMongoEntity entity) {
        if (entity == null) {
            return null;
        }

        return Color.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .name(entity.getName())
                .value(entity.getValue())
                .storeId(entity.getStoreId())
                .build();
    }

    @Override
    public ColorMongoEntity toEntity(Color domain) {
        if (domain == null) {
            return null;
        }

        return ColorMongoEntity.builder()
                .id(domain.getId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .name(domain.getName())
                .value(domain.getValue())
                .storeId(domain.getStoreId())
                .build();
    }

    @Override
    public void updateEntity(Color domain, ColorMongoEntity entity) {
        if (domain == null || entity == null) {
            return;
        }

        entity.setName(domain.getName());
        entity.setValue(domain.getValue());
        entity.setStoreId(domain.getStoreId());
        entity.setUpdatedAt(domain.getUpdatedAt());
    }

    @Override
    public List<Color> toDomain(List<ColorMongoEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<ColorMongoEntity> toEntity(List<Color> domains) {
        return domains.stream().map(this::toEntity).toList();
    }
}
