package com.elyashevich.core.infrastructure.web.dto.color;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ColorRequestDto(
        @NotNull(message = "Name is required")
        @NotBlank(message = "Name must be not empty")
        String name,

        @NotNull(message = "Value is required")
        @NotBlank(message = "Value must be not empty")
        String value
) {
}
