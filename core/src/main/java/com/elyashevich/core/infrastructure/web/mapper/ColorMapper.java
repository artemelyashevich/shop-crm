package com.elyashevich.core.infrastructure.web.mapper;

import com.elyashevich.core.domain.model.Color;
import com.elyashevich.core.infrastructure.web.dto.color.ColorRequestDto;
import org.springframework.stereotype.Component;

@Component
public class ColorMapper {

    public Color toModel(ColorRequestDto colorRequestDto) {
        return Color.builder()
                .name(colorRequestDto.name())
                .value(colorRequestDto.value())
                .build();
    }
}
