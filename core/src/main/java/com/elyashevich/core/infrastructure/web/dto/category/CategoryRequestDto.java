package com.elyashevich.core.infrastructure.web.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequestDto(
        @NotNull(message = "Title is required")
        @NotBlank(message = "Title must be not empty")
        String title,

        @NotNull(message = "Description is required")
        @NotBlank(message = "Description must be not empty")
        String description
) {
}
