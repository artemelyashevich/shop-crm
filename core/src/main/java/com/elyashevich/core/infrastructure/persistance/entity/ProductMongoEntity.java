package com.elyashevich.core.infrastructure.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductMongoEntity {

    @Id
    private String id;

    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Field("updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Field
    @Indexed
    private String title;

    @Field
    private String description;

    @Field
    private BigDecimal price;

    @Builder.Default
    private List<String> images = new ArrayList<>();

    @Field
    private String reviewId;

    @Field
    private String storeId;

    @DBRef
    private CategoryMongoEntity category;

    @DBRef
    private ColorMongoEntity color;
}