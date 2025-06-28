package com.elyashevich.store.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    private String id;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    private String title;

    private String description;

    private String userId;

    @Builder.Default
    private List<String> productIds = new ArrayList<>();

    @Builder.Default
    private List<String> categoryIds = new ArrayList<>();

    @Builder.Default
    private List<String> colorIds = new ArrayList<>();

    @Builder.Default
    private List<String> reviewIds = new ArrayList<>();

    @Builder.Default
    private List<String> orderItemIds = new ArrayList<>();

    public void updateTimestamps() {
        this.updatedAt = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void addProduct(String productId) {
        if (productIds == null) {
            productIds = new ArrayList<>();
        }
        productIds.add(productId);
    }

    public void addCategory(String categoryId) {
        if (categoryIds == null) {
            categoryIds = new ArrayList<>();
        }
        categoryIds.add(categoryId);
    }

    public void addColor(String colorId) {
        if (colorIds == null) {
            colorIds = new ArrayList<>();
        }
        colorIds.add(colorId);
    }
}