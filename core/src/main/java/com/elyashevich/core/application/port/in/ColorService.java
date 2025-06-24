package com.elyashevich.core.application.port.in;

import com.elyashevich.core.domain.model.Color;

public interface ColorService {

    Color findByStoreId(String storeId);

    Color findById(String id);

    Color create(Color color);

    Color update(String id, Color color);

    void delete(String id);
}
