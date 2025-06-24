package com.elyashevich.core.infrastructure.web.controller;

import com.elyashevich.core.application.port.in.ColorService;
import com.elyashevich.core.domain.model.Color;
import com.elyashevich.core.infrastructure.web.dto.color.ColorRequestDto;
import com.elyashevich.core.infrastructure.web.mapper.ColorMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/colors")
@RequiredArgsConstructor
public class ColorController {

    private final ColorService colorService;
    private final ColorMapper colorMapper;

    @GetMapping("/store/{id}")
    public List<Color> findAllByStoreId(@PathVariable String id) {
        return colorService.findByStoreId(id);
    }

    @GetMapping("/{id}")
    public Color findById(@PathVariable String id) {
        return colorService.findById(id);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Color create(@PathVariable String id, @Valid @RequestBody ColorRequestDto dto) {
        Color color = colorMapper.toModel(dto);
        color.setStoreId(id);
        return colorService.create(color);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Color update(@PathVariable String id, @Valid @RequestBody ColorRequestDto dto) {
        Color color = colorMapper.toModel(dto);
        return  colorService.update(id, color);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        colorService.delete(id);
    }
}
