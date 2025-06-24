package com.elyashevich.core.application.service;

import com.elyashevich.core.application.port.in.CategoryService;
import com.elyashevich.core.application.port.out.CategoryRepository;
import com.elyashevich.core.domain.exception.ResourceAlreadyExistsException;
import com.elyashevich.core.domain.exception.ResourceNotFoundException;
import com.elyashevich.core.domain.model.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    public static final String CATEGORY_WITH_ID_NOT_FOUND_TEMPLATE = "Category with id: '%s' not found";
    public static final String CATEGORY_ALREADY_EXISTS_WITH_TITLE_IN_STORE_WITH_ID_TEMPLATE =
            "Category already exists with title: '%s' in store with id: '%s'";

    private final CategoryRepository categoryRepository;

    @Override
    public List<Category> findByStoreId(String storeId) {
        log.debug("Attempting to find all categories for store id {}", storeId);

        List<Category> categories = categoryRepository.findByStoreId(storeId);

        log.info("Found {} categories for store id {}", categories.size(), storeId);
        return categories;
    }

    @Override
    public Category findById(String id) {
        log.debug("Attempting to find category by id {}", id);

        Category category = categoryRepository.findById(id).orElseThrow(() -> {
            String message = CATEGORY_WITH_ID_NOT_FOUND_TEMPLATE.formatted(id);
            log.info(message);
            return new ResourceNotFoundException(message);
        });

        log.info("Found category with id {}", category.getId());
        return category;
    }

    @Override
    @Transactional
    public Category create(Category category) {
        log.debug("Attempting to create category {}", category);

        String title = category.getTitle();
        String storeId = category.getStoreId();

        if (categoryRepository.existsByTitleAndStoreId(title, storeId)) {
            throw new ResourceAlreadyExistsException(
                    CATEGORY_ALREADY_EXISTS_WITH_TITLE_IN_STORE_WITH_ID_TEMPLATE
                            .formatted(title, storeId)
            );
        }

        category.updateTimestamps();

        Category newCategory = categoryRepository.create(category);

        log.info("Created category {}", newCategory);
        return newCategory;
    }

    @Override
    @Transactional
    public Category update(String id, Category category) {
        log.debug("Attempting to update category with id {}", id);

        Category oldCategory = findById(id);

        updateCategoryFields(oldCategory, category);

        Category updatedCategory = categoryRepository.create(oldCategory);

        log.info("Updated category with id {}", updatedCategory);
        return category;
    }

    @Override
    @Transactional
    public void delete(String id) {
        log.debug("Attempting to delete category with id {}", id);

        Category category = findById(id);

        categoryRepository.delete(category);

        log.info("Deleted category with id {}", category.getId());
    }

    private void updateCategoryFields(Category existing, Category updateData) {
        existing.setTitle(updateData.getTitle());
        existing.setDescription(updateData.getDescription());
        existing.updateTimestamps();
    }
}
