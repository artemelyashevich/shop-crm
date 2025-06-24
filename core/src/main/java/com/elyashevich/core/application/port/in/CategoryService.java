package com.elyashevich.core.application.port.in;

import com.elyashevich.core.infrastructure.persistance.entity.CategoryMongoEntity;

import java.util.List;

public interface CategoryService {

    List<CategoryMongoEntity> findByStoreId(String storeId);

    CategoryMongoEntity findById(String id);

    CategoryMongoEntity create(CategoryMongoEntity category);

    CategoryMongoEntity update(String id, CategoryMongoEntity category);

    void delete(String id);
}
