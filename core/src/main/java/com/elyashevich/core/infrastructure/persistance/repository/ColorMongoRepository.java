package com.elyashevich.core.infrastructure.persistance.repository;

import com.elyashevich.core.infrastructure.persistance.entity.ColorMongoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ColorMongoRepository extends MongoRepository<ColorMongoEntity, String> {

    List<ColorMongoEntity> findByStoreId(String storeId);

    boolean existsByNameAndStoreId(String name, String storeId);

    boolean existsByValueAndStoreId(String value, String storeId);
}
