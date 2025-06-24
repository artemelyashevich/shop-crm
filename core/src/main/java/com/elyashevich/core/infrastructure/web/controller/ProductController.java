package com.elyashevich.core.infrastructure.web.controller;

import com.elyashevich.core.application.port.in.ProductService;
import com.elyashevich.core.domain.model.Product;
import com.elyashevich.core.infrastructure.web.dto.product.ProductRequestDto;
import com.elyashevich.core.infrastructure.web.mapper.ProductMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductMapper productMapper;

    @GetMapping
    public List<Product> findAll() {
        return productService.findAll();
    }

    @GetMapping("/store/{id}")
    public List<Product> findAllByStoreId(@PathVariable String id) {
        return productService.findByStoreId(id);
    }

    @GetMapping("/category/{id}")
    public List<Product> findAllByCategoryId(@PathVariable String id) {
        return productService.findByCategoryId(id);
    }

    @GetMapping("/{id}")
    public Product findById(@PathVariable String id) {
        return productService.findById(id);
    }

    @PostMapping("/{storeId}")
    @ResponseStatus(HttpStatus.CREATED)
    public Product save(@PathVariable String storeId, @Valid @RequestBody ProductRequestDto dto) {
        Product product = productMapper.toModel(dto);
        product.setStoreId(storeId);
        return productService.create(product);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Product update(@PathVariable String id, @Valid @RequestBody ProductRequestDto dto) {
        Product product = productMapper.toModel(dto);
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        productService.delete(id);
    }
}
