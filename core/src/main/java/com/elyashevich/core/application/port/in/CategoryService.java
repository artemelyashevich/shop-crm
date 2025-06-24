package com.elyashevich.core.application.port.in;

import com.elyashevich.core.domain.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findByStoreId(String storeId);

    Category findById(String id);

    Category create(Category category);

    Category update(String id, Category category);

    void delete(String id);
}
