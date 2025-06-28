package com.elyashevich.store.infrastructure.persistance.adapter;

import com.elyashevich.store.application.port.out.StoreRepository;
import com.elyashevich.store.domain.model.Store;
import com.elyashevich.store.infrastructure.persistance.entity.StoreMongoEntity;
import com.elyashevich.store.infrastructure.persistance.mapper.EntityMapper;
import com.elyashevich.store.infrastructure.persistance.repository.StoreMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StoreRepositoryAdapter implements StoreRepository {

    private final StoreMongoRepository storeRepository;
    private final EntityMapper<Store, StoreMongoEntity> storeEntityMapper;

    @Override
    public Optional<Store> findById(String id) {
        return storeRepository.findById(id).map(storeEntityMapper::toDomain);
    }

    @Override
    public Store save(Store store) {
        StoreMongoEntity entity = storeRepository.save(storeEntityMapper.toEntity(store));
        return storeEntityMapper.toDomain(entity);
    }

    @Override
    public void delete(String id) {
        storeRepository.deleteById(id);
    }
}
