package com.elyashevich.user.application.port.out;

import com.elyashevich.user.domain.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    User create(User user);
}
