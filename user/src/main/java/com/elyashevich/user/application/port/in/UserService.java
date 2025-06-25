package com.elyashevich.user.application.port.in;

import com.elyashevich.user.domain.model.User;

public interface UserService {

    User findById(String id);

    User findByEmail(String email);

    User create(User user);
}
