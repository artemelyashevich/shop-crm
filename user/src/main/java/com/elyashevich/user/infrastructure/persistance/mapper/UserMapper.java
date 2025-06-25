package com.elyashevich.user.infrastructure.persistance.mapper;

import com.elyashevich.user.domain.model.User;
import com.elyashevich.user.infrastructure.persistance.entity.UserMongoEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapper implements EntityMapper<User,  UserMongoEntity> {

    public User toDomain(UserMongoEntity entity) {
        if (entity == null) return null;

        return User.builder()
                .id(entity.getId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .name(entity.getName())
                .picture(entity.getPicture())
                .storesId(
                        entity.getStoresId() != null
                                ? new ArrayList<>(entity.getStoresId())
                                : new ArrayList<>()
                )
                .favoritesId(
                        entity.getFavoritesId() != null
                                ? new ArrayList<>(entity.getFavoritesId())
                                : new ArrayList<>()
                )
                .reviewsId(
                        entity.getReviewsId() != null
                                ? new ArrayList<>(entity.getReviewsId())
                                : new ArrayList<>()
                )
                .ordersId(
                        entity.getOrdersId() != null
                                ? new ArrayList<>(entity.getOrdersId())
                                : new ArrayList<>()
                )
                .build();
    }

    public UserMongoEntity toEntity(User domain) {
        if (domain == null) return null;

        return UserMongoEntity.builder()
                .id(domain.getId())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .email(domain.getEmail())
                .password(domain.getPassword())
                .name(domain.getName())
                .picture(domain.getPicture())
                .storesId(
                        domain.getStoresId() != null
                                ? new ArrayList<>(domain.getStoresId())
                                : new ArrayList<>()
                )
                .favoritesId(
                        domain.getFavoritesId() != null
                                ? new ArrayList<>(domain.getFavoritesId())
                                : new ArrayList<>()
                )
                .reviewsId(
                        domain.getReviewsId() != null
                                ? new ArrayList<>(domain.getReviewsId())
                                : new ArrayList<>()
                )
                .ordersId(
                        domain.getOrdersId() != null
                                ? new ArrayList<>(domain.getOrdersId())
                                : new ArrayList<>()
                )
                .build();
    }

    @Override
    public List<User> toDomain(List<UserMongoEntity> entities) {
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<UserMongoEntity> toEntity(List<User> domains) {
        return domains.stream().map(this::toEntity).toList();
    }
}