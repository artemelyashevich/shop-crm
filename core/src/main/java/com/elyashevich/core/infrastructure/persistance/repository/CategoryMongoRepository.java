package com.elyashevich.core.infrastructure.persistance.repository;

import com.elyashevich.core.infrastructure.persistance.entity.CategoryMongoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CategoryMongoRepository extends MongoRepository<CategoryMongoEntity, String> {

    List<CategoryMongoEntity> findByStoreId(String storeId);

    boolean existsByTitleAndStoreId(String title, String storeId);
}
