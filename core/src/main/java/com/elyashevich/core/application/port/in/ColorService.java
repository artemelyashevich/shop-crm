package com.elyashevich.core.application.port.in;

import com.elyashevich.core.infrastructure.persistance.entity.ColorMongoEntity;

public interface ColorService {

    ColorMongoEntity findByStoreId(String storeId);

    ColorMongoEntity findById(String id);

    ColorMongoEntity create(ColorMongoEntity color);

    ColorMongoEntity update(String id, ColorMongoEntity color);

    void delete(String id);
}
