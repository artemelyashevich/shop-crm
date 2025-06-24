package com.elyashevich.core.infrastructure.persistance.adapter;

import com.elyashevich.core.domain.model.Color;
import com.elyashevich.core.infrastructure.persistance.entity.ColorMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import com.elyashevich.core.infrastructure.persistance.repository.ColorMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColorRepositoryAdapterTest {

    @Mock
    private ColorMongoRepository colorMongoRepository;

    @Mock
    private EntityMapper<Color, ColorMongoEntity> entityMapper;

    @InjectMocks
    private ColorRepositoryAdapter colorRepositoryAdapter;

    // Test data providers
    private static Stream<Arguments> provideColorsForFindByStoreId() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        "store-1",
                        List.of(
                                createTestEntity("color-1", "Red", "#FF0000", "store-1", now.minusDays(1)),
                                createTestEntity("color-2", "Blue", "#0000FF", "store-1", now.minusHours(1))
                        ),
                        List.of(
                                createTestColor("color-1", "Red", "#FF0000", "store-1", now.minusDays(1)),
                                createTestColor("color-2", "Blue", "#0000FF", "store-1", now.minusHours(1))
                        )
                ),
                Arguments.of(
                        "empty-store",
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );
    }

    private static Stream<Arguments> provideColorsForFindById() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        "existing-id",
                        Optional.of(createTestEntity("existing-id", "Green", "#00FF00", "store-1", now)),
                        Optional.of(createTestColor("existing-id", "Green", "#00FF00", "store-1", now))
                ),
                Arguments.of(
                        "non-existing-id",
                        Optional.empty(),
                        Optional.empty()
                )
        );
    }

    private static Stream<Arguments> provideColorsForCreate() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        createTestColor(null, "New Color", "#FFFFFF", "store-1", null),
                        createTestEntity(null, "New Color", "#FFFFFF", "store-1", now),
                        createTestEntity("saved-id", "New Color", "#FFFFFF", "store-1", now),
                        createTestColor("saved-id", "New Color", "#FFFFFF", "store-1", now)
                ),
                Arguments.of(
                        createTestColor("existing-id", "Existing Color", "#000000", "store-1", now.minusDays(1)),
                        createTestEntity("existing-id", "Existing Color", "#000000", "store-1", now),
                        createTestEntity("existing-id", "Existing Color", "#000000", "store-1", now),
                        createTestColor("existing-id", "Existing Color", "#000000", "store-1", now)
                )
        );
    }

    private static Stream<Arguments> provideColorsForDelete() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(createTestColor("color-1", "To Delete", "#111111", "store-1", now)),
                Arguments.of(createTestColor("color-2", "Another Delete", "#222222", "store-2", now))
        );
    }

    private static Stream<Arguments> provideNameAndStoreIdForExists() {
        return Stream.of(
                Arguments.of("Existing Name", "store-1", true),
                Arguments.of("Non-existing Name", "store-1", false),
                Arguments.of("Existing Name", "different-store", false)
        );
    }

    private static Stream<Arguments> provideValueAndStoreIdForExists() {
        return Stream.of(
                Arguments.of("#FF0000", "store-1", true),
                Arguments.of("#00FF00", "store-1", false),
                Arguments.of("#FF0000", "different-store", false)
        );
    }

    // Helper methods for creating test data
    private static ColorMongoEntity createTestEntity(String id, String name, String value, String storeId, LocalDateTime createdAt) {
        ColorMongoEntity entity = new ColorMongoEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setValue(value);
        entity.setStoreId(storeId);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(createdAt);
        return entity;
    }

    private static Color createTestColor(String id, String name, String value, String storeId, LocalDateTime createdAt) {
        return Color.builder()
                .id(id)
                .name(name)
                .value(value)
                .storeId(storeId)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideColorsForFindByStoreId")
    void findByStoreId_WithVariousStoreIds_ReturnsMatchingColors(
            String storeId,
            List<ColorMongoEntity> entities,
            List<Color> expectedColors
    ) {
        // Arrange
        when(colorMongoRepository.findByStoreId(storeId)).thenReturn(entities);
        when(entityMapper.toDomain(entities)).thenReturn(expectedColors);

        // Act
        List<Color> result = colorRepositoryAdapter.findByStoreId(storeId);

        // Assert
        assertEquals(expectedColors, result);
        verify(colorMongoRepository).findByStoreId(storeId);
        verify(entityMapper).toDomain(entities);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void findByStoreId_WithNullOrEmptyStoreId_ReturnsEmptyList(String invalidStoreId) {
        // Arrange
        when(colorMongoRepository.findByStoreId(invalidStoreId)).thenReturn(Collections.emptyList());
        when(entityMapper.toDomain(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<Color> result = colorRepositoryAdapter.findByStoreId(invalidStoreId);

        // Assert
        assertTrue(result.isEmpty());
        verify(colorMongoRepository).findByStoreId(invalidStoreId);
        verify(entityMapper).toDomain(Collections.emptyList());
    }

    @ParameterizedTest
    @MethodSource("provideColorsForFindById")
    void findById_WithVariousIds_ReturnsMatchingColor(
            String id,
            Optional<ColorMongoEntity> entity,
            Optional<Color> expectedColor
    ) {
        // Arrange
        when(colorMongoRepository.findById(id)).thenReturn(entity);
        if (entity.isPresent()) {
            when(entityMapper.toDomain(entity.get())).thenReturn(expectedColor.get());
        }

        // Act
        Optional<Color> result = colorRepositoryAdapter.findById(id);

        // Assert
        assertEquals(expectedColor, result);
        verify(colorMongoRepository).findById(id);
        if (entity.isPresent()) {
            verify(entityMapper).toDomain(entity.get());
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void findById_WithNullOrEmptyId_ReturnsEmptyOptional(String invalidId) {
        // Act
        Optional<Color> result = colorRepositoryAdapter.findById(invalidId);

        // Assert
        assertTrue(result.isEmpty());
        verify(entityMapper, never()).toDomain((ColorMongoEntity) any());
    }

    @ParameterizedTest
    @MethodSource("provideColorsForCreate")
    void create_WithVariousColors_ReturnsSavedColor(
            Color inputColor,
            ColorMongoEntity entityToSave,
            ColorMongoEntity savedEntity,
            Color expectedColor
    ) {
        // Arrange
        when(entityMapper.toEntity(inputColor)).thenReturn(entityToSave);
        when(colorMongoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(entityMapper.toDomain(savedEntity)).thenReturn(expectedColor);

        // Act
        Color result = colorRepositoryAdapter.create(inputColor);

        // Assert
        assertAll(
                () -> assertEquals(expectedColor.getId(), result.getId()),
                () -> assertEquals(expectedColor.getName(), result.getName()),
                () -> assertEquals(expectedColor.getValue(), result.getValue()),
                () -> assertEquals(expectedColor.getStoreId(), result.getStoreId()),
                () -> assertNotNull(result.getCreatedAt()),
                () -> assertNotNull(result.getUpdatedAt())
        );
        verify(entityMapper).toEntity(inputColor);
        verify(colorMongoRepository).save(entityToSave);
        verify(entityMapper).toDomain(savedEntity);
    }

    @Test
    void create_WithNullColor_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> colorRepositoryAdapter.create(null));
        verifyNoInteractions(entityMapper);
        verifyNoInteractions(colorMongoRepository);
    }

    @ParameterizedTest
    @MethodSource("provideColorsForDelete")
    void delete_WithValidColor_DeletesEntity(Color color) {
        // Arrange
        ColorMongoEntity entity = createTestEntity(
                color.getId(),
                color.getName(),
                color.getValue(),
                color.getStoreId(),
                color.getCreatedAt()
        );
        when(entityMapper.toEntity(color)).thenReturn(entity);

        // Act
        colorRepositoryAdapter.delete(color);

        // Assert
        verify(entityMapper).toEntity(color);
        verify(colorMongoRepository).delete(entity);
    }

    @ParameterizedTest
    @MethodSource("provideNameAndStoreIdForExists")
    void existsByNameAndStoreId_WithVariousInputs_ReturnsExpectedResult(
            String name,
            String storeId,
            boolean expectedResult
    ) {
        // Arrange
        when(colorMongoRepository.existsByNameAndStoreId(name, storeId))
                .thenReturn(expectedResult);

        // Act
        boolean result = colorRepositoryAdapter.existsByNameAndStoreId(name, storeId);

        // Assert
        assertEquals(expectedResult, result);
        verify(colorMongoRepository).existsByNameAndStoreId(name, storeId);
    }

    @ParameterizedTest
    @MethodSource("provideValueAndStoreIdForExists")
    void existsByValueAndStoreId_WithVariousInputs_ReturnsExpectedResult(
            String value,
            String storeId,
            boolean expectedResult
    ) {
        // Arrange
        when(colorMongoRepository.existsByValueAndStoreId(value, storeId))
                .thenReturn(expectedResult);

        // Act
        boolean result = colorRepositoryAdapter.existsByValueAndStoreId(value, storeId);

        // Assert
        assertEquals(expectedResult, result);
        verify(colorMongoRepository).existsByValueAndStoreId(value, storeId);
    }

}