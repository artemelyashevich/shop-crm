package com.elyashevich.core.application.port.out;

import com.elyashevich.core.domain.model.Color;

import java.util.List;
import java.util.Optional;

public interface ColorRepository {

    List<Color> findByStoreId(String storeId);

    Optional<Color> findById(String id);

    Color create(Color color);

    void delete(Color color);

    boolean existsByNameAndStoreId(String name, String storeId);

    boolean existsByValueAndStoreId(String value, String storeId);
}
