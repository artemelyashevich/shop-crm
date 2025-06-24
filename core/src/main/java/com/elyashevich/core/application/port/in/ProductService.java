package com.elyashevich.core.application.port.in;

import com.elyashevich.core.domain.model.Product;

import java.util.List;

public interface ProductService {

    List<Product> findAll();

    List<Product> findByStoreId(String storeId);

    List<Product> findByCategoryId(String categoryId);

    Product findById(String id);

    Product create(Product product);

    Product update(String productId, Product product);

    void delete(String productId);
}
