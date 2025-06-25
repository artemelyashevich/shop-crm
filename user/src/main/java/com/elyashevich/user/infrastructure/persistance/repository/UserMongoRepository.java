package com.elyashevich.user.infrastructure.persistance.repository;

import com.elyashevich.user.infrastructure.persistance.entity.UserMongoEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserMongoRepository extends MongoRepository<UserMongoEntity, String> {

    Optional<UserMongoEntity> findByEmail(String email);
}
