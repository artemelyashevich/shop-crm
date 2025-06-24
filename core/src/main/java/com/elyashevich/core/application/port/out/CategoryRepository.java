package com.elyashevich.core.application.port.out;

import com.elyashevich.core.domain.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    List<Category> findByStoreId(String storeId);

    Optional<Category> findById(String id);

    Category create(Category category);

    void delete(Category category);

    boolean existsByTitleAndStoreId(String title, String storeId);
}