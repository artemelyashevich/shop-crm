package com.elyashevich.core.infrastructure.persistance.adapter;

import com.elyashevich.core.domain.model.Category;
import com.elyashevich.core.infrastructure.persistance.entity.CategoryMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import com.elyashevich.core.infrastructure.persistance.repository.CategoryMongoRepository;
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
class CategoryRepositoryAdapterTest {

    @Mock
    private CategoryMongoRepository categoryMongoRepository;

    @Mock
    private EntityMapper<Category, CategoryMongoEntity> categoryMapper;

    @InjectMocks
    private CategoryRepositoryAdapter categoryRepositoryAdapter;

    // Test data providers
    private static Stream<Arguments> provideCategoriesForFindByStoreId() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        "store-1",
                        List.of(
                                createTestEntity("cat-1", "Category 1", "store-1", now.minusDays(1)),
                                createTestEntity("cat-2", "Category 2", "store-1", now.minusHours(1))
                        ),
                        List.of(
                                createTestCategory("cat-1", "Category 1", "store-1", now.minusDays(1)),
                                createTestCategory("cat-2", "Category 2", "store-1", now.minusHours(1))
                        )
                ),
                Arguments.of(
                        "empty-store",
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );
    }

    private static Stream<Arguments> provideCategoriesForFindById() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        "existing-id",
                        Optional.of(createTestEntity("existing-id", "Existing", "store-1", now)),
                        Optional.of(createTestCategory("existing-id", "Existing", "store-1", now))
                ),
                Arguments.of(
                        "non-existing-id",
                        Optional.empty(),
                        Optional.empty()
                )
        );
    }

    private static Stream<Arguments> provideCategoriesForCreate() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        createTestCategory(null, "New Category", "store-1", null),
                        createTestEntity(null, "New Category", "store-1", now),
                        createTestEntity("saved-id", "New Category", "store-1", now),
                        createTestCategory("saved-id", "New Category", "store-1", now)
                ),
                Arguments.of(
                        createTestCategory("existing-id", "Existing Category", "store-1", now.minusDays(1)),
                        createTestEntity("existing-id", "Existing Category", "store-1", now),
                        createTestEntity("existing-id", "Existing Category", "store-1", now),
                        createTestCategory("existing-id", "Existing Category", "store-1", now)
                )
        );
    }

    private static Stream<Arguments> provideCategoriesForDelete() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(createTestCategory("cat-1", "To Delete", "store-1", now)),
                Arguments.of(createTestCategory("cat-2", "Another Delete", "store-2", now))
        );
    }

    private static Stream<Arguments> provideTitleAndStoreIdForExists() {
        return Stream.of(
                Arguments.of("Existing Title", "store-1", true),
                Arguments.of("Non-existing Title", "store-1", false),
                Arguments.of("Existing Title", "different-store", false)
        );
    }

    // Helper methods for creating test data
    private static CategoryMongoEntity createTestEntity(String id, String title, String storeId, LocalDateTime createdAt) {
        CategoryMongoEntity entity = new CategoryMongoEntity();
        entity.setId(id);
        entity.setTitle(title);
        entity.setStoreId(storeId);
        entity.setCreatedAt(createdAt);
        entity.setUpdatedAt(createdAt);
        return entity;
    }

    private static Category createTestCategory(String id, String title, String storeId, LocalDateTime createdAt) {
        return Category.builder()
                .id(id)
                .title(title)
                .storeId(storeId)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .build();
    }

    @ParameterizedTest
    @MethodSource("provideCategoriesForFindByStoreId")
    void findByStoreId_WithVariousStoreIds_ReturnsMatchingCategories(
            String storeId,
            List<CategoryMongoEntity> entities,
            List<Category> expectedCategories
    ) {
        // Arrange
        when(categoryMongoRepository.findByStoreId(storeId)).thenReturn(entities);
        when(categoryMapper.toDomain(entities)).thenReturn(expectedCategories);

        // Act
        List<Category> result = categoryRepositoryAdapter.findByStoreId(storeId);

        // Assert
        assertEquals(expectedCategories, result);
        verify(categoryMongoRepository).findByStoreId(storeId);
        verify(categoryMapper).toDomain(entities);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void findByStoreId_WithNullOrEmptyStoreId_ReturnsEmptyList(String invalidStoreId) {
        // Arrange
        when(categoryMongoRepository.findByStoreId(invalidStoreId)).thenReturn(Collections.emptyList());
        when(categoryMapper.toDomain(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<Category> result = categoryRepositoryAdapter.findByStoreId(invalidStoreId);

        // Assert
        assertTrue(result.isEmpty());
        verify(categoryMongoRepository).findByStoreId(invalidStoreId);
        verify(categoryMapper).toDomain(Collections.emptyList());
    }

    @ParameterizedTest
    @MethodSource("provideCategoriesForFindById")
    void findById_WithVariousIds_ReturnsMatchingCategory(
            String id,
            Optional<CategoryMongoEntity> entity,
            Optional<Category> expectedCategory
    ) {
        // Arrange
        when(categoryMongoRepository.findById(id)).thenReturn(entity);
        if (entity.isPresent()) {
            when(categoryMapper.toDomain(entity.get())).thenReturn(expectedCategory.get());
        }

        // Act
        Optional<Category> result = categoryRepositoryAdapter.findById(id);

        // Assert
        assertEquals(expectedCategory, result);
        verify(categoryMongoRepository).findById(id);
        if (entity.isPresent()) {
            verify(categoryMapper).toDomain(entity.get());
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void findById_WithNullOrEmptyId_ReturnsEmptyOptional(String invalidId) {
        // Act
        Optional<Category> result = categoryRepositoryAdapter.findById(invalidId);

        // Assert
        assertTrue(result.isEmpty());
        verify(categoryMapper, never()).toDomain((CategoryMongoEntity) any());
    }

    @ParameterizedTest
    @MethodSource("provideCategoriesForCreate")
    void create_WithVariousCategories_ReturnsSavedCategory(
            Category inputCategory,
            CategoryMongoEntity entityToSave,
            CategoryMongoEntity savedEntity,
            Category expectedCategory
    ) {
        // Arrange
        when(categoryMapper.toEntity(inputCategory)).thenReturn(entityToSave);
        when(categoryMongoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(categoryMapper.toDomain(savedEntity)).thenReturn(expectedCategory);

        // Act
        Category result = categoryRepositoryAdapter.create(inputCategory);

        // Assert
        assertAll(
                () -> assertEquals(expectedCategory.getId(), result.getId()),
                () -> assertEquals(expectedCategory.getTitle(), result.getTitle()),
                () -> assertEquals(expectedCategory.getStoreId(), result.getStoreId()),
                () -> assertNotNull(result.getCreatedAt()),
                () -> assertNotNull(result.getUpdatedAt())
        );
        verify(categoryMapper).toEntity(inputCategory);
        verify(categoryMongoRepository).save(entityToSave);
        verify(categoryMapper).toDomain(savedEntity);
    }

    @Test
    void create_WithNullCategory_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> categoryRepositoryAdapter.create(null));
        verifyNoInteractions(categoryMapper);
        verifyNoInteractions(categoryMongoRepository);
    }

    @ParameterizedTest
    @MethodSource("provideCategoriesForDelete")
    void delete_WithValidCategory_DeletesEntity(Category category) {
        // Arrange
        CategoryMongoEntity entity = createTestEntity(
                category.getId(),
                category.getTitle(),
                category.getStoreId(),
                category.getCreatedAt()
        );
        when(categoryMapper.toEntity(category)).thenReturn(entity);

        // Act
        categoryRepositoryAdapter.delete(category);

        // Assert
        verify(categoryMapper).toEntity(category);
        verify(categoryMongoRepository).delete(entity);
    }

    @ParameterizedTest
    @MethodSource("provideTitleAndStoreIdForExists")
    void existsByTitleAndStoreId_WithVariousInputs_ReturnsExpectedResult(
            String title,
            String storeId,
            boolean expectedResult
    ) {
        // Arrange
        when(categoryMongoRepository.existsByTitleAndStoreId(title, storeId))
                .thenReturn(expectedResult);

        // Act
        boolean result = categoryRepositoryAdapter.existsByTitleAndStoreId(title, storeId);

        // Assert
        assertEquals(expectedResult, result);
        verify(categoryMongoRepository).existsByTitleAndStoreId(title, storeId);
    }
}