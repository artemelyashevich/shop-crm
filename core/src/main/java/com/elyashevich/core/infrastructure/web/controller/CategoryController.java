package com.elyashevich.core.infrastructure.web.controller;

import com.elyashevich.core.application.port.in.CategoryService;
import com.elyashevich.core.domain.model.Category;
import com.elyashevich.core.infrastructure.web.dto.category.CategoryRequestDto;
import com.elyashevich.core.infrastructure.web.mapper.CategoryMapper;
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
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping("/store/{id}")
    public List<Category> findCategoriesByStoreId(@PathVariable String id) {
        return categoryService.findByStoreId(id);
    }

    @GetMapping("/{id}")
    public Category findCategoryById(@PathVariable String id) {
        return categoryService.findById(id);
    }

    @PostMapping("/{storeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Category createCategory(@PathVariable String storeId, @Valid @RequestBody CategoryRequestDto dto) {
        Category category = categoryMapper.toModel(dto);
        category.setStoreId(storeId);
        return categoryService.create(category);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Category updateCategory(@PathVariable String id, @Valid @RequestBody CategoryRequestDto dto) {
        Category category = categoryMapper.toModel(dto);
        return categoryService.update(id, category);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable String id) {
        categoryService.delete(id);
    }
}

