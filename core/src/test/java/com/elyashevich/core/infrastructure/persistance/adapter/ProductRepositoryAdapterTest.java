package com.elyashevich.core.infrastructure.persistance.adapter;

import com.elyashevich.core.domain.model.Product;
import com.elyashevich.core.infrastructure.persistance.entity.CategoryMongoEntity;
import com.elyashevich.core.infrastructure.persistance.entity.ColorMongoEntity;
import com.elyashevich.core.infrastructure.persistance.entity.ProductMongoEntity;
import com.elyashevich.core.infrastructure.persistance.mapper.EntityMapper;
import com.elyashevich.core.infrastructure.persistance.repository.CategoryMongoRepository;
import com.elyashevich.core.infrastructure.persistance.repository.ColorMongoRepository;
import com.elyashevich.core.infrastructure.persistance.repository.ProductMongoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
class ProductRepositoryAdapterTest {

    @Mock
    private ProductMongoRepository productMongoRepository;

    @Mock
    private EntityMapper<Product, ProductMongoEntity> productMapper;

    @Mock
    private CategoryMongoRepository categoryMongoRepository;

    @Mock
    private ColorMongoRepository colorMongoRepository;

    @InjectMocks
    private ProductRepositoryAdapter productRepositoryAdapter;

    // Test data providers
    private static Stream<Arguments> provideProductsForFindAll() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        List.of(
                                createTestEntity("prod-1", "Product 1", "store-1", now.minusDays(1)),
                                createTestEntity("prod-2", "Product 2", "store-2", now.minusHours(1))
                        ),
                        List.of(
                                createTestProduct("prod-1", "Product 1", "store-1", now.minusDays(1)),
                                createTestProduct("prod-2", "Product 2", "store-2", now.minusHours(1))
                        )
                ),
                Arguments.of(
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );
    }

    private static Stream<Arguments> provideProductsForFindByStoreId() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        "store-1",
                        List.of(
                                createTestEntity("prod-1", "Product 1", "store-1", now.minusDays(1)),
                                createTestEntity("prod-2", "Product 2", "store-1", now.minusHours(1))
                        ),
                        List.of(
                                createTestProduct("prod-1", "Product 1", "store-1", now.minusDays(1)),
                                createTestProduct("prod-2", "Product 2", "store-1", now.minusHours(1))
                        )
                ),
                Arguments.of(
                        "empty-store",
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );
    }

    private static Stream<Arguments> provideProductsForFindByCategoryId() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        "category-1",
                        List.of(
                                createTestEntityWithCategory("prod-1", "Product 1", "category-1", now.minusDays(1)),
                                createTestEntityWithCategory("prod-2", "Product 2", "category-1", now.minusHours(1))
                        ),
                        List.of(
                                createTestProductWithCategory("prod-1", "Product 1", "category-1", now.minusDays(1)),
                                createTestProductWithCategory("prod-2", "Product 2", "category-1", now.minusHours(1))
                        )
                ),
                Arguments.of(
                        "empty-category",
                        Collections.emptyList(),
                        Collections.emptyList()
                )
        );
    }

    private static Stream<Arguments> provideProductsForFindById() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        "existing-id",
                        Optional.of(createTestEntity("existing-id", "Existing Product", "store-1", now)),
                        Optional.of(createTestProduct("existing-id", "Existing Product", "store-1", now))
                ),
                Arguments.of(
                        "non-existing-id",
                        Optional.empty(),
                        Optional.empty()
                )
        );
    }

    private static Stream<Arguments> provideProductsForCreate() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(
                        createTestProduct(null, "New Product", "store-1", "category-1", "color-1", null),
                        createTestEntity(null, "New Product", "store-1", now),
                        createTestEntity("saved-id", "New Product", "store-1", now),
                        createTestProduct("saved-id", "New Product", "store-1", "category-1", "color-1", now)
                ),
                Arguments.of(
                        createTestProduct("existing-id", "Existing Product", "store-1", null, null, now.minusDays(1)),
                        createTestEntity("existing-id", "Existing Product", "store-1", now),
                        createTestEntity("existing-id", "Existing Product", "store-1", now),
                        createTestProduct("existing-id", "Existing Product", "store-1", null, null, now)
                )
        );
    }

    private static Stream<Arguments> provideProductsForDelete() {
        LocalDateTime now = LocalDateTime.now();
        return Stream.of(
                Arguments.of(createTestProduct("prod-1", "To Delete", "store-1", "category-1", "color-1", now)),
                Arguments.of(createTestProduct("prod-2", "Another Delete", "store-2", null, null, now))
        );
    }

    // Helper methods for creating test data
    private static ProductMongoEntity createTestEntity(String id, String title, String storeId, LocalDateTime createdAt) {
        return ProductMongoEntity.builder()
                .id(id)
                .title(title)
                .storeId(storeId)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .price(BigDecimal.valueOf(9.99))
                .images(new ArrayList<>())
                .build();
    }

    private static ProductMongoEntity createTestEntityWithCategory(String id, String title, String categoryId, LocalDateTime createdAt) {
        ProductMongoEntity entity = createTestEntity(id, title, "store-1", createdAt);
        entity.setCategory(new CategoryMongoEntity());
        entity.getCategory().setId(categoryId);
        return entity;
    }

    private static Product createTestProduct(String id, String title, String storeId, LocalDateTime createdAt) {
        return Product.builder()
                .id(id)
                .title(title)
                .storeId(storeId)
                .createdAt(createdAt)
                .updatedAt(createdAt)
                .price(BigDecimal.valueOf(9.99))
                .images(new ArrayList<>())
                .build();
    }

    private static Product createTestProduct(String id, String title, String storeId, String categoryId, String colorId, LocalDateTime createdAt) {
        Product product = createTestProduct(id, title, storeId, createdAt);
        product.setCategoryId(categoryId);
        product.setColorId(colorId);
        return product;
    }

    private static Product createTestProductWithCategory(String id, String title, String categoryId, LocalDateTime createdAt) {
        Product product = createTestProduct(id, title, "store-1", createdAt);
        product.setCategoryId(categoryId);
        return product;
    }

    @ParameterizedTest
    @MethodSource("provideProductsForFindAll")
    void findAll_WithVariousData_ReturnsAllProducts(
            List<ProductMongoEntity> entities,
            List<Product> expectedProducts
    ) {
        // Arrange
        when(productMongoRepository.findAll()).thenReturn(entities);
        when(productMapper.toDomain(entities)).thenReturn(expectedProducts);

        // Act
        List<Product> result = productRepositoryAdapter.findAll();

        // Assert
        assertEquals(expectedProducts, result);
        verify(productMongoRepository).findAll();
        verify(productMapper).toDomain(entities);
    }

    @ParameterizedTest
    @MethodSource("provideProductsForFindByStoreId")
    void findByStoreId_WithVariousStoreIds_ReturnsMatchingProducts(
            String storeId,
            List<ProductMongoEntity> entities,
            List<Product> expectedProducts
    ) {
        // Arrange
        when(productMongoRepository.findByStoreId(storeId)).thenReturn(entities);
        when(productMapper.toDomain(entities)).thenReturn(expectedProducts);

        // Act
        List<Product> result = productRepositoryAdapter.findByStoreId(storeId);

        // Assert
        assertEquals(expectedProducts, result);
        verify(productMongoRepository).findByStoreId(storeId);
        verify(productMapper).toDomain(entities);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void findByStoreId_WithNullOrEmptyStoreId_ReturnsEmptyList(String invalidStoreId) {
        // Arrange
        when(productMongoRepository.findByStoreId(invalidStoreId)).thenReturn(Collections.emptyList());
        when(productMapper.toDomain(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productRepositoryAdapter.findByStoreId(invalidStoreId);

        // Assert
        assertTrue(result.isEmpty());
        verify(productMongoRepository).findByStoreId(invalidStoreId);
        verify(productMapper).toDomain(Collections.emptyList());
    }

    @ParameterizedTest
    @MethodSource("provideProductsForFindByCategoryId")
    void findByCategoryId_WithVariousCategoryIds_ReturnsMatchingProducts(
            String categoryId,
            List<ProductMongoEntity> entities,
            List<Product> expectedProducts
    ) {
        // Arrange
        when(productMongoRepository.findByCategoryId(categoryId)).thenReturn(entities);
        when(productMapper.toDomain(entities)).thenReturn(expectedProducts);

        // Act
        List<Product> result = productRepositoryAdapter.findByCategoryId(categoryId);

        // Assert
        assertEquals(expectedProducts, result);
        verify(productMongoRepository).findByCategoryId(categoryId);
        verify(productMapper).toDomain(entities);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void findByCategoryId_WithNullOrEmptyCategoryId_ReturnsEmptyList(String invalidCategoryId) {
        // Arrange
        when(productMongoRepository.findByCategoryId(invalidCategoryId)).thenReturn(Collections.emptyList());
        when(productMapper.toDomain(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productRepositoryAdapter.findByCategoryId(invalidCategoryId);

        // Assert
        assertTrue(result.isEmpty());
        verify(productMongoRepository).findByCategoryId(invalidCategoryId);
        verify(productMapper).toDomain(Collections.emptyList());
    }

    @ParameterizedTest
    @MethodSource("provideProductsForFindById")
    void findById_WithVariousIds_ReturnsMatchingProduct(
            String id,
            Optional<ProductMongoEntity> entity,
            Optional<Product> expectedProduct
    ) {
        // Arrange
        when(productMongoRepository.findById(id)).thenReturn(entity);
        if (entity.isPresent()) {
            when(productMapper.toDomain(entity.get())).thenReturn(expectedProduct.get());
        }

        // Act
        Optional<Product> result = productRepositoryAdapter.findById(id);

        // Assert
        assertEquals(expectedProduct, result);
        verify(productMongoRepository).findById(id);
        if (entity.isPresent()) {
            verify(productMapper).toDomain(entity.get());
        }
    }

    @ParameterizedTest
    @NullAndEmptySource
    void findById_WithNullOrEmptyId_ReturnsEmptyOptional(String invalidId) {
        // Act
        Optional<Product> result = productRepositoryAdapter.findById(invalidId);

        // Assert
        assertTrue(result.isEmpty());
        verify(productMapper, never()).toDomain((ProductMongoEntity) any());
    }

    @ParameterizedTest
    @MethodSource("provideProductsForCreate")
    @Transactional
    void create_WithVariousProducts_ReturnsSavedProduct(
            Product inputProduct,
            ProductMongoEntity entityToSave,
            ProductMongoEntity savedEntity,
            Product expectedProduct
    ) {
        // Arrange
        when(productMapper.toEntity(inputProduct)).thenReturn(entityToSave);
        when(productMongoRepository.save(entityToSave)).thenReturn(savedEntity);
        when(productMapper.toDomain(savedEntity)).thenReturn(expectedProduct);

        if (inputProduct.getCategoryId() != null) {
            when(categoryMongoRepository.findById(inputProduct.getCategoryId()))
                    .thenReturn(Optional.of(new CategoryMongoEntity()));
        }
        if (inputProduct.getColorId() != null) {
            when(colorMongoRepository.findById(inputProduct.getColorId()))
                    .thenReturn(Optional.of(new ColorMongoEntity()));
        }

        // Act
        Product result = productRepositoryAdapter.create(inputProduct);

        // Assert
        assertAll(
                () -> assertEquals(expectedProduct.getId(), result.getId()),
                () -> assertEquals(expectedProduct.getTitle(), result.getTitle()),
                () -> assertEquals(expectedProduct.getStoreId(), result.getStoreId()),
                () -> assertNotNull(result.getCreatedAt()),
                () -> assertNotNull(result.getUpdatedAt())
        );
        verify(productMapper).toEntity(inputProduct);
        verify(productMongoRepository).save(entityToSave);
        verify(productMapper).toDomain(savedEntity);

        if (inputProduct.getCategoryId() != null) {
            verify(categoryMongoRepository).findById(inputProduct.getCategoryId());
        }
        if (inputProduct.getColorId() != null) {
            verify(colorMongoRepository).findById(inputProduct.getColorId());
        }
    }

    @Test
    @Transactional
    void create_WithNullProduct_ThrowsNullPointerException() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> productRepositoryAdapter.create(null));
        verifyNoInteractions(productMapper);
        verifyNoInteractions(productMongoRepository);
        verifyNoInteractions(categoryMongoRepository);
        verifyNoInteractions(colorMongoRepository);
    }

    @ParameterizedTest
    @MethodSource("provideProductsForDelete")
    void delete_WithValidProduct_DeletesEntity(Product product) {
        // Arrange
        ProductMongoEntity entity = createTestEntity(
                product.getId(),
                product.getTitle(),
                product.getStoreId(),
                product.getCreatedAt()
        );
        when(productMapper.toEntity(product)).thenReturn(entity);

        // Act
        productRepositoryAdapter.delete(product);

        // Assert
        verify(productMapper).toEntity(product);
        verify(productMongoRepository).delete(entity);
    }
}