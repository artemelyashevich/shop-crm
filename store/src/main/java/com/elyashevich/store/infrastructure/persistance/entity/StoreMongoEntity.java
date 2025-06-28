package com.elyashevich.store.infrastructure.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "stores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreMongoEntity {
    @Id
    private String id;
    
    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Field("updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    private String title;

    private String description;

    private String userId;

    @Builder.Default
    private List<String> products = new ArrayList<>();
    
    @Builder.Default
    private List<String> categories = new ArrayList<>();
    
    @Builder.Default
    private List<String> colors = new ArrayList<>();
    
    @Builder.Default
    private List<String> reviews = new ArrayList<>();
    
    @Builder.Default
    private List<String> orderItems = new ArrayList<>();
}