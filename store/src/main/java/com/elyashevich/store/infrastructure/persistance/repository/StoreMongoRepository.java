package com.elyashevich.store.infrastructure.persistance.repository;

import com.elyashevich.store.infrastructure.persistance.entity.StoreMongoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StoreMongoRepository extends MongoRepository<StoreMongoEntity, String> {
}
