package com.elyashevich.user.infrastructure.web.mapper;

import com.elyashevich.user.domain.model.User;
import com.elyashevich.user.infrastructure.web.dto.user.UserRequestDto;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserRequestDto dto) {
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .build();
    }
}
