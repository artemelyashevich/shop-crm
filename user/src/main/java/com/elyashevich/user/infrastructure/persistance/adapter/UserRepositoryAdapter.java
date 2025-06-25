package com.elyashevich.user.infrastructure.persistance.adapter;

import com.elyashevich.user.application.port.out.UserRepository;
import com.elyashevich.user.domain.model.User;
import com.elyashevich.user.infrastructure.persistance.entity.UserMongoEntity;
import com.elyashevich.user.infrastructure.persistance.mapper.EntityMapper;
import com.elyashevich.user.infrastructure.persistance.repository.UserMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserMongoRepository userMongoRepository;
    private final EntityMapper<User, UserMongoEntity> userEntityMapper;

    @Override
    public Optional<User> findById(String id) {
        return userMongoRepository.findById(id)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMongoRepository.findByEmail(email)
                .map(userEntityMapper::toDomain);
    }

    @Override
    public User create(User user) {
        UserMongoEntity userMongoEntity = userEntityMapper.toEntity(user);
        UserMongoEntity newUser = userMongoRepository.save(userMongoEntity);
        return userEntityMapper.toDomain(newUser);
    }
}
