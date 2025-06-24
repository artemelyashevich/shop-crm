package com.elyashevich.core.infrastructure.persistance.adapter;

import com.elyashevich.core.application.port.out.ColorRepository;
import com.elyashevich.core.domain.model.Color;
import com.elyashevich.core.infrastructure.persistance.entity.ColorMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import com.elyashevich.core.infrastructure.persistance.repository.ColorMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ColorRepositoryAdapter implements ColorRepository {

    private final ColorMongoRepository colorMongoRepository;
    private final EntityMapper<Color, ColorMongoEntity> entityMapper;

    @Override
    public List<Color> findByStoreId(String storeId) {
        List<ColorMongoEntity> entities = colorMongoRepository.findByStoreId(storeId);
        return entityMapper.toDomain(entities);
    }

    @Override
    public Optional<Color> findById(String id) {
        return colorMongoRepository.findById(id)
                .map(entityMapper::toDomain);
    }

    @Override
    public Color create(Color color) {
        color.updateTimestamps();
        ColorMongoEntity entity = entityMapper.toEntity(color);
        ColorMongoEntity savedEntity = colorMongoRepository.save(entity);
        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Color color) {
        ColorMongoEntity entity = entityMapper.toEntity(color);
        colorMongoRepository.delete(entity);
    }

    @Override
    public boolean existsByNameAndStoreId(String name, String storeId) {
        return colorMongoRepository.existsByNameAndStoreId(name, storeId);
    }

    @Override
    public boolean existsByValueAndStoreId(String value, String storeId) {
        return colorMongoRepository.existsByValueAndStoreId(value, storeId);
    }
}
