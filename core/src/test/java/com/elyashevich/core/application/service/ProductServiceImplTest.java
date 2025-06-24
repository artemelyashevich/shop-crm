package com.elyashevich.core.application.service;

import com.elyashevich.core.application.port.out.ProductRepository;
import com.elyashevich.core.domain.exception.ResourceNotFoundException;
import com.elyashevich.core.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private static Stream<Arguments> provideProducts() {
        return Stream.of(
                Arguments.of(
                        "Multiple products",
                        List.of(
                                createTestProduct("1", "Product 1", "store-1", "category-1"),
                                createTestProduct("2", "Product 2", "store-1", "category-2")
                        ),
                        2
                ),
                Arguments.of(
                        "Empty list",
                        List.of(),
                        0
                )
        );
    }

    private static Stream<Arguments> provideProductsByStore() {
        return Stream.of(
                Arguments.of(
                        "Store with products",
                        "store-1",
                        List.of(
                                createTestProduct("1", "Product 1", "store-1", "category-1"),
                                createTestProduct("2", "Product 2", "store-1", "category-2")
                        ),
                        2
                ),
                Arguments.of(
                        "Store without products",
                        "store-2",
                        List.of(),
                        0
                )
        );
    }

    private static Stream<Arguments> provideProductsByCategory() {
        return Stream.of(
                Arguments.of(
                        "Category with products",
                        "category-1",
                        List.of(
                                createTestProduct("1", "Product 1", "store-1", "category-1"),
                                createTestProduct("3", "Product 3", "store-2", "category-1")
                        ),
                        2
                ),
                Arguments.of(
                        "Category without products",
                        "category-3",
                        List.of(),
                        0
                )
        );
    }

    private static Stream<Arguments> provideProductFindByIdCases() {
        return Stream.of(
                Arguments.of(
                        "Existing product",
                        Optional.of(createTestProduct("1", "Product 1", "store-1", "category-1")),
                        "1",
                        false
                ),
                Arguments.of(
                        "Non-existing product",
                        Optional.empty(),
                        "999",
                        true
                )
        );
    }

    private static Stream<Arguments> provideProductUpdateCases() {
        return Stream.of(
                Arguments.of(
                        "Update all fields",
                        "1",
                        "Updated Product",
                        "New description",
                        2000,
                        List.of("image1.jpg", "image2.jpg"),
                        "category-2",
                        "color-2"
                ),
                Arguments.of(
                        "Partial update",
                        "2",
                        null,
                        "New description only",
                        null,
                        null,
                        null,
                        null
                )
        );
    }

    private static Product createTestProduct(String id, String title, String storeId, String categoryId) {
        return Product.builder()
                .id(id)
                .title(title)
                .storeId(storeId)
                .categoryId(categoryId)
                .price(BigDecimal.valueOf(1000))
                .description("Test description")
                .images(List.of("default.jpg"))
                .build();
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideProducts")
    @DisplayName("findAll should return all products")
    void findAll_ShouldReturnProducts(String description, List<Product> expectedProducts, int expectedSize) {
        when(productRepository.findAll()).thenReturn(expectedProducts);

        List<Product> result = productService.findAll();

        assertEquals(expectedSize, result.size());
        if (!expectedProducts.isEmpty()) {
            assertEquals(expectedProducts.get(0).getId(), result.get(0).getId());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideProductsByStore")
    @DisplayName("findByStoreId should return products for store")
    void findByStoreId_ShouldReturnProductsForStore(
            String description,
            String storeId,
            List<Product> expectedProducts,
            int expectedSize
    ) {
        when(productRepository.findByStoreId(storeId)).thenReturn(expectedProducts);

        List<Product> result = productService.findByStoreId(storeId);

        assertEquals(expectedSize, result.size());
        if (!expectedProducts.isEmpty()) {
            assertEquals(expectedProducts.get(0).getStoreId(), result.get(0).getStoreId());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideProductsByCategory")
    @DisplayName("findByCategoryId should return products for category")
    void findByCategoryId_ShouldReturnProductsForCategory(
            String description,
            String categoryId,
            List<Product> expectedProducts,
            int expectedSize
    ) {
        when(productRepository.findByCategoryId(categoryId)).thenReturn(expectedProducts);

        List<Product> result = productService.findByCategoryId(categoryId);

        assertEquals(expectedSize, result.size());
        if (!expectedProducts.isEmpty()) {
            assertEquals(expectedProducts.get(0).getCategoryId(), result.get(0).getCategoryId());
        }
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideProductFindByIdCases")
    @DisplayName("findById should handle cases correctly")
    void findById_ShouldHandleCases(
            String description,
            Optional<Product> mockResponse,
            String inputId,
            boolean shouldThrow
    ) {
        when(productRepository.findById(inputId)).thenReturn(mockResponse);

        if (shouldThrow) {
            ResourceNotFoundException exception = assertThrows(
                    ResourceNotFoundException.class,
                    () -> productService.findById(inputId)
            );
            assertEquals(
                    String.format(ProductServiceImpl.PRODUCT_WITH_ID_NOT_FOUND_TEMPLATE, inputId),
                    exception.getMessage()
            );
        } else {
            Product result = productService.findById(inputId);
            assertEquals(mockResponse.get().getId(), result.getId());
        }
    }

    @Test
    @DisplayName("create should save new product")
    void create_ShouldSaveProduct() {
        Product newProduct = createTestProduct(null, "New Product", "store-1", "category-1");
        Product savedProduct = createTestProduct("123", "New Product", "store-1", "category-1");

        when(productRepository.create(newProduct)).thenReturn(savedProduct);

        Product result = productService.create(newProduct);

        assertNotNull(result.getId());
        assertEquals(savedProduct.getTitle(), result.getTitle());
        verify(productRepository).create(newProduct);
    }

    @ParameterizedTest
    @CsvSource({
            "product-1",
            "product-2",
            "product-3"
    })
    @DisplayName("delete should invoke repository delete")
    void delete_ShouldCallRepository(String productId) {
        Product existing = createTestProduct(productId, "ToDelete", "store-1", "category-1");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existing));
        doNothing().when(productRepository).delete(existing);

        assertDoesNotThrow(() -> productService.delete(productId));
        verify(productRepository).delete(existing);
    }

    @Test
    @DisplayName("delete should throw when product not found")
    void delete_NonExistingProduct_ShouldThrow() {
        String productId = "nonexistent";
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.delete(productId)
        );

        assertEquals(
                String.format(ProductServiceImpl.PRODUCT_WITH_ID_NOT_FOUND_TEMPLATE, productId),
                exception.getMessage()
        );
        verify(productRepository, never()).delete(any());
    }
}