package com.elyashevich.store.application.port.out;

import com.elyashevich.store.domain.model.Store;

import java.util.Optional;

public interface StoreRepository {

    Optional<Store> findById(String id);

    Store save(Store store);

    void delete(String id);
}
