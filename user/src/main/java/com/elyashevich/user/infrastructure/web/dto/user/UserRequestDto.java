package com.elyashevich.user.infrastructure.web.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UserRequestDto(
        @NotNull(message = "Name is required")
        @NotBlank(message = "Name must be not empty")
        @Length(min = 1, max = 255, message = "Name must be in {min} and {max}")
        String name,

        @NotNull(message = "Email is required")
        @NotBlank(message = "Email must be not empty")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Password is required")
        @NotBlank(message = "Password must be not empty")
        @Length(min = 6, max = 255, message = "Password must be in {min} and {max}")
        String password
) {
}
