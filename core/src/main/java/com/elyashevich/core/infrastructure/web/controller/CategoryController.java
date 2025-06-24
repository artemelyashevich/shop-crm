package com.elyashevich.core.infrastructure.web.controller;

import com.elyashevich.core.application.port.in.CategoryService;
import com.elyashevich.core.domain.model.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    public List<Category> getCategories() {
        return null;
    }
}
