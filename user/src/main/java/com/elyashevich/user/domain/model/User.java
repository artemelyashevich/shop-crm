package com.elyashevich.user.domain.model;

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
public class User {

    private String id;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    private String email;

    private String password;

    @Builder.Default
    private String name = "Не указано";

    @Builder.Default
    private String picture = "/uploads/no-user-image.png";

    @Builder.Default
    private List<String> storesId = new ArrayList<>();

    @Builder.Default
    private List<String> favoritesId = new ArrayList<>();

    @Builder.Default
    private List<String> reviewsId = new ArrayList<>();

    @Builder.Default
    private List<String> ordersId = new ArrayList<>();

    public void updateTimestamps() {
        this.updatedAt = LocalDateTime.now();
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public void addStore(String storeId) {
        if (storesId == null) {
            storesId = new ArrayList<>();
        }
        storesId.add(storeId);
    }

    public void addFavorite(String productId) {
        if (favoritesId == null) {
            favoritesId = new ArrayList<>();
        }
        favoritesId.add(productId);
    }
}