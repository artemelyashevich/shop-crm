package com.elyashevich.core.infrastructure.persistance.adapter;

import com.elyashevich.core.application.port.out.CategoryRepository;
import com.elyashevich.core.domain.model.Category;
import com.elyashevich.core.infrastructure.persistance.entity.CategoryMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import com.elyashevich.core.infrastructure.persistance.repository.CategoryMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryAdapter implements CategoryRepository {

    private final CategoryMongoRepository categoryMongoRepository;
    private final EntityMapper<Category, CategoryMongoEntity> categoryMapper;

    @Override
    public List<Category> findByStoreId(String storeId) {
        List<CategoryMongoEntity> entities = categoryMongoRepository.findByStoreId(storeId);
        return categoryMapper.toDomain(entities);
    }

    @Override
    public Optional<Category> findById(String id) {
        return categoryMongoRepository.findById(id)
                .map(categoryMapper::toDomain);
    }

    @Override
    public Category create(Category category) {
        category.updateTimestamps();
        CategoryMongoEntity entity = categoryMapper.toEntity(category);
        CategoryMongoEntity savedEntity = categoryMongoRepository.save(entity);
        return categoryMapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Category category) {
        CategoryMongoEntity entity = categoryMapper.toEntity(category);
        categoryMongoRepository.delete(entity);
    }

    @Override
    public boolean existsByTitleAndStoreId(String title, String storeId) {
        return categoryMongoRepository.existsByTitleAndStoreId(title, storeId);
    }
}
