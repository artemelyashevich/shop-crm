package com.elyashevich.core.infrastructure.persistance.adapter;

import com.elyashevich.core.application.port.out.ProductRepository;
import com.elyashevich.core.domain.model.Product;
import com.elyashevich.core.infrastructure.persistance.entity.ProductMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import com.elyashevich.core.infrastructure.persistance.repository.CategoryMongoRepository;
import com.elyashevich.core.infrastructure.persistance.repository.ColorMongoRepository;
import com.elyashevich.core.infrastructure.persistance.repository.ProductMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private final ProductMongoRepository productMongoRepository;
    private final EntityMapper<Product, ProductMongoEntity> productMapper;
    private final CategoryMongoRepository categoryMongoRepository;
    private final ColorMongoRepository colorMongoRepository;

    @Override
    public List<Product> findAll() {
        return productMapper.toDomain(productMongoRepository.findAll());
    }

    @Override
    public List<Product> findByStoreId(String storeId) {
        return productMapper.toDomain(
                productMongoRepository.findByStoreId(storeId)
        );
    }

    @Override
    public List<Product> findByCategoryId(String categoryId) {
        return productMapper.toDomain(
                productMongoRepository.findByCategoryId(categoryId)
        );
    }

    @Override
    public Optional<Product> findById(String id) {
        return productMongoRepository.findById(id)
                .map(productMapper::toDomain);
    }

    @Override
    @Transactional
    public Product create(Product product) {
        product.updateTimestamps();

        ProductMongoEntity entity = productMapper.toEntity(product);
        setRelations(product, entity);

        ProductMongoEntity savedEntity = productMongoRepository.save(entity);
        return productMapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Product product) {
        ProductMongoEntity entity = productMapper.toEntity(product);
        productMongoRepository.delete(entity);
    }

    private void setRelations(Product product, ProductMongoEntity entity) {
        if (product.getCategoryId() != null) {
            entity.setCategory(categoryMongoRepository.findById(product.getCategoryId()).orElse(null));
        }

        if (product.getColorId() != null) {
            entity.setColor(colorMongoRepository.findById(product.getColorId()).orElse(null));
        }
    }
}
