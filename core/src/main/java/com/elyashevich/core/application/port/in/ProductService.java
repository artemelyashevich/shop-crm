package com.elyashevich.core.application.port.in;

import com.elyashevich.core.infrastructure.persistance.entity.ProductMongoEntity;

import java.util.List;

public interface ProductService {

    List<ProductMongoEntity> findAll();

    List<ProductMongoEntity> findByStoreId(String storeId);

    List<ProductMongoEntity> findByCategoryId(String categoryId);

    ProductMongoEntity findById(String id);

    ProductMongoEntity create(ProductMongoEntity product);

    ProductMongoEntity update(String productId, ProductMongoEntity product);

    void delete(String productId);
}
