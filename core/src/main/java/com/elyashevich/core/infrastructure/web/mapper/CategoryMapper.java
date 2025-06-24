package com.elyashevich.core.infrastructure.web.mapper;

import com.elyashevich.core.domain.model.Category;
import com.elyashevich.core.infrastructure.web.dto.category.CategoryRequestDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toModel(CategoryRequestDto dto) {
        return Category.builder()
                .title(dto.title())
                .description(dto.description())
                .build();
    }
}
