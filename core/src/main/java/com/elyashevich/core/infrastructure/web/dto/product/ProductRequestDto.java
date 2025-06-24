package com.elyashevich.core.infrastructure.web.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequestDto(
        @NotBlank(message = "Title is required")
        @Size(max = 100, message = "Title must be less than 100 characters")
        String title,

        @NotBlank(message = "Description is required")
        @Size(max = 1000, message = "Description must be less than 1000 characters")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        BigDecimal price,

        @NotEmpty(message = "At least one image is required")
        List<@NotBlank(message = "Image URL cannot be blank") String> images,

        @NotBlank(message = "Category ID is required")
        String categoryId,

        @NotBlank(message = "Color ID is required")
        String colorId
) {
}