package com.elyashevich.user.application.service;

import com.elyashevich.user.application.port.in.UserService;
import com.elyashevich.user.application.port.out.UserRepository;
import com.elyashevich.user.domain.exception.ResourceAlreadyExistsException;
import com.elyashevich.user.domain.exception.ResourceNotFoundException;
import com.elyashevich.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    public static final String USER_WITH_ID_NOT_FOUND_TEMPLATE = "User with id '%s' not found'";
    public static final String USER_WITH_EMAIL_NOT_FOUND_TEMPLATE = "User with email '%s' not found";
    private final UserRepository userRepository;

    @Override
    public User findById(String id) {
        log.debug("Attempting to find user with id {}", id);

        User user = userRepository.findById(id).orElseThrow(() -> {
            String message = USER_WITH_ID_NOT_FOUND_TEMPLATE.formatted(id);
            log.info(message);
            return new ResourceNotFoundException(message);
        });

        log.info("User with id {} found", id);
        return user;
    }

    @Override
    public User findByEmail(String email) {
        log.debug("Attempting to find user with email {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> {
            String message = USER_WITH_EMAIL_NOT_FOUND_TEMPLATE.formatted(email);
            log.info(message);
            return new ResourceNotFoundException(message);
        });

        log.info("User with email {} found", email);
        return user;
    }

    @Override
    @Transactional
    public User create(User user) {
        log.debug("Attempting to create user {}", user);

        String email = user.getEmail();

        if (userRepository.existsByEmail(email)) {
            String message = "User with email '%s' already exists".formatted(email);
            log.info(message);
            throw new ResourceAlreadyExistsException(message);
        }

        User newUser = userRepository.create(user);

        log.info("User with email {} created", email);
        return newUser;
    }
}
