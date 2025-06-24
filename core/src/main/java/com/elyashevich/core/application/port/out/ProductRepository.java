package com.elyashevich.core.application.port.out;

import com.elyashevich.core.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<Product> findAll();

    List<Product> findByStoreId(String storeId);

    List<Product> findByCategoryId(String categoryId);

    Optional<Product> findById(String id);

    Product create(Product product);

    void delete(Product product);
}