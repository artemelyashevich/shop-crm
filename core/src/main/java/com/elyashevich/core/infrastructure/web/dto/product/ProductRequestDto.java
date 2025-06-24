package com.elyashevich.core.infrastructure.web.dto.product;

import java.math.BigDecimal;
import java.util.List;

public record ProductRequestDto(
        String title,
        String description,
        BigDecimal price,
        List<String> images,
        String categoryId,
        String colorId
) {
}
