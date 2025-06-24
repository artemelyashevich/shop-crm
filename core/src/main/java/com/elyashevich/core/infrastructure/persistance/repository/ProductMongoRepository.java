package com.elyashevich.core.infrastructure.persistance.repository;

import com.elyashevich.core.infrastructure.persistance.entity.ProductMongoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductMongoRepository extends MongoRepository<ProductMongoEntity, String> {

    List<ProductMongoEntity> findByStoreId(String storeId);

    List<ProductMongoEntity> findByCategoryId(String categoryId);
}
