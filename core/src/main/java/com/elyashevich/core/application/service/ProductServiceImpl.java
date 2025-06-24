package com.elyashevich.core.application.service;

import com.elyashevich.core.application.port.in.ProductService;
import com.elyashevich.core.application.port.out.ProductRepository;
import com.elyashevich.core.domain.exception.ResourceNotFoundException;
import com.elyashevich.core.domain.model.Product;
import com.elyashevich.core.infrastructure.persistance.entity.ProductMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    public static final String PRODUCT_WITH_ID_NOT_FOUND_TEMPLATE = "Product with id '%s' not found";

    private final ProductRepository productRepository;
    private final EntityMapper<Product, ProductMongoEntity> productMapper;

    @Override
    public List<Product> findAll() {
        log.debug("Attempting to find all products");

        List<Product> products = productRepository.findAll();

        log.info("Found {} products", products.size());
        return products;
    }

    @Override
    public List<Product> findByStoreId(String storeId) {
        log.debug("Attempting to find all products by store id {}", storeId);

        List<Product> products = productRepository.findByStoreId(storeId);

        log.info("Found {} products by store id: {}", products.size(), storeId);
        return products;
    }

    @Override
    public List<Product> findByCategoryId(String categoryId) {
        log.debug("Attempting to find all products by category id {}", categoryId);

        List<Product> products = productRepository.findByCategoryId(categoryId);

        log.info("Found {} products by category id {}", products.size(), categoryId);
        return products;
    }

    @Override
    public Product findById(String id) {
        log.debug("Attempting to find product by id {}", id);

        Product product = productRepository.findById(id).orElseThrow(() -> {
            String message = PRODUCT_WITH_ID_NOT_FOUND_TEMPLATE.formatted(id);
            log.info(message);
            return new ResourceNotFoundException(message);
        });

        log.info("Found product by id {}", product);
        return product;
    }

    @Override
    @Transactional
    // TODO: impl images uploading with gridfs
    public Product create(Product product) {
        log.debug("Attempting to create product {}", product);

        Product newProduct = productRepository.create(product);

        log.info("Created product {}", newProduct);

        return newProduct;
    }

    @Override
    @Transactional
    public Product update(String productId, Product product) {
        log.debug("Attempting to update product with id {}", productId);

        Product oldProduct = findById(productId);

        updateProductFields(oldProduct, product);

        Product updatedProduct = productRepository.create(oldProduct);

        log.info("Updated product with id {}", updatedProduct);
        return updatedProduct;
    }

    @Override
    @Transactional
    public void delete(String productId) {
        log.debug("Attempting to delete product with id {}", productId);

        Product product = findById(productId);

        productRepository.delete(product);

        log.info("Deleted product with id {}", product);
    }

    private void updateProductFields(Product existing, Product updateData) {
        existing.setImages(updateData.getImages());
        existing.setPrice(updateData.getPrice());
        existing.setTitle(updateData.getTitle());
        existing.setDescription(updateData.getDescription());
        existing.setCategoryId(updateData.getCategoryId());
        existing.setColorId(updateData.getColorId());
    }
}
