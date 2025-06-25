package com.elyashevich.user.infrastructure.persistance.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMongoEntity {

    @Id
    private String id;
    
    @Field("created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Field("updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Indexed(unique = true)
    private String email;

    @Field
    private String password;
    
    @Builder.Default
    private String name = "Не указано";
    
    @Builder.Default
    private String picture = "/uploads/no-user-image.png";

    @Builder.Default
    private List<String> storesId = new ArrayList<>();
    
    @DBRef
    @Builder.Default
    private List<String> favoritesId = new ArrayList<>();
    
    @DBRef
    @Builder.Default
    private List<String> reviewsId = new ArrayList<>();
    
    @DBRef
    @Builder.Default
    private List<String> ordersId = new ArrayList<>();
}