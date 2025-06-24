package com.elyashevich.core.infrastructure.persistance.mapper;

import java.util.List;

public interface EntityMapper<D, E> {

    D toDomain(E entity);

    E toEntity(D domain);

    void updateEntity(D domain, E entity);

    List<D> toDomain(List<E> entities);

    List<E> toEntity(List<D> domains);
}